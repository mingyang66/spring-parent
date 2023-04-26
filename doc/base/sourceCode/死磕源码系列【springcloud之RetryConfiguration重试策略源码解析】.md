### 死磕源码系列【springcloud之RetryConfiguration重试策略源码解析】

>
RetryConfiguration配置是ConsulAutoConfiguration自动化配置的内部类，其作用是将AopAutoConfiguration、RetryProperties加入到IOC容器之中，并且实例化一个RetryOperationsInterceptor重试拦截器加入到IOC容器之中；

##### 1.RetryConfiguration配置类源码

```java
	
  //启用retry重试功能
  //AopAutoConfiguration类加入IOC容器
  //RetryProperties加入IOC容器
  @ConditionalOnClass({ Retryable.class, Aspect.class, AopAutoConfiguration.class })
	@Configuration(proxyBeanMethods = false)
	@EnableRetry(proxyTargetClass = true)
	@Import(AopAutoConfiguration.class)
	@EnableConfigurationProperties(RetryProperties.class)
	@ConditionalOnProperty(value = "spring.cloud.consul.retry.enabled",
			matchIfMissing = true)
	protected static class RetryConfiguration {

		@Bean(name = "consulRetryInterceptor")
		@ConditionalOnMissingBean(name = "consulRetryInterceptor")
		public RetryOperationsInterceptor consulRetryInterceptor(
				RetryProperties properties) {
      //根据RetryProperties属性配置构建拦截器类
			return RetryInterceptorBuilder.stateless()
					.backOffOptions(properties.getInitialInterval(),
							properties.getMultiplier(), properties.getMaxInterval())
					.maxAttempts(properties.getMaxAttempts()).build();
		}

	}
```

> 上述代码其实就是构建基于spring-retry的重试拦截器配置类，可以通过RetryProperties属性配置类更改重试的相关属性；

##### RetryProperties重试属性相关配置，此配置一定要配置在bootstrap.properties或yaml配置文件中

```properties
# 是否开启consul重试, 默认：true
spring.cloud.consul.retry.enabled=true
# 初始重试间隔（毫秒）,默认：1000
spring.cloud.consul.retry.initial-interval=1000
# 最大间隔，默认：2000
spring.cloud.consul.retry.max-interval=2000
# 最大尝试次数，默认：6
spring.cloud.consul.retry.max-attempts=5
# 下一个间隔的乘数，默认：1.1
spring.cloud.consul.retry.multiplier=1.1
```

##### RetryOperationsInterceptor重试拦截器，调用接口是会被AOP拦截到，然后通过while循环模式重试调用接口方法

```java
//拦截器类实现MethodInterceptor
public class RetryOperationsInterceptor implements MethodInterceptor {
	//实例化RetryTemplate实例对象
	private RetryOperations retryOperations = new RetryTemplate();
	//项目处理失败时恢复操作的策略接口
	private MethodInvocationRecoverer<?> recoverer;
	//上下文属性名称
	private String label;

	public void setLabel(String label) {
		this.label = label;
	}

	public void setRetryOperations(RetryOperations retryTemplate) {
		Assert.notNull(retryTemplate, "'retryOperations' cannot be null.");
		this.retryOperations = retryTemplate;
	}

	public void setRecoverer(MethodInvocationRecoverer<?> recoverer) {
		this.recoverer = recoverer;
	}

	public Object invoke(final MethodInvocation invocation) throws Throwable {
		//获取重试上下文报告名称，如果不存在，则根据方法名自动生成
		String name;
		if (StringUtils.hasText(label)) {
			name = label;
		} else {
			name = invocation.getMethod().toGenericString();
		}
		final String label = name;
		//重试回调接口，调用真实的接口方法，如果有异常则抛出异常
		RetryCallback<Object, Throwable> retryCallback = new RetryCallback<Object, Throwable>() {

			public Object doWithRetry(RetryContext context) throws Exception {
				//设置重试上下文名称
				context.setAttribute(RetryContext.NAME, label);

				if (invocation instanceof ProxyMethodInvocation) {
					try {
            //调用真实的放阿飞
						return ((ProxyMethodInvocation) invocation).invocableClone().proceed();
					}
					catch (Exception e) {
						throw e;
					}
					catch (Error e) {
						throw e;
					}
					catch (Throwable e) {
						throw new IllegalStateException(e);
					}
				}
				else {
					throw new IllegalStateException(
							"MethodInvocation of the wrong type detected - this should not happen with Spring AOP, " +
									"so please raise an issue if you see this exception");
				}
			}

		};

		if (recoverer != null) {
			ItemRecovererCallback recoveryCallback = new ItemRecovererCallback(
					invocation.getArguments(), recoverer);
			return this.retryOperations.execute(retryCallback, recoveryCallback);
		}
		//调用RetryTemplate的接口请求方法，参数是回调函数
		return this.retryOperations.execute(retryCallback);

	}

	/**
	 * @author Dave Syer
	 *
	 */
	private static final class ItemRecovererCallback implements RecoveryCallback<Object> {

		private final Object[] args;

		private final MethodInvocationRecoverer<?> recoverer;

		/**
		 * @param args the item that failed.
		 */
		private ItemRecovererCallback(Object[] args, MethodInvocationRecoverer<?> recoverer) {
			this.args = Arrays.asList(args).toArray();
			this.recoverer = recoverer;
		}

		public Object recover(RetryContext context) {
			return recoverer.recover(args, context.getLastThrowable());
		}

	}

}

```

##### RetryTemplate类中实际的调用方法doExecute实现了重试调用

```java
	protected <T, E extends Throwable> T doExecute(RetryCallback<T, E> retryCallback,
			RecoveryCallback<T> recoveryCallback, RetryState state)
			throws E, ExhaustedRetryException {

		RetryPolicy retryPolicy = this.retryPolicy;
		BackOffPolicy backOffPolicy = this.backOffPolicy;

		// Allow the retry policy to initialise itself...
		RetryContext context = open(retryPolicy, state);
		if (this.logger.isTraceEnabled()) {
			this.logger.trace("RetryContext retrieved: " + context);
		}

		// Make sure the context is available globally for clients who need
		// it...
		RetrySynchronizationManager.register(context);

		Throwable lastException = null;

		boolean exhausted = false;
		try {

			// Give clients a chance to enhance the context...
			boolean running = doOpenInterceptors(retryCallback, context);

			if (!running) {
				throw new TerminatedRetryException(
						"Retry terminated abnormally by interceptor before first attempt");
			}

			// Get or Start the backoff context...
			BackOffContext backOffContext = null;
			Object resource = context.getAttribute("backOffContext");

			if (resource instanceof BackOffContext) {
				backOffContext = (BackOffContext) resource;
			}

			if (backOffContext == null) {
				backOffContext = backOffPolicy.start(context);
				if (backOffContext != null) {
					context.setAttribute("backOffContext", backOffContext);
				}
			}

			/*
			 * 核心，使用while循环的模式重试调用接口
			 */
			while (canRetry(retryPolicy, context) && !context.isExhaustedOnly()) {

				try {
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Retry: count=" + context.getRetryCount());
					}
					// 重置最后一个异常，这样如果成功了，关闭拦截器就不会认为我们失败了
					lastException = null;
          //调用回调函数，如果成功直接返回结束循环，否则抛出异常继续重试
					return retryCallback.doWithRetry(context);
				}
				catch (Throwable e) {

					lastException = e;

					try {
            //更改重试次数
						registerThrowable(retryPolicy, state, context, e);
					}
					catch (Exception ex) {
						throw new TerminatedRetryException("Could not register throwable",
								ex);
					}
					finally {
            //调用监听器onError方法
						doOnErrorInterceptors(retryCallback, context, e);
					}

					if (canRetry(retryPolicy, context) && !context.isExhaustedOnly()) {
						try {
							backOffPolicy.backOff(backOffContext);
						}
						catch (BackOffInterruptedException ex) {
							lastException = e;
							// back off was prevented by another thread - fail the retry
							if (this.logger.isDebugEnabled()) {
								this.logger
										.debug("Abort retry because interrupted: count="
												+ context.getRetryCount());
							}
							throw ex;
						}
					}

					if (this.logger.isDebugEnabled()) {
						this.logger.debug(
								"Checking for rethrow: count=" + context.getRetryCount());
					}

					if (shouldRethrow(retryPolicy, context, state)) {
						if (this.logger.isDebugEnabled()) {
							this.logger.debug("Rethrow in retry for policy: count="
									+ context.getRetryCount());
						}
						throw RetryTemplate.<E>wrapIfNecessary(e);
					}

				}

				/*
				 * A stateful attempt that can retry may rethrow the exception before now,
				 * but if we get this far in a stateful retry there's a reason for it,
				 * like a circuit breaker or a rollback classifier.
				 */
				if (state != null && context.hasAttribute(GLOBAL_STATE)) {
					break;
				}
			}

			if (state == null && this.logger.isDebugEnabled()) {
				this.logger.debug(
						"Retry failed last attempt: count=" + context.getRetryCount());
			}

			exhausted = true;
			return handleRetryExhausted(recoveryCallback, context, state);

		}
		catch (Throwable e) {
			throw RetryTemplate.<E>wrapIfNecessary(e);
		}
		finally {
			close(retryPolicy, context, state, lastException == null || exhausted);
			doCloseInterceptors(retryCallback, context, lastException);
			RetrySynchronizationManager.clear();
		}

	}
```

总结：springcloud首先构建RetryOperationsInterceptor基于AOP的拦截器，拦截器内部使用了基于spring-retry的重试框架，其核心是RetryTemplate模板类，该类中通过while循环根据RetryProperties属性配置来重试；

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)