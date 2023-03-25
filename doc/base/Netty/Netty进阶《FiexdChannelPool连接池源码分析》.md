#### Netty进阶《FiexdChannelPool连接池源码分析》

> ChannelPool接口的直接实现类是SimpleChannelPool，而SimpleChannelPool的实现类是FixedChannelPool，FixedChannelPool是ChannelPool的增强实现，允许设置最大并发连接数；

##### 一、了解枚举类AcquireTimeoutAction

```java
    public enum AcquireTimeoutAction {
        /**
         * 检测到超时时创建一个新链接
         */
        NEW,

        /**
         * 获取调用结果失败，Future返回一个TimeoutException异常
         */
        FAIL
    }
```

##### 二、FiexdChannelPool构造函数

```java
public FixedChannelPool(Bootstrap bootstrap,
                            ChannelPoolHandler handler,
                            ChannelHealthChecker healthCheck, AcquireTimeoutAction action,
                            final long acquireTimeoutMillis,
                            int maxConnections, int maxPendingAcquires,
                            boolean releaseHealthCheck, boolean lastRecentUsed) {
        //调用SimpleChannelPool的构造函数
        super(bootstrap, handler, healthCheck, releaseHealthCheck, lastRecentUsed);
       //最大并发连接数有效性检查
        checkPositive(maxConnections, "maxConnections");
        //获取连接挂起的最大数量，一旦超过，尝试获取将失败
        checkPositive(maxPendingAcquires, "maxPendingAcquires");
        if (action == null && acquireTimeoutMillis == -1) {
            timeoutTask = null;
            acquireTimeoutNanos = -1;
        } else if (action == null && acquireTimeoutMillis != -1) {
            throw new NullPointerException("action");
        } else if (action != null && acquireTimeoutMillis < 0) {
            throw new IllegalArgumentException("acquireTimeoutMillis: " + acquireTimeoutMillis + " (expected: >= 0)");
        } else {
            acquireTimeoutNanos = TimeUnit.MILLISECONDS.toNanos(acquireTimeoutMillis);
            switch (action) {
               //连接池获取连接失败，将会返回Future 超时异常
            case FAIL:
                timeoutTask = new TimeoutTask() {
                    @Override
                    public void onTimeout(AcquireTask task) {
                        // Fail the promise as we timed out.
                        task.promise.setFailure(new AcquireTimeoutException());
                    }
                };
                break;
                //新建连接
            case NEW:
                timeoutTask = new TimeoutTask() {
                    @Override
                    public void onTimeout(AcquireTask task) {
                        // Increment the acquire count and delegate to super to actually acquire a Channel which will
                        // create a new connection.
                        task.acquired();
                        //调用父类的新建方法
                        FixedChannelPool.super.acquire(task.promise);
                    }
                };
                break;
            default:
                throw new Error();
            }
        }
        executor = bootstrap.config().group().next();
        this.maxConnections = maxConnections;
        this.maxPendingAcquires = maxPendingAcquires;
    }
```

##### 三、SimpleChannelPool构造函数

```java
    public SimpleChannelPool(Bootstrap bootstrap, final ChannelPoolHandler handler, ChannelHealthChecker healthCheck,
                             boolean releaseHealthCheck, boolean lastRecentUsed) {
       //校验ChannelPoolHandler类型处理器有效性，为不同的池操作通知的ChannelPoolHandler
        this.handler = checkNotNull(handler, "handler");
      //校验ChannelHealthChecker类型处理器有效性，从ChannelPool中获取Channel时，将用于检查Channel是否仍然健康的健康检查器
        this.healthCheck = checkNotNull(healthCheck, "healthCheck");
      //如果此参数设置为true,将在提供给用户 之前检查通道运行情况；否则只在添加到连接池时检查有效性
        this.releaseHealthCheck = releaseHealthCheck;
        // Clone the original Bootstrap as we want to set our own handler
        this.bootstrap = checkNotNull(bootstrap, "bootstrap").clone();
      //此处会调用ChannelPoolHandler实现类的channelCreated方法对新创建的Channel进行初始化
        this.bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                assert ch.eventLoop().inEventLoop();
                handler.channelCreated(ch);
            }
        });
      //连接池中的Channel默认采用LIFO,如果是false则采用FIFO模式。
        this.lastRecentUsed = lastRecentUsed;
    }
```

##### 四、FixedChannelPool.super.acquire新建Channel方法

```java
 @Override
    public Future<Channel> acquire(final Promise<Channel> promise) {
        return acquireHealthyFromPoolOrNew(checkNotNull(promise, "promise"));
    }

    /**
     * Tries to retrieve healthy channel from the pool if any or creates a new channel otherwise.
     * @param promise the promise to provide acquire result.
     * @return future for acquiring a channel.
     */
    private Future<Channel> acquireHealthyFromPoolOrNew(final Promise<Channel> promise) {
        try {
            //从队列中获取Channel
            final Channel ch = pollChannel();
           //如果池中不存在Channel
            if (ch == null) {
                // No Channel left in the pool bootstrap a new Channel
                Bootstrap bs = bootstrap.clone();
                bs.attr(POOL_KEY, this);
               //发送请求建立一个新的Channel连接
                ChannelFuture f = connectChannel(bs);
                if (f.isDone()) {
                   //如果任务已经完成，通知设置连接
                    notifyConnect(f, promise);
                } else {
                    f.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            notifyConnect(future, promise);
                        }
                    });
                }
            } else {
              //如果池中存在队列
              //返回Channel注册到的EventLoop对象
                EventLoop loop = ch.eventLoop();
                if (loop.inEventLoop()) {
                  //对Channel做健康检查
                    doHealthCheck(ch, promise);
                } else {
                    loop.execute(new Runnable() {
                        @Override
                        public void run() {
                            doHealthCheck(ch, promise);
                        }
                    });
                }
            }
        } catch (Throwable cause) {
            promise.tryFailure(cause);
        }
        return promise;
    }
		//根据lastRecentUsed属性设置判定从池中是按照LIFO还是按照FIFO
    protected Channel pollChannel() {
        return lastRecentUsed ? deque.pollLast() : deque.pollFirst();
    }
```

##### 五、notifyConnect通知连接

```java
    private void notifyConnect(ChannelFuture future, Promise<Channel> promise) {
        Channel channel = null;
        try {
           //如果I/O操作成功
            if (future.isSuccess()) {
              //获取Channel对象
                channel = future.channel();
              //此处调用ChannelPoolHandler实现类的channelAcquired方法
                handler.channelAcquired(channel);
              //设置Channel对象并通知所有的监听器
                if (!promise.trySuccess(channel)) {
                    // Promise was completed in the meantime (like cancelled), just release the channel again
                    release(channel);
                }
            } else {
                promise.tryFailure(future.cause());
            }
        } catch (Throwable cause) {
            closeAndFail(channel, cause, promise);
        }
    }
```

##### 六、acquire获取Channel方法

```java
 @Override
    public Future<Channel> acquire(final Promise<Channel> promise) {
        try {
          //判定任务执行器是否已经注册到了EventLoop上
            if (executor.inEventLoop()) {
                acquire0(promise);
            } else {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        acquire0(promise);
                    }
                });
            }
        } catch (Throwable cause) {
            promise.tryFailure(cause);
        }
        return promise;
    }

    private void acquire0(final Promise<Channel> promise) {
        try {
            assert executor.inEventLoop();

            if (closed) {
                promise.setFailure(new IllegalStateException("FixedChannelPool was closed"));
                return;
            }
          //线程池并发数量是否小于最大并发量
            if (acquiredChannelCount.get() < maxConnections) {
                assert acquiredChannelCount.get() >= 0;

                // We need to create a new promise as we need to ensure the AcquireListener runs in the correct
                // EventLoop
                Promise<Channel> p = executor.newPromise();
                AcquireListener l = new AcquireListener(promise);
                l.acquired();
                p.addListener(l);
              //此处会调用父类的acquire方法获取或者新建Channel
                super.acquire(p);
            } else {
              //判定挂起等待的数量大于等于最大允许的挂起等待数量
                if (pendingAcquireCount >= maxPendingAcquires) {
                    tooManyOutstanding(promise);
                } else {
                    AcquireTask task = new AcquireTask(promise);
                    if (pendingAcquireQueue.offer(task)) {
                        ++pendingAcquireCount;

                        if (timeoutTask != null) {
                            task.timeoutFuture = executor.schedule(timeoutTask, acquireTimeoutNanos,
                                  TimeUnit.NANOSECONDS);
                        }
                    } else {
                        tooManyOutstanding(promise);
                    }
                }

                assert pendingAcquireCount > 0;
            }
        } catch (Throwable cause) {
            promise.tryFailure(cause);
        }
    }
```

##### 七、release释放连接到ChannelPool

```java
   @Override
    public Future<Void> release(final Channel channel, final Promise<Void> promise) {
        ObjectUtil.checkNotNull(promise, "promise");
        final Promise<Void> p = executor.newPromise();
      //此处会调用父类SimpleChannelPool的释放连接的方法
        super.release(channel, p.addListener(new FutureListener<Void>() {

            @Override
            public void operationComplete(Future<Void> future) {
                try {
                    assert executor.inEventLoop();

                    if (closed) {
                        // Since the pool is closed, we have no choice but to close the channel
                        channel.close();
                        promise.setFailure(new IllegalStateException("FixedChannelPool was closed"));
                        return;
                    }

                    if (future.isSuccess()) {
                        decrementAndRunTaskQueue();
                        promise.setSuccess(null);
                    } else {
                        Throwable cause = future.cause();
                        // Check if the exception was not because of we passed the Channel to the wrong pool.
                        if (!(cause instanceof IllegalArgumentException)) {
                            decrementAndRunTaskQueue();
                        }
                        promise.setFailure(future.cause());
                    }
                } catch (Throwable cause) {
                    promise.tryFailure(cause);
                }
            }
        }));
        return promise;
    }
```



源码参考：[https://github.com/mingyang66/SkyDb](https://github.com/mingyang66/SkyDb)

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)