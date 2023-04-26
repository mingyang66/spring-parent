### 死磕源码系列【SpringApplicationRunListener监听器源码及加载过程解析】

>
springboot提供了两个类SpringApplicationRunListeners、SpringApplicationRunListener（EventPublishingRunListener），spring框架还提供了一个ApplicationListener接口，那么这几个类或接口的关系又是如何呢？首先SpringApplicationRunListeners类是SpringApplicationRunListener接口的代理类，可以批量调用SpringApplicationRunListener接口方法，SpringApplicationRunListener接口只有一个实现类EventPublishingRunListener，其有一个属性SimpleApplicationEventMulticaster，SimpleApplicationEventMulticaster即是一个ApplicationListener监听器接口的代理实现类，可以批量的执行监听器的onApplicationEvent方法。

##### 1.SpringApplicationRunListener接口源码

SpringApplicationRunListener是对org.springframework.boot.SpringApplication类的run方法进行监听，SpringApplicationRunListener实现类是通过SpringFactoriesLoader类加载（即springboot
SPI）;并且需要声明一个包含SpringApplication实例及String[]的参数构造方法。

```java
public interface SpringApplicationRunListener {

	/**
	 * 当run方法第一次启动时立即调用，可用于非常早期的初始化
	 */
	default void starting() {
	}

	/**
	 * 在ApplicationContext创建之前，一旦环境environment准备好就调用。
	 * @param environment the environment
	 */
	default void environmentPrepared(ConfigurableEnvironment environment) {
	}

	/**
	 * 在资源（可以理解为配置主类）被加载完成之前，一旦ApplicationContext被创建并准备好就立马调用，
	 * @param context the application context
	 */
	default void contextPrepared(ConfigurableApplicationContext context) {
	}

	/**
	 * 在资源加载完成之后，但在刷新之前调用
	 * @param context the application context
	 */
	default void contextLoaded(ConfigurableApplicationContext context) {
	}

	/**
	 * 上下文已刷新，应用程序已启动，但是CommandLineRunner和ApplicationRunner尚未调用。
	 * @param context the application context.
	 * @since 2.0.0
	 */
	default void started(ConfigurableApplicationContext context) {
	}

	/**
	 * 当ApplicationContext已经refresh且所有的CommandLineRunner和ApplicationRunner都已被调用时，在run方法完成之前立即调用。
	 * @param context the application context.
	 * @since 2.0.0
	 */
	default void running(ConfigurableApplicationContext context) {
	}

	/**
	 * 在运行应用程序时发生故障时调用
	 * @param context 应用程序上下文，可能为null(在应用程序上下文创建之前)
	 * @param exception the failure
	 * @since 2.0.0
	 */
	default void failed(ConfigurableApplicationContext context, Throwable exception) {
	}
```

##### 2.SpringApplicationRunListener接口唯一实现类EventPublishingRunListener

```java
public class EventPublishingRunListener implements SpringApplicationRunListener, Ordered {
	//启动类实例对象
	private final SpringApplication application;
	//参数
	private final String[] args;
	//ApplicationListener监听器接口代理广播类
	private final SimpleApplicationEventMulticaster initialMulticaster;

	public EventPublishingRunListener(SpringApplication application, String[] args) {
		this.application = application;
		this.args = args;
		this.initialMulticaster = new SimpleApplicationEventMulticaster();
    //获取应用程序的监听器类，并循环添加到代理类的监听器助手属性对象中
		for (ApplicationListener<?> listener : application.getListeners()) {
			this.initialMulticaster.addApplicationListener(listener);
		}
	}
}	
```

其中application.getListeners()获取通过SPI方式定义的所有ApplicationListener监听器接口定义的监听器类，其初始化是在SpringApplication类中通过构造函数的方式，如下：

```java
//监听器对象集合
private List<ApplicationListener<?>> listeners;

public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
		this.resourceLoader = resourceLoader;
		Assert.notNull(primarySources, "PrimarySources must not be null");
		this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
		this.webApplicationType = WebApplicationType.deduceFromClasspath();
  	//通过SPI方式初始化应用程序初始化器
		setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class));
		//通过SPI方式获取ApplicationListener监听器
		setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
		this.mainApplicationClass = deduceMainApplicationClass();
	}
```

spring.factories配置文件：

```java
# Application Listeners
org.springframework.context.ApplicationListener=\
org.springframework.boot.ClearCachesApplicationListener,\
org.springframework.boot.builder.ParentContextCloserApplicationListener,\
org.springframework.boot.cloud.CloudFoundryVcapEnvironmentPostProcessor,\
org.springframework.boot.context.FileEncodingApplicationListener,\
org.springframework.boot.context.config.AnsiOutputApplicationListener,\
org.springframework.boot.context.config.ConfigFileApplicationListener,\
org.springframework.boot.context.config.DelegatingApplicationListener,\
org.springframework.boot.context.logging.ClasspathLoggingApplicationListener,\
org.springframework.boot.context.logging.LoggingApplicationListener,\
org.springframework.boot.liquibase.LiquibaseServiceLocatorApplicationListener
```

##### 3.SpringApplicationRunListener接口实现类集合类SpringApplicationRunListeners

```java
class SpringApplicationRunListeners {

	private final Log log;
	//存储SpringApplicationRunListener监听器集合
	private final List<SpringApplicationRunListener> listeners;

	SpringApplicationRunListeners(Log log, Collection<? extends SpringApplicationRunListener> listeners) {
		this.log = log;
		this.listeners = new ArrayList<>(listeners);
	}
}
```

##### 4.org.springframework.boot.SpringApplication#run(java.lang.String...)方法中通过SPI方式获取SpringApplicationRunListeners对象

```java
	public ConfigurableApplicationContext run(String... args) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		ConfigurableApplicationContext context = null;
		Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
		configureHeadlessProperty();
    //通过SPI方式获取SpringApplicationRunListener监听器对象集合
		SpringApplicationRunListeners listeners = getRunListeners(args);
    //启动监听器，传递ApplicationStartingEvent事件
		listeners.starting();
		try {
			ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
      //environment准备好之后调用监听器environmentPrepared方法
      //传递ApplicationEnvironmentPreparedEvent事件
			ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
			configureIgnoreBeanInfo(environment);
			Banner printedBanner = printBanner(environment);
			context = createApplicationContext();
			exceptionReporters = getSpringFactoriesInstances(SpringBootExceptionReporter.class,
					new Class[] { ConfigurableApplicationContext.class }, context);
      //资源加载之前调用监听器的contextPrepared方法，传递ApplicationContextInitializedEvent事件
      //资源加载之后，refresh方法调用之前调用监听器的contextLoaded方法，传递ApplicationPreparedEvent事件
			prepareContext(context, environment, listeners, applicationArguments, printedBanner);
			refreshContext(context);
			afterRefresh(context, applicationArguments);
			stopWatch.stop();
			if (this.logStartupInfo) {
				new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), stopWatch);
			}
      //应用程序refresh之后调用，传递ApplicationStartedEvent事件
			listeners.started(context);
			callRunners(context, applicationArguments);
		}
		catch (Throwable ex) {
      //应用程序启动过程中出现异常调用failed方法
      //传递ApplicationFailedEvent事件
			handleRunFailure(context, ex, exceptionReporters, listeners);
			throw new IllegalStateException(ex);
		}

		try {
      //在应用程序run方法运行结束之前调用，传递ApplicationReadyEvent事件
			listeners.running(context);
		}
		catch (Throwable ex) {
			handleRunFailure(context, ex, exceptionReporters, null);
			throw new IllegalStateException(ex);
		}
		return context;
	}
	//通过SPI方式获取SpringApplicationRunListener接口实现类，并创建SpringApplicationRunListeners集合类
	private SpringApplicationRunListeners getRunListeners(String[] args) {
		Class<?>[] types = new Class<?>[] { SpringApplication.class, String[].class };
		return new SpringApplicationRunListeners(logger,
				getSpringFactoriesInstances(SpringApplicationRunListener.class, types, this, args));
	}
```

spring.factories配置文件如下：

```java
# Run Listeners
org.springframework.boot.SpringApplicationRunListener=\
org.springframework.boot.context.event.EventPublishingRunListener

```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)