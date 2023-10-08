#### springboot RabbitMQ客户端连接故障恢复

> 最近做RabbitMQ故障演练发现RabbitMQ服务器停止后，基于springboot的消费端不可以自动的恢复，队列的消费者消失，消息一直积压到队列中，这种情况肯定是不可接收的；通过研究源代码找到了解决方案。

##### 一、添加自动恢复配置automaticRecovery

```java
 CachingConnectionFactory factory = new CachingConnectionFactory(connectionFactory);
cachingConnectionFactoryConfigurer.configure(factory);

//设置TCP连接超时时间，默认：60000ms
factory.getRabbitConnectionFactory().setConnectionTimeout(properties.getConnectionTimeout());
//启用或禁用连接自动恢复，默认：false
factory.getRabbitConnectionFactory().setAutomaticRecoveryEnabled(properties.isAutomaticRecovery());
//设置连接恢复时间间隔，默认：5000ms
factory.getRabbitConnectionFactory().setNetworkRecoveryInterval(properties.getNetworkRecoveryInterval());
//启用或禁用拓扑恢复，默认：true【拓扑恢复功能可以帮助消费者重新声明之前定义的队列、交换机和绑定等拓扑结构】
factory.getRabbitConnectionFactory().setTopologyRecoveryEnabled(properties.isTopologyRecovery());
//替换默认异常处理DefaultExceptionHandler
factory.getRabbitConnectionFactory().setExceptionHandler(new DefaultMqExceptionHandler());
//添加连接监听器
factory.addConnectionListener(new DefaultMqConnectionListener(factory));
```

> 通过上述配置如果RabbitMQ服务器发生故障，则会自动重启恢复连接及队列的消费者，如果恢复失败则会间隔5000ms再次重试；在这里提一个问题，如果服务重试一直失败，重试的上限是多少？带着这个问题我们分析下源码。

##### 二、RabbitMQ客户端实现连接的自动恢复功能

AutorecoveringConnection#beginAutomaticRecovery是在 RabbitMQ 客户端库层面实现的连接的自动恢复功能。当 RabbitMQ 连接出现故障时，它会尝试重新建立连接，以确保消息传递的可靠性。

```java
  private synchronized void beginAutomaticRecovery() throws InterruptedException {
        //获取故障恢复连接的间隔时间，实际是设置的：networkRecoveryInterval
        final long delay = this.params.getRecoveryDelayHandler().getDelay(0);
        if (delay > 0)  {
           //等待指定的间隔时间
            this.wait(delay);
        }
				//调用恢复通知监听器
        this.notifyRecoveryListenersStarted();
				//获取恢复建立的连接对象
        final RecoveryAwareAMQConnection newConn = this.recoverConnection();
        //如果为null则直接返回
        if (newConn == null) {
            return;
        }
    		//连接已经恢复建立，恢复监听器、channel等资源
        LOGGER.debug("Connection {} has recovered", newConn);
        this.addAutomaticRecoveryListener(newConn);
        this.recoverShutdownListeners(newConn);
        this.recoverBlockedListeners(newConn);
        this.recoverChannels(newConn);
        // don't assign new delegate connection until channel recovery is complete
        this.delegate = newConn;
       //判断是否恢复拓扑结构，如果开启则开启拓扑结构恢复
        if (this.params.isTopologyRecoveryEnabled()) {
            notifyTopologyRecoveryListenersStarted();
            recoverTopology(params.getTopologyRecoveryExecutor());
        }
      	this.notifyRecoveryListenersComplete();
    }
```

addAutomaticRecoveryListener自动恢复监听器

```java
private void addAutomaticRecoveryListener(final RecoveryAwareAMQConnection newConn) {
    final AutorecoveringConnection c = this;
    // this listener will run after shutdown listeners,
    // see https://github.com/rabbitmq/rabbitmq-java-client/issues/135
    RecoveryCanBeginListener starter = cause -> {
        try {
            if (shouldTriggerConnectionRecovery(cause)) {
                //开始自动回复
                c.beginAutomaticRecovery();
            }
        } catch (Exception e) {
            newConn.getExceptionHandler().handleConnectionRecoveryException(c, e);
        }
    };
    synchronized (this) {
        newConn.addRecoveryCanBeginListener(starter);
    }
}
```

init初始化

```java
public void init() throws IOException, TimeoutException {
    //建立连接，否则抛出异常
    this.delegate = this.cf.newConnection();
    //自动回复监听器
    this.addAutomaticRecoveryListener(delegate);
}
```

##### 三、消费端实现消息的消费和处理

SimpleMessageListenerContainer.AsyncMessageProcessingConsumer#run是应用程序层面实现消息的消费和处理，它负责从RabbitMQ中接收消息并进行相应的逻辑处理：

```java
@Override // NOSONAR - complexity - many catch blocks
public void run() { // NOSONAR - line count
  ...

  try {
    //消费端初始化方法
    initialize();
    //当消费端是活跃状态，或者队列非空，或者消费端未被关闭则进入主循环
    while (isActive(this.consumer) || this.consumer.hasDelivery() || !this.consumer.cancelled()) {
      mainLoop();
    }
  }
  catch (InterruptedException e) {
    ...
    }
}

```

消费端initialize初始化方法：

```java
	private void initialize() throws Throwable { // NOSONAR
			try {
				redeclareElementsIfNecessary();
        //启动消费端初始化
				this.consumer.start();
				this.start.countDown();
			}
			catch (QueuesNotAvailableException e) {
				if (isMissingQueuesFatal()) {
					throw e;
				}
				else {
					this.start.countDown();
          //消费端启动异常等待处理
					handleStartupFailure(this.consumer.getBackOffExecution());
					throw e;
				}
			}
			catch (FatalListenerStartupException ex) {
				if (isPossibleAuthenticationFailureFatal()) {
					throw ex;
				}
				else {
					Throwable possibleAuthException = ex.getCause().getCause();
					if (!(possibleAuthException instanceof PossibleAuthenticationFailureException)) {
						throw ex;
					}
					else {
						this.start.countDown();
            //消费端启动异常等待处理
						handleStartupFailure(this.consumer.getBackOffExecution());
						throw possibleAuthException;
					}
				}
			}
			catch (Throwable t) { //NOSONAR
				this.start.countDown();
        //消费端启动异常等待处理
				handleStartupFailure(this.consumer.getBackOffExecution());
				throw t;
			}

			if (getTransactionManager() != null) {
				/*
				 * Register the consumer's channel so it will be used by the transaction manager
				 * if it's an instance of RabbitTransactionManager.
				 */
				ConsumerChannelRegistry.registerConsumerChannel(this.consumer.getChannel(), getConnectionFactory());
			}
		}
```

消费端异常等待处理处理：

```java
protected void handleStartupFailure(BackOffExecution backOffExecution) {
   //获取等待时间间隔，参考FixedBackOff类实现
		long recoveryInterval = backOffExecution.nextBackOff();
		if (BackOffExecution.STOP == recoveryInterval) {
			synchronized (this) {
				if (isActive()) {
					logger.warn("stopping container - restart recovery attempts exhausted");
					stop();
				}
			}
			return;
		}
		try {
			if (logger.isDebugEnabled() && isActive()) {
				logger.debug("Recovering consumer in " + recoveryInterval + " ms.");
			}
      //当前时间加上等待时间
			long timeout = System.currentTimeMillis() + recoveryInterval;
      //如果当前时间小于等待时间，则休眠200毫秒，再次尝试
			while (isActive() && System.currentTimeMillis() < timeout) {
				Thread.sleep(RECOVERY_LOOP_WAIT_TIME);
			}
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("Irrecoverable interruption on consumer restart", e);
		}
	}
```

FixedBackOff回退等待时间类实现：

```java
public class FixedBackOff implements BackOff {
   // 默认恢复重试间隔
    public static final long DEFAULT_INTERVAL = 5000L;
   //最大重试次数，可以认为无限大
    public static final long UNLIMITED_ATTEMPTS = Long.MAX_VALUE;
   // 默认恢复重试间隔
    private long interval = 5000L;
   //最大重试次数，可以认为无限大
    private long maxAttempts = Long.MAX_VALUE;

    public FixedBackOff() {
    }

    public FixedBackOff(long interval, long maxAttempts) {
        this.interval = interval;
        this.maxAttempts = maxAttempts;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getInterval() {
        return this.interval;
    }

    public void setMaxAttempts(long maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public long getMaxAttempts() {
        return this.maxAttempts;
    }

    public BackOffExecution start() {
        return new FixedBackOffExecution();
    }

    private class FixedBackOffExecution implements BackOffExecution {
        private long currentAttempts;

        private FixedBackOffExecution() {
            this.currentAttempts = 0L;
        }
				//获取下一次尝试的时间间隔，可以认为一直都是5000ms
        public long nextBackOff() {
            ++this.currentAttempts;
            return this.currentAttempts <= FixedBackOff.this.getMaxAttempts() ? FixedBackOff.this.getInterval() : -1L;
        }

        public String toString() {
            String attemptValue = FixedBackOff.this.maxAttempts == Long.MAX_VALUE ? "unlimited" : String.valueOf(FixedBackOff.this.maxAttempts);
            return "FixedBackOff{interval=" + FixedBackOff.this.interval + ", currentAttempts=" + this.currentAttempts + ", maxAttempts=" + attemptValue + '}';
        }
    }
}

```

> 总结：综上源码分析可知消费端故障恢复重试等待时间是5000ms,重试次数可以认为是无限制（Long最大值）

mainloop主循环逻辑：

```java
		private void mainLoop() throws Exception { // NOSONAR Exception
			try {
				if (SimpleMessageListenerContainer.this.stopNow.get()) {
					this.consumer.forceCloseAndClearQueue();
					return;
				}
        //接收客户端发送过来的消息，至少获取一条
				boolean receivedOk = receiveAndExecute(this.consumer); // At least one message received
				if (SimpleMessageListenerContainer.this.maxConcurrentConsumers != null) {
					checkAdjust(receivedOk);
				}
				long idleEventInterval = getIdleEventInterval();
				if (idleEventInterval > 0) {
					if (receivedOk) {
						updateLastReceive();
					}
					else {
						long now = System.currentTimeMillis();
						long lastAlertAt = SimpleMessageListenerContainer.this.lastNoMessageAlert.get();
						long lastReceive = getLastReceive();
						if (now > lastReceive + idleEventInterval
								&& now > lastAlertAt + idleEventInterval
								&& SimpleMessageListenerContainer.this.lastNoMessageAlert
								.compareAndSet(lastAlertAt, now)) {
							publishIdleContainerEvent(now - lastReceive);
						}
					}
				}
			}
			catch (ListenerExecutionFailedException ex) {
				// Continue to process, otherwise re-throw
				if (ex.getCause() instanceof NoSuchMethodException) {
					throw new FatalListenerExecutionException("Invalid listener", ex);
				}
			}
			catch (AmqpRejectAndDontRequeueException rejectEx) {
				/*
				 *  These will normally be wrapped by an LEFE if thrown by the
				 *  listener, but we will also honor it if thrown by an
				 *  error handler.
				 */
			}
		}
```

receiveAndExecute接收和处理消息：

```java
private boolean receiveAndExecute(final BlockingQueueConsumer consumer) throws Exception { // NOSONAR

		PlatformTransactionManager transactionManager = getTransactionManager();
		if (transactionManager != null) {
			try {
				if (this.transactionTemplate == null) {
					this.transactionTemplate =
							new TransactionTemplate(transactionManager, getTransactionAttribute());
				}
				return this.transactionTemplate
						.execute(status -> { // NOSONAR null never returned
							RabbitResourceHolder resourceHolder = ConnectionFactoryUtils.bindResourceToTransaction(
									new RabbitResourceHolder(consumer.getChannel(), false),
									getConnectionFactory(), true);
							// unbound in ResourceHolderSynchronization.beforeCompletion()
							try {
                //接收处理消息
								return doReceiveAndExecute(consumer);
							}
							catch (RuntimeException e1) {
								prepareHolderForRollback(resourceHolder, e1);
								throw e1;
							}
							catch (Exception e2) {
								throw new WrappedTransactionException(e2);
							}
						});
			}
			catch (WrappedTransactionException e) { // NOSONAR exception flow control
				throw (Exception) e.getCause();
			}
		}
		//接收处理消息
		return doReceiveAndExecute(consumer);

	}
```

调用具体的消息监听器消费消息：

```java
	private void doExecuteListener(Channel channel, Object data) {
		if (data instanceof Message) {
			Message message = (Message) data;
			if (this.afterReceivePostProcessors != null) {
				for (MessagePostProcessor processor : this.afterReceivePostProcessors) {
					message = processor.postProcessMessage(message);
					if (message == null) {
						throw new ImmediateAcknowledgeAmqpException(
								"Message Post Processor returned 'null', discarding message");
					}
				}
			}
			if (this.deBatchingEnabled && this.batchingStrategy.canDebatch(message.getMessageProperties())) {
				this.batchingStrategy.deBatch(message, fragment -> invokeListener(channel, fragment));
			}
			else {
				invokeListener(channel, message);
			}
		}
		else {
			invokeListener(channel, data);
		}
	}
```



GitHub代码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)