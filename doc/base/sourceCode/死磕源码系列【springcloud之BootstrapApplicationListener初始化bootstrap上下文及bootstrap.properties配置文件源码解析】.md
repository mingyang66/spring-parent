### 死磕源码系列【springcloud之BootstrapApplicationListener初始化bootstrap上下文及bootstrap.properties配置文件源码解析】

> BootstrapApplicationListener是ApplicationListener监听器接口的子类，其主要作用是初始化parent上下文、bootstrap.properties配置文件及应用程序上下文设置父上下文；

##### BootstrapApplicationListener监听器源码

```java
public class BootstrapApplicationListener
		implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

	/**
	 * 属性源名称是bootstrap
	 */
	public static final String BOOTSTRAP_PROPERTY_SOURCE_NAME = "bootstrap";

	/**
	 * 监听器的默认顺序
	 */
	public static final int DEFAULT_ORDER = Ordered.HIGHEST_PRECEDENCE + 5;

	/**
	 * 默认属性名称
	 */
	public static final String DEFAULT_PROPERTIES = "springCloudDefaultProperties";

	private int order = DEFAULT_ORDER;
	/**
	* ApplicationEnvironmentPreparedEvent是SpringApplication启动且Environment首次可供检查和修改时发布的事件
	**/
	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
		ConfigurableEnvironment environment = event.getEnvironment();
    // 判定bootstrap引导配置是否开启
		if (!environment.getProperty("spring.cloud.bootstrap.enabled", Boolean.class,
				true)) {
			return;
		}
		// 不监听bootstrap引导上下文
		if (environment.getPropertySources().contains(BOOTSTRAP_PROPERTY_SOURCE_NAME)) {
			return;
		}
		ConfigurableApplicationContext context = null;
		String configName = environment
				.resolvePlaceholders("${spring.cloud.bootstrap.name:bootstrap}");
    //轮询上下文中配置的初始化器
		for (ApplicationContextInitializer<?> initializer : event.getSpringApplication()
				.getInitializers()) {
      // 如果初始化器是ParentContextApplicationContextInitializer类型实例，则调用其初始化方法
			if (initializer instanceof ParentContextApplicationContextInitializer) {
				context = findBootstrapContext(
						(ParentContextApplicationContextInitializer) initializer,
						configName);
			}
		}
    //在springcloud中此处context为null
		if (context == null) {
      //核心 获取bootstrap上下文及初始化environment
			context = bootstrapServiceContext(environment, event.getSpringApplication(),
					configName);
      //添加监听器
			event.getSpringApplication()
					.addListeners(new CloseContextOnFailureApplicationListener(context));
		}

		apply(context, event.getSpringApplication(), environment);
	}

```

##### bootstrapServiceContext方法获取应用上下文及bootstrap配置文件

```java
	private ConfigurableApplicationContext bootstrapServiceContext(
			ConfigurableEnvironment environment, final SpringApplication application,
			String configName) {
    //新建environment上下文对象
		StandardEnvironment bootstrapEnvironment = new StandardEnvironment();
	
    ...
    //此处会将BootstrapImportSelectorConfiguration类家属资源集合，在初始化上下文的过程中会将其加入IOC容器
    builder.sources(BootstrapImportSelectorConfiguration.class);
    // 核心-此处调用初始化上下文的方法，并且不会创建tomcat，会通过回调设置应用程序的父上下文为bootstrap上下文  
		final ConfigurableApplicationContext context = builder.run();
		// 设置上下文ID
		context.setId("bootstrap");
		// 设置上下文的父上下文
		addAncestorInitializer(application, context);
		// It only has properties in it now that we don't want in the parent so remove
		// it (and it will be added back later)
		bootstrapProperties.remove(BOOTSTRAP_PROPERTY_SOURCE_NAME);
		mergeDefaultProperties(environment.getPropertySources(), bootstrapProperties);
		return context;
	}
```

上述源码中提到了会初始化上下文，但是不会创建tomcat，这又是为什么呢？看下SpringApplication#run方法中的createApplicationContext方法：

```java

	public static final String DEFAULT_CONTEXT_CLASS = "org.springframework.context."
			+ "annotation.AnnotationConfigApplicationContext";

	public static final String DEFAULT_SERVLET_WEB_CONTEXT_CLASS = "org.springframework.boot."
			+ "web.servlet.context.AnnotationConfigServletWebServerApplicationContext";

	public static final String DEFAULT_REACTIVE_WEB_CONTEXT_CLASS = "org.springframework."
			+ "boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext";

protected ConfigurableApplicationContext createApplicationContext() {
		Class<?> contextClass = this.applicationContextClass;
		if (contextClass == null) {
			try {
				switch (this.webApplicationType) {
				case SERVLET:
					contextClass = Class.forName(DEFAULT_SERVLET_WEB_CONTEXT_CLASS);
					break;
				case REACTIVE:
					contextClass = Class.forName(DEFAULT_REACTIVE_WEB_CONTEXT_CLASS);
					break;
				default:
					contextClass = Class.forName(DEFAULT_CONTEXT_CLASS);
				}
			}
			catch (ClassNotFoundException ex) {
				throw new IllegalStateException(
						"Unable create a default ApplicationContext, please specify an ApplicationContextClass", ex);
			}
		}
		return (ConfigurableApplicationContext) BeanUtils.instantiateClass(contextClass);
	}
```

此方法通过反射的方式创建应用程序上下文，并且根据容器的类型创建不同的上下文，servlet容器创建的是AnnotationConfigServletWebServerApplicationContext上下文，bootstrap引导上下文是使用默认的AnnotationConfigApplicationContext上下文创建，到这里还是不明白不同的上下文跟tomcat创建有什么关系？那我们继续往下看，创建tomcat容器是在AbstractApplicationContext#refresh方法中通过onRefresh实现的，看下onRefresh方法：

```java
	protected void onRefresh() throws BeansException {
		// For subclasses: do nothing by default.
	}
```

这是一个受保护的空方法，具体是由其子类实现,AnnotationConfigServletWebServerApplicationContext上下文的父类ServletWebServerApplicationContext是AbstractApplicationContext类的子类，并且实现了onRefresh方法；AnnotationConfigApplicationContext上下文虽然也是AbstractApplicationContext的子类，但是并未实现onRefresh方法，所以就可以很好的解释为什么创建了上下文，但是并没有创建tomcat的原因。

另外应用程序会通过ContextIdApplicationContextInitializer初始化器设置其自身的父上下文ID;

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)