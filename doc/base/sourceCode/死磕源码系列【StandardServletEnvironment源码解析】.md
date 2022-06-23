### 死磕源码系列【StandardServletEnvironment源码解析】

> spring中的StandardServletEnvironment将由基于servlet的web应用程序使用，所有基于servlet的web相关ApplicationContext类将会初始化一个StandardServletEnvironment实例。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201015175302794.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhb21pbmd5YW5n,size_16,color_FFFFFF,t_70#pic_center)

通过上面的图标可以清楚StandardServletEnvironment类的继承关系，PropertyResolver接口是用于针对任何基础源解析属性的接口；ConfigurablePropertyResolver接口是PropertyResolver的子接口，提供了将属性值从一种类型转换为另外一种类型的转换服务；Environment接口代表正在运行的应用程序的环境（包含配置文件Profile、属性Properties）;AbstractEnvironment抽象类是Environment接口的抽象实现，支持设置默认文件名，并允许指定激活和默认的配置文件；StandardEnvironment是Environment接口的实现，适用于标准（即非web）应用程序的实现，提供了两个标准的属性源systemEnvironment、systemProperties；ConfigurableWebEnvironment接口允许在ServletContext、ServletConfig变为可用的早期初始化servlet相关的PropertySource对象。

StandardServletEnvironment类源码如下：

```java
public class StandardServletEnvironment extends StandardEnvironment implements ConfigurableWebEnvironment {

	/** servletContext初始化参数属性源名 */
	public static final String SERVLET_CONTEXT_PROPERTY_SOURCE_NAME = "servletContextInitParams";

	/** ServletConfig初始化参数属性源名 */
	public static final String SERVLET_CONFIG_PROPERTY_SOURCE_NAME = "servletConfigInitParams";

	/** JNDI属性源名 */
	public static final String JNDI_PROPERTY_SOURCE_NAME = "jndiProperties";


	@Override
	protected void customizePropertySources(MutablePropertySources propertySources) {
    //新增servletConfig属性源配置，使用占位属性源对象，会被后面的后置处理器替换掉
		propertySources.addLast(new StubPropertySource(SERVLET_CONFIG_PROPERTY_SOURCE_NAME));
    //新增servletContext属性源配置，使用占位属性源对象，会被后面的后置处理器替换掉
		propertySources.addLast(new StubPropertySource(SERVLET_CONTEXT_PROPERTY_SOURCE_NAME));
		if (JndiLocatorDelegate.isDefaultJndiEnvironmentAvailable()) {
      //新增JNDI属性源配置
			propertySources.addLast(new JndiPropertySource(JNDI_PROPERTY_SOURCE_NAME));
		}
    //调用父类StandardEnvironment方法
    //新增PropertiesPropertySource属性原配置，name是systemProperties
    //新增SystemEnvironmentPropertySource属性源配置，name是systemEnvironment
		super.customizePropertySources(propertySources);
	}
	//此方法用于替换掉servletContext、servletConfig对应的占位属性源配置
	@Override
	public void initPropertySources(@Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig) {
		WebApplicationContextUtils.initServletPropertySources(getPropertySources(), servletContext, servletConfig);
	}

}

```

org.springframework.web.context.support.WebApplicationContextUtils#initServletPropertySources方法：

```java
	public static void initServletPropertySources(MutablePropertySources sources,
			@Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig) {

		Assert.notNull(sources, "'propertySources' must not be null");
		String name = StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME;
		if (servletContext != null && sources.get(name) instanceof StubPropertySource) {
      //替换掉servletContextInitParams占位属性源配置
			sources.replace(name, new ServletContextPropertySource(name, servletContext));
		}
		name = StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME;
		if (servletConfig != null && sources.get(name) instanceof StubPropertySource) {
      //替换掉servletConfigInitParams占位属性源配置
			sources.replace(name, new ServletConfigPropertySource(name, servletConfig));
		}
	}
```

在springboot框架中Environment对象初始化过程如org.springframework.boot.SpringApplication#prepareEnvironment方法：

```java
	private ConfigurableEnvironment prepareEnvironment(SpringApplicationRunListeners listeners,
			ApplicationArguments applicationArguments) {
		// 创建environment对象
		ConfigurableEnvironment environment = getOrCreateEnvironment();
    //对属性源及配置文件进行配置
		configureEnvironment(environment, applicationArguments.getSourceArgs());
    //环境Environment附加ConfigurationPropertySource属性配置源支持
		ConfigurationPropertySources.attach(environment);
    //通过后置处理程序加载配置文件
		listeners.environmentPrepared(environment);
		bindToSpringApplication(environment);
		if (!this.isCustomEnvironment) {
			environment = new EnvironmentConverter(getClassLoader()).convertEnvironmentIfNecessary(environment,
					deduceEnvironmentClass());
		}
		ConfigurationPropertySources.attach(environment);
		return environment;
	}
```

org.springframework.boot.SpringApplication#getOrCreateEnvironment方法创建Environment对象：

```java
	private ConfigurableEnvironment getOrCreateEnvironment() {
		if (this.environment != null) {
			return this.environment;
		}
		switch (this.webApplicationType) {
        //当前环境类型是servlet,所以就创建StandardServletEnvironment对象
		case SERVLET:
			return new StandardServletEnvironment();
		case REACTIVE:
			return new StandardReactiveWebEnvironment();
		default:
			return new StandardEnvironment();
		}
	}
```

org.springframework.boot.SpringApplication#configureEnvironment模板代理方法源码：

```java
	protected void configureEnvironment(ConfigurableEnvironment environment, String[] args) {
		if (this.addConversionService) {
			ConversionService conversionService = ApplicationConversionService.getSharedInstance();
      //设置属性转换对象
			environment.setConversionService((ConfigurableConversionService) conversionService);
		}
    //配置属性源
		configurePropertySources(environment, args);
    //配置配置文件
		configureProfiles(environment, args);
	}
```

org.springframework.boot.SpringApplication#configurePropertySources方法对应用程序的环境的PropertySource进行新增、删除或者冲新排序：

```java
protected void configurePropertySources(ConfigurableEnvironment environment, String[] args) {
		//获取应用程序环境中的属性源集合
  	MutablePropertySources sources = environment.getPropertySources();
		if (this.defaultProperties != null && !this.defaultProperties.isEmpty()) {
			sources.addLast(new MapPropertySource("defaultProperties", this.defaultProperties));
		}
  	//设置命令行参数的PropertySource对象
		if (this.addCommandLineProperties && args.length > 0) {
			String name = CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME;
			if (sources.contains(name)) {
				PropertySource<?> source = sources.get(name);
				CompositePropertySource composite = new CompositePropertySource(name);
				composite.addPropertySource(
						new SimpleCommandLinePropertySource("springApplicationCommandLineArgs", args));
				composite.addPropertySource(source);
				sources.replace(name, composite);
			}
			else {
				sources.addFirst(new SimpleCommandLinePropertySource(args));
			}
		}
	}
```

org.springframework.boot.SpringApplication#configureProfiles方法设置激活的配置文件：

```java
	protected void configureProfiles(ConfigurableEnvironment environment, String[] args) {
		Set<String> profiles = new LinkedHashSet<>(this.additionalProfiles);
		profiles.addAll(Arrays.asList(environment.getActiveProfiles()));
		environment.setActiveProfiles(StringUtils.toStringArray(profiles));
	}
```

org.springframework.boot.context.properties.source.ConfigurationPropertySources#attach方法附加ConfigurationPropertySourcesPropertySource支持：

```java
	public static void attach(Environment environment) {
		Assert.isInstanceOf(ConfigurableEnvironment.class, environment);
    //获取属性配置源
		MutablePropertySources sources = ((ConfigurableEnvironment) environment).getPropertySources();
    //获取属性配置源name为configurationProperties的对象
		PropertySource<?> attached = sources.get(ATTACHED_PROPERTY_SOURCE_NAME);
		if (attached != null && attached.getSource() != sources) {
			sources.remove(ATTACHED_PROPERTY_SOURCE_NAME);
			attached = null;
		}
		if (attached == null) {
      //新增属性配置源
			sources.addFirst(new ConfigurationPropertySourcesPropertySource(ATTACHED_PROPERTY_SOURCE_NAME,
					new SpringConfigurationPropertySources(sources)));
		}
	}
```

------

GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

