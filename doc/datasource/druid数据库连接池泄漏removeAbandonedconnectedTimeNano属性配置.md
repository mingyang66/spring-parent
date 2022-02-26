#### druid数据库连接池泄漏removeAbandoned|connectedTimeNano属性配置

> 当程序存在缺陷时，申请的连接忘记关闭，这时候就存在连接泄漏了，druid提供了removeAbandanded相关配置，用来关闭长时间不适用的连接，removeAbandanded功能不建议再生产环境中使用，仅用于连接蟹柳检测诊断；

配置：

```properties
#连接池泄漏监测，当程序存在缺陷时，申请的连接忘记关闭，这时就存在连接泄漏了，开启后对性能有影响，建议生产关闭，默认：false
spring.emily.datasource.config.mysql.remove-abandoned=false
#默认：300*1000
spring.emily.datasource.config.mysql.remove-abandoned-timeout-millis=300000
#回收连接时打印日志，默认：false
spring.emily.datasource.config.mysql.log-abandoned=false

```

> 配置removeAbandanded对性能会有一些影响，建议怀疑泄漏之后在打开；上述配置是如何生效的呢？其实是在DestroyTask线程中根据生效；

##### com.alibaba.druid.pool.DruidDataSource.DestroyTask销毁守护线程：

```java
    public class DestroyTask implements Runnable {
        public DestroyTask() {

        }

        @Override
        public void run() {
          //此方法是数据库连接池中连接有效性维护，保活机制处理的核心方法，之前讲过
            shrink(true, keepAlive);
					//此处是根据removeAbandoned方法判定是否开启数据库连接池泄漏检查维护
            if (isRemoveAbandoned()) {
               //连接池泄漏核心方法
                removeAbandoned();
            }
        }

    }

```

com.alibaba.druid.pool.DruidDataSource#removeAbandoned方法处理连接池泄漏：

```java
   public int removeAbandoned() {
        int removeCount = 0;

        long currrentNanos = System.nanoTime();

        List<DruidPooledConnection> abandonedList = new ArrayList<DruidPooledConnection>();

        activeConnectionLock.lock();
        try {
          //活跃连接数
            Iterator<DruidPooledConnection> iter = activeConnections.keySet().iterator();

            for (; iter.hasNext();) {
                DruidPooledConnection pooledConnection = iter.next();
								//running属性标识当前连接正在被数据库CRUD操作使用，在CRUD之前会标记为true,之后无论失败成功都会标记为false,所以不会存在数据库连接泄漏问题
                if (pooledConnection.isRunning()) {
                    continue;
                }
								//获取连接空闲时间，其中connectedTimeNano属性是连接最后一次被使用的时间，会在连接getConnectionDirect调用的时候初始化，下面的文档会说明
                long timeMillis = (currrentNanos - pooledConnection.getConnectedTimeNano()) / (1000 * 1000);
								//如果连接空闲时间大于设置的连接池泄漏时间，则会被强制回收
                if (timeMillis >= removeAbandonedTimeoutMillis) {
                   //从活跃连接池中删除连接
                    iter.remove();
                   //设置线程池栈开启状态
                    pooledConnection.setTraceEnable(false);
                  //加入连接泄漏连接数组
                    abandonedList.add(pooledConnection);
                }
            }
        } finally {
            activeConnectionLock.unlock();
        }

        if (abandonedList.size() > 0) {
           //关闭连接泄漏的连接
            for (DruidPooledConnection pooledConnection : abandonedList) {
                final ReentrantLock lock = pooledConnection.lock;
                lock.lock();
                try {
                    if (pooledConnection.isDisable()) {
                        continue;
                    }
                } finally {
                    lock.unlock();
                }

                JdbcUtils.close(pooledConnection);
                pooledConnection.abandond();
                removeAbandonedCount++;
                removeCount++;
								//如果开启了打印连接泄漏日志，则会打印线程栈信息
                if (isLogAbandoned()) {
                    StringBuilder buf = new StringBuilder();
                    buf.append("abandon connection, owner thread: ");
                    buf.append(pooledConnection.getOwnerThread().getName());
                    buf.append(", connected at : ");
                    buf.append(pooledConnection.getConnectedTimeMillis());
                    buf.append(", open stackTrace\n");

                    StackTraceElement[] trace = pooledConnection.getConnectStackTrace();
                    for (int i = 0; i < trace.length; i++) {
                        buf.append("\tat ");
                        buf.append(trace[i].toString());
                        buf.append("\n");
                    }

                    buf.append("ownerThread current state is " + pooledConnection.getOwnerThread().getState()
                               + ", current stackTrace\n");
                    trace = pooledConnection.getOwnerThread().getStackTrace();
                    for (int i = 0; i < trace.length; i++) {
                        buf.append("\tat ");
                        buf.append(trace[i].toString());
                        buf.append("\n");
                    }

                    LOG.error(buf.toString());
                }
            }
        }

        return removeCount;
    }
```

所有的CRUD方法执行前都会调用com.alibaba.druid.pool.DruidPooledConnection#beforeExecute方法：

```java
    final void beforeExecute() {
        final DruidConnectionHolder holder = this.holder;
      //必须开启了连接泄漏检查才会标记为true
        if (holder != null && holder.dataSource.removeAbandoned) {
            running = true;
        }
    }
```

所有的CRUD方法执行前都会调用com.alibaba.druid.pool.DruidPooledConnection#afterExecute方法：

```java
    final void afterExecute() {
        final DruidConnectionHolder holder = this.holder;
        if (holder != null) {
            DruidAbstractDataSource dataSource = holder.dataSource;
          //只有开启了连接池泄漏检查才会标记为false
            if (dataSource.removeAbandoned) {
                running = false;
               //初始化最后的活跃时间
                holder.lastActiveTimeMillis = System.currentTimeMillis();
            }
            dataSource.onFatalError = false;
        }
    }
```

com.alibaba.druid.pool.DruidDataSource#getConnectionDirect方法会初始化connectedTimeNano属性：

```java
if (removeAbandoned) {
  StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
  //初始化线程栈
  poolableConnection.connectStackTrace = stackTrace;
  //初始化连接最后的使用时间
  poolableConnection.setConnectedTimeNano();
  //标记连接池栈开启状态
  poolableConnection.traceEnable = true;

  activeConnectionLock.lock();
  try {
    //将当前连接放入活跃连接数组中
    activeConnections.put(poolableConnection, PRESENT);
  } finally {
    activeConnectionLock.unlock();
  }
}
```



GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

