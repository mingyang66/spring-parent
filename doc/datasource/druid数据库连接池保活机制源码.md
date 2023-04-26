#### druid数据库连接池保活机制源码

##### druid连接池未设置保活机制时如何对连接有效性保护？

1. 如果连接发生了致命性异常，则会加入保活连接数组，接下来校验有效性；
2. 如果设置了物理连接超时时间，并且连接的空闲时间大于设置的物理连接超时时间，则会加入驱逐连接数组；
3. 如果连接的空闲时间小于最小驱逐空闲时间，并且小于保活检查间隔时间，则继续等待下一轮检查；
4. 如果连接的空闲时间大于最小驱逐空闲时间，并且轮询索引小于合并计数器，则将该连接放入驱逐连接数组；
5. 如果连接的空闲时间大于最大驱逐空闲时间，则将连接放入驱逐连接数组；

> 未启用保活机制剔除后连接池中的连接不一定能够保证真实有效，如果防火墙关闭连接，或者其他不知情的场景关闭了连接，则可能导致连接的无效；也就是说不会对连接进行有效性检查；

##### druid开启保活机制

如果设置了保活机制，并且空闲时间大于保活检查间隔时间，则将连接放入保活连接数组；接下来会对保活连接数组中的连接进行有效性检查

> 启用保活机制后会对连接池中符合条件的连接进行连接有效性检查，确保连接的有效性；

##### druid保活机制守护线程启动顺序如下：

1. com.alibaba.druid.pool.DruidDataSource#init
2. com.alibaba.druid.pool.DruidDataSource#createAndStartDestroyThread
3. com.alibaba.druid.pool.DruidDataSource.DestroyConnectionThread
4. com.alibaba.druid.pool.DruidDataSource.DestroyTask
5. com.alibaba.druid.pool.DruidDataSource#shrink(boolean, boolean)

##### DestroyConnectionThread守护线程：

```java
public class DestroyConnectionThread extends Thread {

        public DestroyConnectionThread(String name){
            super(name);
            this.setDaemon(true);
        }

        public void run() {
            initedLatch.countDown();

            for (;;) {
                // 从前面开始删除
                try {
                    if (closed || closing) {
                        break;
                    }
										//timeBetweenEvictionRunsMillis是触发心跳间隔时间，如果大于0则进入休眠时间，
                    //默认1分钟,1分钟进行一次心跳检查
                    if (timeBetweenEvictionRunsMillis > 0) {
                        Thread.sleep(timeBetweenEvictionRunsMillis);
                    } else {
                        Thread.sleep(1000); //
                    }

                    if (Thread.interrupted()) {
                        break;
                    }

                    destroyTask.run();
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

    }
```

> 程序启动后守护线程会进入无线循环，根据心跳间隔时间time-between-eviction-runs-millis循环调用DestoryTask线程；

##### DestoryTask线程源码：

```java
  public class DestroyTask implements Runnable {
        public DestroyTask() {

        }

        @Override
        public void run() {
            shrink(true, keepAlive);

            if (isRemoveAbandoned()) {
                removeAbandoned();
            }
        }

    }
```

##### shirnk方法核心代码

```java
    public void shrink(boolean checkTime, boolean keepAlive) {
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            return;
        }

        boolean needFill = false;
        //驱逐计数器
        int evictCount = 0;
        //保活计数器
        int keepAliveCount = 0;
        int fatalErrorIncrement = fatalErrorCount - fatalErrorCountLastShrink;
        fatalErrorCountLastShrink = fatalErrorCount;
        
        try {
            if (!inited) {
                return;
            }
						//合并计数器=数据库连接池中存储的连接数-最小空闲连接数（）
            final int checkCount = poolingCount - minIdle;
            final long currentTimeMillis = System.currentTimeMillis();
            for (int i = 0; i < poolingCount; ++i) {
                //从数据库连接池中取出连接句柄
                DruidConnectionHolder connection = connections[i];
								//（如果发生致命错误或者致命错误次数大于0）&& 最后致命错误时间大于当前连接的连接时间
                //则吧连接句柄添加到keepAliveConnections连接句柄数组，退出本次循环
                if ((onFatalError || fatalErrorIncrement > 0) && (lastFatalErrorTimeMillis > connection.connectTimeMillis))  {
                    //逻辑上判定为存活连接
                    keepAliveConnections[keepAliveCount++] = connection;
                    continue;
                }
								//上层出入true
                if (checkTime) {
                    //如果设置了物理连接超时参数，并且大于0（默认：-1）
                    if (phyTimeoutMillis > 0) {
                        //获取连接建立的时长
                        long phyConnectTimeMillis = currentTimeMillis - connection.connectTimeMillis;
                        //如果当前连接建立的物理连接时长大于参数设置的物理连接时长，则将连接加入evictConnections数组
                        if (phyConnectTimeMillis > phyTimeoutMillis) {
                            evictConnections[evictCount++] = connection;
                            continue;
                        }
                    }
										//当前连接空闲时长
                    long idleMillis = currentTimeMillis - connection.lastActiveTimeMillis;
										//空闲时间小于最小驱逐空闲时间并且空闲时间小于保活检查间隔时间
                    //则不进行保活校验
                    if (idleMillis < minEvictableIdleTimeMillis
                            && idleMillis < keepAliveBetweenTimeMillis
                    ) {
                        break;
                    }
									  //空闲时间大于等于最小驱逐空闲时间
                    //如果当前当前索引小于计数器，则添加到驱逐连接数组
                    if (idleMillis >= minEvictableIdleTimeMillis) {
                       //如果当前索引小于合并计数器，则将其加入驱逐数组
                        if (checkTime && i < checkCount) {
                            evictConnections[evictCount++] = connection;
                            continue;
                          //如果空闲时间大于最大驱逐空闲时间，则加入驱逐连接数组
                        } else if (idleMillis > maxEvictableIdleTimeMillis) {
                            evictConnections[evictCount++] = connection;
                            continue;
                        }
                    }
										//这里才是开启保活机制的开关
                    //如果开启保活机制并且空闲时间大于等于保活间隔时间，则加入报活连接数组
                    if (keepAlive && idleMillis >= keepAliveBetweenTimeMillis) {
                        keepAliveConnections[keepAliveCount++] = connection;
                    }
                } else {
                   //如果索引小于最小弃用数，则将其加入驱逐连接数组
                    if (i < checkCount) {
                        evictConnections[evictCount++] = connection;
                    } else {
                        break;
                    }
                }
            }
						//合并计数器=驱逐计数器+保活计数器
            int removeCount = evictCount + keepAliveCount;
           //如果合并计数器大于0
            if (removeCount > 0) {
                System.arraycopy(connections, removeCount, connections, 0, poolingCount - removeCount);
                Arrays.fill(connections, poolingCount - removeCount, poolingCount, null);
                poolingCount -= removeCount;
            }
            keepAliveCheckCount += keepAliveCount;

            if (keepAlive && poolingCount + activeCount < minIdle) {
                needFill = true;
            }
        } finally {
            lock.unlock();
        }
				//驱逐计数器大于0
        //关闭驱逐数组中的连接
        if (evictCount > 0) {
            for (int i = 0; i < evictCount; ++i) {
                DruidConnectionHolder item = evictConnections[i];
                Connection connection = item.getConnection();
                JdbcUtils.close(connection);
                destroyCountUpdater.incrementAndGet(this);
            }
            Arrays.fill(evictConnections, null);
        }
				//保活计数器大于0
        //对连接可用性进行校验，有效则重新加入队列，否则关闭连接
        if (keepAliveCount > 0) {
            // keep order
            for (int i = keepAliveCount - 1; i >= 0; --i) {
                DruidConnectionHolder holer = keepAliveConnections[i];
                Connection connection = holer.getConnection();
                holer.incrementKeepAliveCheckCount();

                boolean validate = false;
                try {
                   //校验连接的有效性
                    this.validateConnection(connection);
                    validate = true;
                } catch (Throwable error) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("keepAliveErr", error);
                    }
                    // skip
                }
							 //有效则加入连接池
                boolean discard = !validate;
                if (validate) {
                    holer.lastKeepTimeMillis = System.currentTimeMillis();
                    boolean putOk = put(holer, 0L, true);
                    if (!putOk) {
                        discard = true;
                    }
                }
								//无效则关闭连接
                if (discard) {
                    try {
                        connection.close();
                    } catch (Exception e) {
                        // skip
                    }

                    lock.lock();
                    try {
                        discardCount++;

                        if (activeCount + poolingCount <= minIdle) {
                            emptySignal();
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }
            this.getDataSourceStat().addKeepAliveCheckCount(keepAliveCount);
            Arrays.fill(keepAliveConnections, null);
        }

        if (needFill) {
            lock.lock();
            try {
                int fillCount = minIdle - (activeCount + poolingCount + createTaskCount);
                for (int i = 0; i < fillCount; ++i) {
                    emptySignal();
                }
            } finally {
                lock.unlock();
            }
        } else if (onFatalError || fatalErrorIncrement > 0) {
            lock.lock();
            try {
                emptySignal();
            } finally {
                lock.unlock();
            }
        }
    }
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)