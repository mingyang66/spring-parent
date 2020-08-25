### springboot通过AsyncConfigurer接口实现异步线程池自动化配置组件

> springboot提供了org.springframework.scheduling.annotation.AsyncConfigurer接口让开发人员可以自定义线程池执行器；框架默认提供了一个空的实现类AsyncConfigurerSupport，不过两个方法体内部提供的都是空实现；

##### 1.首先看下AsyncConfigurer接口

```java
public interface AsyncConfigurer {

	/**
	 * 方法返回一个实际执行线程的线程池
	 */
	@Nullable
	default Executor getAsyncExecutor() {
		return null;
	}

	/**
	 * 当线程池执行异步任务时会抛出AsyncUncaughtExceptionHandler异常，
	 * 此方法会捕获该异常
	 */
	@Nullable
	default AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return null;
	}

}
```

##### 2.通过上面的接口实现异步线程池

先看下我们的目标：

- 自定义一个异步线程池替换掉springboot框架默认的线程池；
- 自动捕获异步线程实例抛出的异常；
- 线程池的核心指标可以通过配置文件自定义配置；
- 封装成为一个组件，可以通过配置控制容器是否加载；

自动化配置类如下：

```java
package com.sgrain.boot.autoconfigure.threadpool;

import com.sgrain.boot.common.exception.PrintExceptionInfo;
import com.sgrain.boot.common.utils.LoggerUtils;
import com.sgrain.boot.common.utils.constant.CharacterUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @program: spring-parent
 * @description: 异步线程池配置 AsyncConfigurer在applicationContext早期初始化，如果需要依赖于其它的bean，尽可能的将它们声明为lazy
 * @create: 2020/08/21
 */
@EnableAsync
@Configuration
@EnableConfigurationProperties(AsyncThreadPoolProperties.class)
@ConditionalOnProperty(prefix = "spring.sgrain.async-thread-pool", name = "enable", havingValue = "true", matchIfMissing = false)
public class AsyncThreadPoolAutoConfiguration implements AsyncConfigurer {

    @Autowired
    private AsyncThreadPoolProperties asyncThreadPoolProperties;

    /**
     * 定义线程池
     * 使用{@link java.util.concurrent.LinkedBlockingQueue}(FIFO）队列，是一个用于并发环境下的阻塞队列集合类
     * ThreadPoolTaskExecutor不是完全被IOC容器管理的bean,可以在方法上加上@Bean注解交给容器管理,这样可以将taskExecutor.initialize()方法调用去掉，容器会自动调用
     *
     * @return
     */
    @Bean("asyncTaskExecutor")
    @Override
    public Executor getAsyncExecutor() {
        //Java虚拟机可用的处理器数
        int processors = Runtime.getRuntime().availableProcessors();
        //定义线程池
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //核心线程数
        taskExecutor.setCorePoolSize(Objects.nonNull(asyncThreadPoolProperties.getCorePoolSize()) ? asyncThreadPoolProperties.getCorePoolSize() : processors);
        //线程池最大线程数,默认：40000
        taskExecutor.setMaxPoolSize(Objects.nonNull(asyncThreadPoolProperties.getMaxPoolSize()) ? asyncThreadPoolProperties.getMaxPoolSize() : 40000);
        //线程队列最大线程数,默认：80000
        taskExecutor.setQueueCapacity(Objects.nonNull(asyncThreadPoolProperties.getMaxPoolSize()) ? asyncThreadPoolProperties.getMaxPoolSize() : 80000);
        //线程名称前缀
        taskExecutor.setThreadNamePrefix(StringUtils.isNotEmpty(asyncThreadPoolProperties.getThreadNamePrefix()) ? asyncThreadPoolProperties.getThreadNamePrefix() : "Async-ThreadPool-");
        //线程池中线程最大空闲时间，默认：60，单位：秒
        taskExecutor.setKeepAliveSeconds(asyncThreadPoolProperties.getKeepAliveSeconds());
        //核心线程是否允许超时，默认:false
        taskExecutor.setAllowCoreThreadTimeOut(asyncThreadPoolProperties.isAllowCoreThreadTimeOut());
        //IOC容器关闭时是否阻塞等待剩余的任务执行完成，默认:false（必须设置setAwaitTerminationSeconds）
        taskExecutor.setWaitForTasksToCompleteOnShutdown(asyncThreadPoolProperties.isWaitForTasksToCompleteOnShutdown());
        //阻塞IOC容器关闭的时间，默认：10秒（必须设置setWaitForTasksToCompleteOnShutdown）
        taskExecutor.setAwaitTerminationSeconds(asyncThreadPoolProperties.getAwaitTerminationSeconds());
        /**
         * 拒绝策略，默认是AbortPolicy
         * AbortPolicy：丢弃任务并抛出RejectedExecutionException异常
         * DiscardPolicy：丢弃任务但不抛出异常
         * DiscardOldestPolicy：丢弃最旧的处理程序，然后重试，如果执行器关闭，这时丢弃任务
         * CallerRunsPolicy：执行器执行任务失败，则在策略回调方法中执行任务，如果执行器关闭，这时丢弃任务
         */
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        //初始化
        //taskExecutor.initialize();

        return taskExecutor;
    }

    /**
     * 异步方法执行的过程中抛出的异常捕获
     *
     * @return
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            String msg = StringUtils.EMPTY;
            if (ArrayUtils.isNotEmpty(objects) && objects.length > 0) {
                msg = StringUtils.join(msg, "参数是：");
                for (int i = 0; i < objects.length; i++) {
                    msg = StringUtils.join(msg, objects[i], CharacterUtils.ENTER);
                }
            }
            if (Objects.nonNull(throwable)) {
                msg = StringUtils.join(msg, PrintExceptionInfo.printErrorInfo(throwable));
            }
            LoggerUtils.error(method.getDeclaringClass(), msg);
        };
    }
}

```

> 线程池的自定义属性及异常处理代码中的注解已经标的很清晰了，不在重复说明；@Configuration注解描述当前类是一个配置类，@EnableAsync注解描述启动线程池执行器开启异步执行功能；@EnableConfigurationProperties注解描述启动对@ConfigurationProperties注解标注的bean的支持，该bean可以被注入到IOC容器之中；@ConditionalOnProperty是一个条件注解，用来控制容器是否将当前的配置类注入到IOC容器之中；

线程池使用到的外部属性配置类如下：

```java
package com.sgrain.boot.autoconfigure.threadpool;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: spring-parent
 * @description: 异步线程池配置文件
 * @create: 2020/08/21
 */
@ConfigurationProperties(prefix = "spring.sgrain.async-thread-pool")
public class AsyncThreadPoolProperties {
    /**
     * 是否启动异步线程池，默认 false
     */
    private boolean enable;
    /**
     * 核心线程数,默认：Java虚拟机可用线程数
     */
    private Integer corePoolSize;
    /**
     * 线程池最大线程数,默认：40000
     */
    private Integer maxPoolSize;
    /**
     * 线程队列最大线程数,默认：80000
     */
    private Integer queueCapacity;
    /**
     * 自定义线程名前缀，默认：Async-ThreadPool-
     */
    private String threadNamePrefix;
    /**
     * 线程池中线程最大空闲时间，默认：60，单位：秒
     */
    private Integer keepAliveSeconds = 60;
    /**
     * 核心线程是否允许超时，默认false
     */
    private boolean allowCoreThreadTimeOut;
    /**
     * IOC容器关闭时是否阻塞等待剩余的任务执行完成，默认:false（必须设置setAwaitTerminationSeconds）
     */
    private boolean waitForTasksToCompleteOnShutdown;
    /**
     * 阻塞IOC容器关闭的时间，默认：10秒（必须设置setWaitForTasksToCompleteOnShutdown）
     */
    private int awaitTerminationSeconds = 10;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public Integer getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(Integer queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public Integer getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(Integer keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public boolean isAllowCoreThreadTimeOut() {
        return allowCoreThreadTimeOut;
    }

    public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
    }

    public boolean isWaitForTasksToCompleteOnShutdown() {
        return waitForTasksToCompleteOnShutdown;
    }

    public void setWaitForTasksToCompleteOnShutdown(boolean waitForTasksToCompleteOnShutdown) {
        this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
    }

    public int getAwaitTerminationSeconds() {
        return awaitTerminationSeconds;
    }

    public void setAwaitTerminationSeconds(int awaitTerminationSeconds) {
        this.awaitTerminationSeconds = awaitTerminationSeconds;
    }
}

```

properties属性配置示例如下：

```java
#异步线程池
#异步线程池组件开关，默认false
spring.sgrain.async-thread-pool.enable=true
#核心线程数,默认：Java虚拟机可用线程数
spring.sgrain.async-thread-pool.core-pool-size=4
#线程池最大线程数,默认：40000
spring.sgrain.async-thread-pool.max-pool-size=40000
#线程队列最大线程数,默认：80000
spring.sgrain.async-thread-pool.queue-capacity=80000
#自定义线程名前缀，默认：Async-ThreadPool-
spring.sgrain.async-thread-pool.thread-name-prefix=Async-ThreadPool-
#线程池中线程最大空闲时间，默认：60，单位：秒
spring.sgrain.async-thread-pool.keep-alive-seconds=60
#核心线程是否允许超时，默认false
spring.sgrain.async-thread-pool.allow-core-thread-time-out=false
#IOC容器关闭时是否阻塞等待剩余的任务执行完成，默认:false（必须设置setAwaitTerminationSeconds）
spring.sgrain.async-thread-pool.wait-for-tasks-to-complete-on-shutdown=false
#阻塞IOC容器关闭的时间，默认：10秒（必须设置setWaitForTasksToCompleteOnShutdown）
spring.sgrain.async-thread-pool.await-termination-seconds=10
```

上面的开发任务都做好之后还需要最后一步，将com.sgrain.boot.autoconfigure.threadpool.AsyncThreadPoolAutoConfiguration配置到resources/META-INF目录的spring.factories文件中：

```java
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.sgrain.boot.autoconfigure.threadpool.AsyncThreadPoolAutoConfiguration
```

看到配置的属性key是EnableAutoConfiguration大概你就可以猜测到是为了启用自动化配置功能；到现在整个异步线程池自动化配置组件已经开发完成了，那如何使用呢？我就不再举例说明了，网上有很多示例，只说几个重点；

- 使用@Async注解标注方法为异步任务
- 异步任务返回值为void
- 异步任务方法必须使用@Override标注，即是一个接口的实现方法

##### 3.异步线程池原理分析

> springboot框架自带ThreadPoolTaskExecutor线程池，org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration自动化配置类自动的创建默认的线程池，如果没有自定义ThreadPoolTaskExecutor线程池，那么@EnableAsync异步线程自动关联默认的线程池；

- 看下@EnableAsync的源码是如何启动异步执行器的

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AsyncConfigurationSelector.class)
public @interface EnableAsync {

	Class<? extends Annotation> annotation() default Annotation.class;
	boolean proxyTargetClass() default false;
	AdviceMode mode() default AdviceMode.PROXY;
	int order() default Ordered.LOWEST_PRECEDENCE;

}
```

上述代码的核心是@Import注解导入的AsyncConfigurationSelector选择器类；

- AsyncConfigurationSelector选择器类用来确定具体实现的配置类，一共有两种模式一种是使用JDK自带的动态代理模式实现动态代理，另外一种是使用ASPECTJ实现动态代理；默认使用JDK模式；

```java
public class AsyncConfigurationSelector extends AdviceModeImportSelector<EnableAsync> {

	private static final String ASYNC_EXECUTION_ASPECT_CONFIGURATION_CLASS_NAME =
			"org.springframework.scheduling.aspectj.AspectJAsyncConfiguration";


	/**
	 * Returns {@link ProxyAsyncConfiguration} or {@code AspectJAsyncConfiguration}
	 * for {@code PROXY} and {@code ASPECTJ} values of {@link EnableAsync#mode()},
	 * respectively.
	 */
	@Override
	@Nullable
	public String[] selectImports(AdviceMode adviceMode) {
		switch (adviceMode) {
			case PROXY:
				return new String[] {ProxyAsyncConfiguration.class.getName()};
			case ASPECTJ:
				return new String[] {ASYNC_EXECUTION_ASPECT_CONFIGURATION_CLASS_NAME};
			default:
				return null;
		}
	}

}
```

- ProxyAsyncConfiguration配置类源码如下

```java
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ProxyAsyncConfiguration extends AbstractAsyncConfiguration {

	@Bean(name = TaskManagementConfigUtils.ASYNC_ANNOTATION_PROCESSOR_BEAN_NAME)
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public AsyncAnnotationBeanPostProcessor asyncAdvisor() {
		Assert.notNull(this.enableAsync, "@EnableAsync annotation metadata was not injected");
		AsyncAnnotationBeanPostProcessor bpp = new AsyncAnnotationBeanPostProcessor();
		bpp.configure(this.executor, this.exceptionHandler);
		Class<? extends Annotation> customAsyncAnnotation = this.enableAsync.getClass("annotation");
		if (customAsyncAnnotation != AnnotationUtils.getDefaultValue(EnableAsync.class, "annotation")) {
			bpp.setAsyncAnnotationType(customAsyncAnnotation);
		}
		bpp.setProxyTargetClass(this.enableAsync.getBoolean("proxyTargetClass"));
		bpp.setOrder(this.enableAsync.<Integer>getNumber("order"));
		return bpp;
	}

}
```

上面的类是AbstractAsyncConfiguration类的子类，该类通过setConfigurers方法注入实现了AsyncConfigurer配置的自定义线程池，源码如下：

```java
	@Autowired(required = false)
	void setConfigurers(Collection<AsyncConfigurer> configurers) {
		if (CollectionUtils.isEmpty(configurers)) {
			return;
		}
		if (configurers.size() > 1) {
			throw new IllegalStateException("Only one AsyncConfigurer may exist");
		}
		AsyncConfigurer configurer = configurers.iterator().next();
		this.executor = configurer::getAsyncExecutor;
		this.exceptionHandler = configurer::getAsyncUncaughtExceptionHandler;
	}
```

ProxyAsyncConfiguration源码中的AsyncAnnotationBeanPostProcessor是一个Bean的后置处理器，所以会在setBeanFactory方法中对AOP切面类AsyncAnnotationAdvisor进行了初始化，源码如下：

```java
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		super.setBeanFactory(beanFactory);

		AsyncAnnotationAdvisor advisor = new AsyncAnnotationAdvisor(this.executor, this.exceptionHandler);
		if (this.asyncAnnotationType != null) {
			advisor.setAsyncAnnotationType(this.asyncAnnotationType);
		}
		advisor.setBeanFactory(beanFactory);
		this.advisor = advisor;
	}
```

切面类的构造函数中有两个核心点，对通知和切点进行初始化，源码如下：

```java
	public AsyncAnnotationAdvisor(
			@Nullable Supplier<Executor> executor, @Nullable Supplier<AsyncUncaughtExceptionHandler> exceptionHandler) {

		Set<Class<? extends Annotation>> asyncAnnotationTypes = new LinkedHashSet<>(2);
		asyncAnnotationTypes.add(Async.class);
		try {
			asyncAnnotationTypes.add((Class<? extends Annotation>)
					ClassUtils.forName("javax.ejb.Asynchronous", AsyncAnnotationAdvisor.class.getClassLoader()));
		}
		catch (ClassNotFoundException ex) {
			// If EJB 3.1 API not present, simply ignore.
		}
		this.advice = buildAdvice(executor, exceptionHandler);
		this.pointcut = buildPointcut(asyncAnnotationTypes);
	}
```

其中buildAdvice方法对拦截器AnnotationAsyncExecutionInterceptor进行了初始化，该类实现了MethodInterceptor接口，源码如下：

```java
	protected Advice buildAdvice(
			@Nullable Supplier<Executor> executor, @Nullable Supplier<AsyncUncaughtExceptionHandler> exceptionHandler) {

		AnnotationAsyncExecutionInterceptor interceptor = new AnnotationAsyncExecutionInterceptor(null);
		interceptor.configure(executor, exceptionHandler);
		return interceptor;
	}
```

上述拦截器类调用了configure方法，该方法对默认线程池和异常执行器进行了初始化，源码如下：

```java
	public void configure(@Nullable Supplier<Executor> defaultExecutor,
			@Nullable Supplier<AsyncUncaughtExceptionHandler> exceptionHandler) {

		this.defaultExecutor = new SingletonSupplier<>(defaultExecutor, () -> getDefaultExecutor(this.beanFactory));
		this.exceptionHandler = new SingletonSupplier<>(exceptionHandler, SimpleAsyncUncaughtExceptionHandler::new);
	}
```

这个地方是一个关键点，如果没有自定义实现AsyncConfigurer接口，则此处获取到的是默认的线程池，如果自定义实现了AsyncConfigurer接口，则此处获取到的就是自定义线程池；

- 接下来我们看下拦截器的invoke方法，此方法是在AnnotationAsyncExecutionInterceptor的父类AsyncExecutionInterceptor中，源码如下：

```
	@Override
	@Nullable
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
		Method specificMethod = ClassUtils.getMostSpecificMethod(invocation.getMethod(), targetClass);
		final Method userDeclaredMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
		//此处是用来获取具体执行线程任务线程池
		AsyncTaskExecutor executor = determineAsyncExecutor(userDeclaredMethod);
		if (executor == null) {
			throw new IllegalStateException(
					"No executor specified and no default executor set on AsyncExecutionInterceptor either");
		}

		Callable<Object> task = () -> {
			try {
				Object result = invocation.proceed();
				if (result instanceof Future) {
					return ((Future<?>) result).get();
				}
			}
			catch (ExecutionException ex) {
				handleError(ex.getCause(), userDeclaredMethod, invocation.getArguments());
			}
			catch (Throwable ex) {
				handleError(ex, userDeclaredMethod, invocation.getArguments());
			}
			return null;
		};

		return doSubmit(task, executor, invocation.getMethod().getReturnType());
	}
```

上述代码的核心determineAsyncExecutor方法用来确定执行线程的线程池，源码如下：

```java
@Nullable
	protected AsyncTaskExecutor determineAsyncExecutor(Method method) {
		AsyncTaskExecutor executor = this.executors.get(method);
		if (executor == null) {
			Executor targetExecutor;
			String qualifier = getExecutorQualifier(method);
			if (StringUtils.hasLength(qualifier)) {
				targetExecutor = findQualifiedExecutor(this.beanFactory, qualifier);
			}
			else {
				targetExecutor = this.defaultExecutor.get();
			}
			if (targetExecutor == null) {
				return null;
			}
			executor = (targetExecutor instanceof AsyncListenableTaskExecutor ?
					(AsyncListenableTaskExecutor) targetExecutor : new TaskExecutorAdapter(targetExecutor));
			this.executors.put(method, executor);
		}
		return executor;
	}
```

至此，通过@EnableAsync注解启动异步线程池，如何加载默认线程池配置，如何定义AOP切面及拦截器，通过@Async标注异步任务如何确定执行的线程池的原理及源码分析已经完成。

GitHub源码：[https://github.com/mingyang66/spring-parent/tree/master/sgrain-spring-boot-autoconfigure](https://github.com/mingyang66/spring-parent/tree/master/sgrain-spring-boot-autoconfigure)