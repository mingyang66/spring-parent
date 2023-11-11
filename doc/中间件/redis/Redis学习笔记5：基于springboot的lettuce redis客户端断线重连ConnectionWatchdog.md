#### Redis学习笔记5：基于springboot的lettuce redis客户端断线重连ConnectionWatchdog

> lettuce默认采用共享本地连接的模式和redis服务器端交互，如果连接断开如何及时发现并且重新建立连接呢？通过翻阅源码发现有两种方案，方案一：开启连接有效性检测；方案二：通过ConnectionWatchdog监视器

一个对springboot redis框架进行重写，支持lettuce、jedis、连接池、同时连接多个集群、多个redis数据库、开发自定义属性配置的开源SDK

```xml
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-redis</artifactId>
    <version>4.4.0</version>
</dependency>
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

##### 一、开启连接有效性validateConnection检测

- ##### LettuceConnectionFactory.SharedConnection#getConnection获取连接

```java
		@Nullable
		StatefulConnection<E, E> getConnection() {

			synchronized (this.connectionMonitor) {

				if (this.connection == null) {
          //建立本地连接
					this.connection = getNativeConnection();
				}
				//开启连接有效性检测
				if (getValidateConnection()) {
          //对获取到的连接进行ping有效性检测，如果无效则重置，重新建立连接
					validateConnection();
				}

				return this.connection;
			}
		}
		//连接有效性ping检查
		void validateConnection() {

			synchronized (this.connectionMonitor) {

				boolean valid = false;

				if (connection != null && connection.isOpen()) {
					try {

						if (connection instanceof StatefulRedisConnection) {
              //连接有效性ping检查
							((StatefulRedisConnection) connection).sync().ping();
						}

						if (connection instanceof StatefulRedisClusterConnection) {
              //连接有效性ping检查
							((StatefulRedisClusterConnection) connection).sync().ping();
						}
						valid = true;
					} catch (Exception e) {
						log.debug("Validation failed", e);
					}
				}
				//连接无效，则重置连接，并重新建立本地连接
				if (!valid) {

					log.info("Validation of shared connection failed; Creating a new connection.");
					resetConnection();
					this.connection = getNativeConnection();
				}
			}
		}
```

##### 二、ConnectionWatchdog断线重连监视器通过RedisClient初始化

- RedisClient#connectStatefulAsync方法开始对监视器初始化

```java
private <K, V, S> ConnectionFuture<S> connectStatefulAsync(StatefulRedisConnectionImpl<K, V> connection, Endpoint endpoint,
            RedisURI redisURI, Supplier<CommandHandler> commandHandlerSupplier) {

        ConnectionBuilder connectionBuilder;
        if (redisURI.isSsl()) {
            SslConnectionBuilder sslConnectionBuilder = SslConnectionBuilder.sslConnectionBuilder();
            sslConnectionBuilder.ssl(redisURI);
            connectionBuilder = sslConnectionBuilder;
        } else {
            connectionBuilder = ConnectionBuilder.connectionBuilder();
        }

        ConnectionState state = connection.getConnectionState();
        state.apply(redisURI);
        state.setDb(redisURI.getDatabase());

        connectionBuilder.connection(connection);
        connectionBuilder.clientOptions(getOptions());
        connectionBuilder.clientResources(getResources());
        connectionBuilder.commandHandler(commandHandlerSupplier).endpoint(endpoint);

        connectionBuilder(getSocketAddressSupplier(redisURI), connectionBuilder, connection.getConnectionEvents(), redisURI);
        connectionBuilder.connectionInitializer(createHandshake(state));
				//监视器初始化入口
        ConnectionFuture<RedisChannelHandler<K, V>> future = initializeChannelAsync(connectionBuilder);

        return future.thenApply(channelHandler -> (S) connection);
    }

```

- io.lettuce.core.AbstractRedisClient#initializeChannelAsync添加异常监听

```java
protected <K, V, T extends RedisChannelHandler<K, V>> ConnectionFuture<T> initializeChannelAsync(
            ConnectionBuilder connectionBuilder) {

        Mono<SocketAddress> socketAddressSupplier = connectionBuilder.socketAddress();

        if (clientResources.eventExecutorGroup().isShuttingDown()) {
            throw new IllegalStateException("Cannot connect, Event executor group is terminated.");
        }

        CompletableFuture<SocketAddress> socketAddressFuture = new CompletableFuture<>();
        CompletableFuture<Channel> channelReadyFuture = new CompletableFuture<>();

        String uriString = connectionBuilder.getRedisURI().toString();

        EventRecorder.getInstance().record(new ConnectionCreatedEvent(uriString, connectionBuilder.endpoint().getId()));
        EventRecorder.RecordableEvent event = EventRecorder.getInstance()
                .start(new ConnectEvent(uriString, connectionBuilder.endpoint().getId()));

        channelReadyFuture.whenComplete((channel, throwable) -> {
            event.record();
        });

// 通过对tcp建立异常监听        socketAddressSupplier.doOnError(socketAddressFuture::completeExceptionally).doOnNext(socketAddressFuture::complete)
                .subscribe(redisAddress -> {

                    if (channelReadyFuture.isCancelled()) {
                        return;
                    }
                    //初始化入口
                    initializeChannelAsync0(connectionBuilder, channelReadyFuture, redisAddress);
                }, channelReadyFuture::completeExceptionally);

        return new DefaultConnectionFuture<>(socketAddressFuture,
                channelReadyFuture.thenApply(channel -> (T) connectionBuilder.connection()));
    }
```

- io.lettuce.core.AbstractRedisClient#initializeChannelAsync0

- ```java
    private void initializeChannelAsync0(ConnectionBuilder connectionBuilder, CompletableFuture<Channel> channelReadyFuture,
              SocketAddress redisAddress) {
    
          logger.debug("Connecting to Redis at {}", redisAddress);
    
          Bootstrap redisBootstrap = connectionBuilder.bootstrap();
    				//初始化入口
          ChannelInitializer<Channel> initializer = connectionBuilder.build(redisAddress);
          redisBootstrap.handler(initializer);
    
          clientResources.nettyCustomizer().afterBootstrapInitialized(redisBootstrap);
          ChannelFuture connectFuture = redisBootstrap.connect(redisAddress);
    
          channelReadyFuture.whenComplete((c, t) -> {
    
              if (t instanceof CancellationException) {
                  connectFuture.cancel(true);
              }
          });
    
          connectFuture.addListener(future -> {
    
              Channel channel = connectFuture.channel();
              if (!future.isSuccess()) {
    
                  Throwable cause = future.cause();
                  Throwable detail = channel.attr(ConnectionBuilder.INIT_FAILURE).get();
    
                  if (detail != null) {
                      detail.addSuppressed(cause);
                      cause = detail;
                  }
    
                  logger.debug("Connecting to Redis at {}: {}", redisAddress, cause);
                  connectionBuilder.endpoint().initialState();
                  channelReadyFuture.completeExceptionally(cause);
                  return;
              }
    
              RedisHandshakeHandler handshakeHandler = channel.pipeline().get(RedisHandshakeHandler.class);
    
              if (handshakeHandler == null) {
                  channelReadyFuture.completeExceptionally(new IllegalStateException("RedisHandshakeHandler not registered"));
                  return;
              }
    
              handshakeHandler.channelInitialized().whenComplete((success, throwable) -> {
    
                  if (throwable == null) {
    
                      logger.debug("Connecting to Redis at {}: Success", redisAddress);
                      RedisChannelHandler<?, ?> connection = connectionBuilder.connection();
                      connection.registerCloseables(closeableResources, connection);
                      channelReadyFuture.complete(channel);
                      return;
                  }
    
                  logger.debug("Connecting to Redis at {}, initialization: {}", redisAddress, throwable);
                  connectionBuilder.endpoint().initialState();
                  channelReadyFuture.completeExceptionally(throwable);
              });
          });
      }
    
  ```

  - io.lettuce.core.ConnectionBuilder#build

  ```java
      public ChannelInitializer<Channel> build(SocketAddress socketAddress) {
          //初始化入口
          return new PlainChannelInitializer(this::buildHandlers, clientResources);
      }
  ```

  - io.lettuce.core.ConnectionBuilder#buildHandlers

  ```java
      protected List<ChannelHandler> buildHandlers() {
  
          LettuceAssert.assertState(channelGroup != null, "ChannelGroup must be set");
          LettuceAssert.assertState(connectionEvents != null, "ConnectionEvents must be set");
          LettuceAssert.assertState(connection != null, "Connection must be set");
          LettuceAssert.assertState(clientResources != null, "ClientResources must be set");
          LettuceAssert.assertState(endpoint != null, "Endpoint must be set");
          LettuceAssert.assertState(connectionInitializer != null, "ConnectionInitializer must be set");
  
          List<ChannelHandler> handlers = new ArrayList<>();
  
          connection.setOptions(clientOptions);
  
          handlers.add(new ChannelGroupListener(channelGroup, clientResources.eventBus()));
          handlers.add(new CommandEncoder());
          handlers.add(getHandshakeHandler());
          handlers.add(commandHandlerSupplier.get());
  
          handlers.add(new ConnectionEventTrigger(connectionEvents, connection, clientResources.eventBus()));
  				//如果自动重连机制打开
          if (clientOptions.isAutoReconnect()) {
              //新建连接监视器
              handlers.add(createConnectionWatchdog());
          }
  
          return handlers;
      }
  ```

  - ConnectionBuilder#createConnectionWatchdog创建连接监视器

  ```java
      protected ConnectionWatchdog createConnectionWatchdog() {
  				//如果连接监视器已经存在，则直接返回
          if (connectionWatchdog != null) {
              return connectionWatchdog;
          }
  
          LettuceAssert.assertState(bootstrap != null, "Bootstrap must be set for autoReconnect=true");
          LettuceAssert.assertState(socketAddressSupplier != null, "SocketAddressSupplier must be set for autoReconnect=true");
  				//创建连接监视器
          ConnectionWatchdog watchdog = new ConnectionWatchdog(clientResources.reconnectDelay(), clientOptions, bootstrap,
                  clientResources.timer(), clientResources.eventExecutorGroup(), socketAddressSupplier, reconnectionListener,
                  connection, clientResources.eventBus(), endpoint);
  				//将连接监视器注入到DefaultEndpoint对象中
          endpoint.registerConnectionWatchdog(watchdog);
  
          connectionWatchdog = watchdog;
          return watchdog;
      }
  ```

  > 到这里我们已经成功的将监视器对象ConnectionWatchdog在本地连接建立的时候注入到了DefaultEndpoint对象之中，DefaultEndpoint会开启对非活跃连接的监视。

  ##### 三、DefaultEndpoint#notifyChannelActive方法启用ConnectionWatchdog监听断开连接事件

  ```java
  @Override
      public void notifyChannelActive(Channel channel) {
  
          this.logPrefix = null;
          this.channel = channel;
          this.connectionError = null;
  
          if (isClosed()) {
  
              logger.info("{} Closing channel because endpoint is already closed", logPrefix());
              channel.close();
              return;
          }
          //监视器对象不为空，则启用监视器
          if (connectionWatchdog != null) {
              connectionWatchdog.arm();
          }
        ...
          });
      }
  ```
  
  

- ConnectionWatchdog#arm方法开启断连事件监听器

```java
    void arm() {
        //标记启用ConnectionWatchdog监视器
        this.armed = true;
        //设置标记在通道不活跃（连接断开）时监听事件
        setListenOnChannelInactive(true);
    }
```

- ConnectionWatchdog#channelInactive方法在连接的状态从活跃变为不活跃时会被调用，通常标识连接已经断开

```java
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        logger.debug("{} channelInactive()", logPrefix());
        if (!armed) {
            logger.debug("{} ConnectionWatchdog not armed", logPrefix());
            return;
        }

        channel = null;

        if (listenOnChannelInactive && !reconnectionHandler.isReconnectSuspended()) {
            //开启重连接调度
            scheduleReconnect();
        } else {
            logger.debug("{} Reconnect scheduling disabled", logPrefix(), ctx);
        }

        super.channelInactive(ctx);
    }

```

- ConnectionWatchdog#scheduleReconnect方法表示如果通道不可用或已经断开连接，则进行重连接

```java
    public void scheduleReconnect() {

        logger.debug("{} scheduleReconnect()", logPrefix());

        if (!isEventLoopGroupActive()) {
            logger.debug("isEventLoopGroupActive() == false");
            return;
        }

        if (!isListenOnChannelInactive()) {
            logger.debug("Skip reconnect scheduling, listener disabled");
            return;
        }

        if ((channel == null || !channel.isActive()) && reconnectSchedulerSync.compareAndSet(false, true)) {
						//重试次数
            attempts++;
            final int attempt = attempts;
            //延迟时间，根据具体策略来生成等待时间，最大等待30s
            Duration delay = reconnectDelay.createDelay(attempt);
            int timeout = (int) delay.toMillis();
            logger.debug("{} Reconnect attempt {}, delay {}ms", logPrefix(), attempt, timeout);

            this.reconnectScheduleTimeout = timer.newTimeout(it -> {

                reconnectScheduleTimeout = null;

                if (!isEventLoopGroupActive()) {
                    logger.warn("Cannot execute scheduled reconnect timer, reconnect workers are terminated");
                    return;
                }

                reconnectWorkers.submit(() -> {
                   //开启连接重建
                    ConnectionWatchdog.this.run(attempt, delay);
                    return null;
                });
            }, timeout, TimeUnit.MILLISECONDS);

            // Set back to null when ConnectionWatchdog#run runs earlier than reconnectScheduleTimeout's assignment.
            if (!reconnectSchedulerSync.get()) {
                reconnectScheduleTimeout = null;
            }
        } else {
            logger.debug("{} Skipping scheduleReconnect() because I have an active channel", logPrefix());
        }
    }
```

重试等待延迟时间规则如下：

```sh
第1次：PT0.001S
第2次：PT0.002S
第3次：PT0.004S
第4次：PT0.008S
第5次：PT0.016S
第6次：PT0.032S
第7次：PT0.064S
第8次：PT0.128S
第9次：PT0.256S
第10次：PT0.512S
第11次：PT1.024S
第12次：PT2.048S
第13次：PT4.096S
第14次：PT8.192S
第15次：PT16.384S
第16次：PT30S
第17次：PT30S
第18次：PT30S
第19次：PT30S
```



- ConnectionWatchdog#run连接重建

```java
private void run(int attempt, Duration delay) throws Exception {

        reconnectSchedulerSync.set(false);
        reconnectScheduleTimeout = null;

        if (!isEventLoopGroupActive()) {
            logger.debug("isEventLoopGroupActive() == false");
            return;
        }

        if (!isListenOnChannelInactive()) {
            logger.debug("Skip reconnect scheduling, listener disabled");
            return;
        }

        if (isReconnectSuspended()) {
            logger.debug("Skip reconnect scheduling, reconnect is suspended");
            return;
        }

        boolean shouldLog = shouldLog();

        InternalLogLevel infoLevel = InternalLogLevel.INFO;
        InternalLogLevel warnLevel = InternalLogLevel.WARN;

        if (shouldLog) {
            lastReconnectionLogging = System.currentTimeMillis();
        } else {
            warnLevel = InternalLogLevel.DEBUG;
            infoLevel = InternalLogLevel.DEBUG;
        }

        InternalLogLevel warnLevelToUse = warnLevel;

        try {
            reconnectionListener.onReconnectAttempt(new ConnectionEvents.Reconnect(attempt));
            eventBus.publish(new ReconnectAttemptEvent(redisUri, epid, LocalAddress.ANY, remoteAddress, attempt, delay));
            logger.log(infoLevel, "Reconnecting, last destination was {}", remoteAddress);
						//真正建立连接ReconnectionHandler
            Tuple2<CompletableFuture<Channel>, CompletableFuture<SocketAddress>> tuple = reconnectionHandler.reconnect();
            CompletableFuture<Channel> future = tuple.getT1();

            future.whenComplete((c, t) -> {

                if (c != null && t == null) {
                    return;
                }

                CompletableFuture<SocketAddress> remoteAddressFuture = tuple.getT2();
                SocketAddress remote = remoteAddress;
                if (remoteAddressFuture.isDone() && !remoteAddressFuture.isCompletedExceptionally()
                        && !remoteAddressFuture.isCancelled()) {
                    remote = remoteAddressFuture.join();
                }

                String message = String.format("Cannot reconnect to [%s]: %s", remote,
                        t.getMessage() != null ? t.getMessage() : t.toString());

                if (ReconnectionHandler.isExecutionException(t)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(message, t);
                    } else {
                        logger.log(warnLevelToUse, message);
                    }
                } else {
                    logger.log(warnLevelToUse, message, t);
                }
								//发送重连接失败事件
                eventBus.publish(new ReconnectFailedEvent(redisUri, epid, LocalAddress.ANY, remote, t, attempt));

                if (!isReconnectSuspended()) {
                    scheduleReconnect();
                }
            });
        } catch (Exception e) {
            logger.log(warnLevel, "Cannot reconnect: {}", e.toString());
            //发送重连接失败事件
            eventBus.publish(new ReconnectFailedEvent(redisUri, epid, LocalAddress.ANY, remoteAddress, e, attempt));
        }
    }
```

