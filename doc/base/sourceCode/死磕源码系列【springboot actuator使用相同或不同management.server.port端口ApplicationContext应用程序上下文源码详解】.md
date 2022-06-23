### 死磕源码系列【springboot actuator使用相同或不同management.server.port端口ApplicationContext应用程序上下文源码详解】

> springboot actuator用于springboot项目健康监控，默认端口和应用程序相同，这时它们使用同一个应用程序上下文及tomcat容器；当management.server.port端口和应用程序不同时，actuator的应用上下文是系统的子上下文，使用独立的tomcat容器，这时如果我想拦截actuator应用程序的端点、管理actuator的容器及bean又该如何下手呢？

##### 1.监控的端口相同时正常的拦截器及过滤器都可以拦截到端点的请求，但是当端口不同的时候这些统统失效了，啊啊崩溃，如何解决呢？线让大家看一个不同端口拦截端点请求的示例：

定义一个Filter过滤器:

```java
public class MonitorIpFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //AntPathMatcher matcher = new AntPathMatcher();
        //LoggerUtils.info(ActuatorFilter.class, "访问地址是："+request.getRequestURL()+"，是否允许访问："+ matcher.match("/actuator/**", request.getRequestURI()));
        if(RequestUtils.isInternet(RequestUtils.getClientIp(request))){
            filterChain.doFilter(request, response);
        } else {
            response.setHeader("Content-Type", "text/html; charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.println("非内网用户，拒绝访问");
            writer.close();
        }
    }
}

```

定义一个配置类：

```java
@Configuration(proxyBeanMethods = false)
public class MonitorFilterRegistrationBeanAutoConfiguration {
    /**
     * 监控IP是否是内部IP过滤器
     */
    @Bean
    public FilterRegistrationBean<MonitorIpFilter> monitorIpFilterRegistrationBean() {
        FilterRegistrationBean<MonitorIpFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
        filterRegistrationBean.setFilter(new MonitorIpFilter());
        filterRegistrationBean.setName("monitorIpFilter");
        return filterRegistrationBean;
    }

}
```

在spring.factories配置文件中添加如下配置：

```java
org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration=\
  com.emily.boot.actuator.autoconfigure.MonitorFilterRegistrationBeanAutoConfiguration
```

> 经过上述简单的三步，一个基于springboot SPI机制的自动化配置组件开发完成，过滤器就可以拦截到端点发送过来的请求；是不是很神奇，脑袋里会有个疑问？这是如何拦截的？怎么做到的？接下来就这些疑问对源码进行分析并进行一一的解答。

##### 2.ManagementContext上下文加载

ManagementContext上下文应用程序加载触发入口，先看下spring-boot-actuator-autoconfigure.jar文件中的spring.factories文件：

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration,\
org.springframework.boot.actuate.autoconfigure.web.servlet.ServletManagementContextAutoConfiguration
```

> 看到EnableAutoConfiguration你应该能想到这是自动化配置（基于springboot SPI），ManagementContextAutoConfiguration、ServletManagementContextAutoConfiguration这两个类会在应用程序启动后自动的加载到容器之中，ServletManagementContextAutoConfiguration配置类是一个基于servlet应用 程序的工厂类，创建ManagementContext应用程序上下文；ManagementContextAutoConfiguration会在端口不同的时候通过事件触发ManagementContext应用上下文的创建。

ManagementContextAutoConfiguration是在 management.server.port和server.port端口相同时向环境Environment中添加属性配置，不同时创建应用程序上下文并初始化相关bean;端口相同时就不在讲解，看源码就明白了，只讲解端口不同时的源码：

DifferentManagementContextConfiguration是ManagementContextAutoConfiguration的一个内部类：

```java
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnManagementPort(ManagementPortType.DIFFERENT)
	static class DifferentManagementContextConfiguration implements ApplicationListener<WebServerInitializedEvent> {
		//宿主应用程序上下文
		private final ApplicationContext applicationContext;
		//ServletManagementContextAutoConfiguration配置类中初始化的ServletManagementContextFactory
		private final ManagementContextFactory managementContextFactory;

		DifferentManagementContextConfiguration(ApplicationContext applicationContext,
				ManagementContextFactory managementContextFactory) {
			this.applicationContext = applicationContext;
			this.managementContextFactory = managementContextFactory;
		}

		@Override
		public void onApplicationEvent(WebServerInitializedEvent event) {
      //判定时间是否是宿主应用程序发送过来的
			if (event.getApplicationContext().equals(this.applicationContext)) {
        //通过工厂方法创建actuator自己的应用程序上下文
        //EnableChildManagementContextConfiguration是一个很重要的内部类，通过该类的
        //@EnableManagementContext注解可以将ManagementContextConfigurationImportSelector加载到
        //managementcontext之中，并交其对应的配置类加载IOC容器之中
				ConfigurableWebServerApplicationContext managementContext = this.managementContextFactory
						.createManagementContext(this.applicationContext,
								EnableChildManagementContextConfiguration.class,
								PropertyPlaceholderAutoConfiguration.class);
				if (isLazyInitialization()) {
					managementContext.addBeanFactoryPostProcessor(new LazyInitializationBeanFactoryPostProcessor());
				}
        //设置上下文的命名空间
				managementContext.setServerNamespace("management");
        //设置应用上下文的ID
				managementContext.setId(this.applicationContext.getId() + ":management");
				setClassLoaderIfPossible(managementContext);
				CloseManagementContextListener.addIfPossible(this.applicationContext, managementContext);
        //refresh managementContext上下文，并创建端口号为management.server.port的tomcat容器
				managementContext.refresh();
			}
		}
	...

	}
```

> @ConditionalOnManagementPort注解标注当端口不同的时候会将此类实例化加入到应用程序的IOC容器之中，其还实现了ApplicationListener监听器接口，触发此监听器的事件是WebServerInitializedEvent；具体是org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext#createWebServer方法中的WebServerStartStopLifecycle类对应的start方法触发此事件；

上述DifferentManagementContextConfiguration类的核心是onApplicationEvent方法，通过ServletManagementContextFactory工厂方法createManagementContext创建应用程序上下文：

```java
	@Override
	public ConfigurableWebServerApplicationContext createManagementContext(ApplicationContext parent,
			Class<?>... configClasses) {
    //创建一个上下文对象
		AnnotationConfigServletWebServerApplicationContext child = new AnnotationConfigServletWebServerApplicationContext();
    //将宿主应用程序上下文做为其父上下文
		child.setParent(parent);
		List<Class<?>> combinedClasses = new ArrayList<>(Arrays.asList(configClasses));
		combinedClasses.add(ServletWebServerFactoryAutoConfiguration.class);
    //将传递进来的参数EnableChildManagementContextConfiguration、PropertyPlaceholderAutoConfiguration及ServletWebServerFactoryAutoConfiguration类注册到management Context上下文容器之中
		child.register(ClassUtils.toClassArray(combinedClasses));
		registerServletWebServerFactory(parent, child);
		return child;
	}
```

注意到了吗？EnableChildManagementContextConfiguration类是在上述代码中注册到ManagementContext上下文中的，其源码如下：

```java
@Configuration(proxyBeanMethods = false)
@EnableManagementContext(ManagementContextType.CHILD)
class EnableChildManagementContextConfiguration {

}

```

EnableChildManagementContextConfiguration是一个空类，被@Configuration标记的配之类，其还被@EnableManagementContext注解标注，属性ManagementContextType.CHILD说明是一个子上下文，注解@EnableManagementContext源码如下：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ManagementContextConfigurationImportSelector.class)
@interface EnableManagementContext {

	/**
	 * The management context type that should be enabled.
	 * @return the management context type
	 */
	ManagementContextType value();

}
```

注解上有个@Import注解将ManagementContextConfigurationImportSelector类引入，此类实现了DeferredImportSelector接口，看到这个接口应该眼熟了吧，自动化配置类EnableAutoConfiguration相关的配置类加载跟这个接口也有关系；效果其实相当于EnableChildManagementContextConfiguration直接通过@Import引入ManagementContextConfigurationImportSelector类：

```java
@Configuration(proxyBeanMethods = false)
@Import(ManagementContextConfigurationImportSelector.class)
class EnableChildManagementContextConfiguration {

}
```

##### 3.ManagementContextConfigurationImportSelector加载配置类到ManagementContext上下文

springboot自动化配置是通过将spring.factorie配置文件key为org.springframework.boot.autoconfigure.EnableAutoConfiguration的配置类加载到IOC容器之中，AutoConfigurationImportSelector是DeferredImportSelector接口的实现类，就是用来加载自动化配置类的；ManagementContextConfigurationImportSelector也是DeferredImportSelector接口的实现类，其作用跟AutoConfigurationImportSelector类的作用是一样的，将spring.factories配置文件中key是org.springframework.boot.actuate.autoconfigure.web.server.EnableManagementContext的配置加载的ManagementContext对应的容器之中，简言之就是actuator对应的自动化配置加载类：

```java
org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration=\
org.springframework.boot.actuate.autoconfigure.endpoint.web.ServletEndpointManagementContextConfiguration,\
org.springframework.boot.actuate.autoconfigure.endpoint.web.reactive.WebFluxEndpointManagementContextConfiguration,\
org.springframework.boot.actuate.autoconfigure.endpoint.web.servlet.WebMvcEndpointManagementContextConfiguration,\
org.springframework.boot.actuate.autoconfigure.endpoint.web.jersey.JerseyWebEndpointManagementContextConfiguration,\
org.springframework.boot.actuate.autoconfigure.security.servlet.SecurityRequestMatchersManagementContextConfiguration,\
org.springframework.boot.actuate.autoconfigure.web.jersey.JerseySameManagementContextConfiguration,\
org.springframework.boot.actuate.autoconfigure.web.jersey.JerseyChildManagementContextConfiguration,\
org.springframework.boot.actuate.autoconfigure.web.reactive.ReactiveManagementChildContextConfiguration,\
org.springframework.boot.actuate.autoconfigure.web.servlet.ServletManagementChildContextConfiguration,\
org.springframework.boot.actuate.autoconfigure.web.servlet.WebMvcEndpointChildContextConfiguration
```

ManagementContextConfigurationImportSelector类源码如下：

```java
@Order(Ordered.LOWEST_PRECEDENCE)
class ManagementContextConfigurationImportSelector implements DeferredImportSelector, BeanClassLoaderAware {

	private ClassLoader classLoader;

	@Override
	public String[] selectImports(AnnotationMetadata metadata) {
    //获取@EnableManagementContext注解属性值
		ManagementContextType contextType = (ManagementContextType) metadata
				.getAnnotationAttributes(EnableManagementContext.class.getName()).get("value");
		// 获取所有的配置类，过滤重复的
		List<ManagementConfiguration> configurations = getConfigurations();
    //排序（升序）
		OrderComparator.sort(configurations);
		List<String> names = new ArrayList<>();
    //获取符合条件的配置类，类型为ANY或者CHILD
		for (ManagementConfiguration configuration : configurations) {
			if (configuration.getContextType() == ManagementContextType.ANY
					|| configuration.getContextType() == contextType) {
				names.add(configuration.getClassName());
			}
		}
		return StringUtils.toStringArray(names);
	}
	//获取所有的配置类
	private List<ManagementConfiguration> getConfigurations() {
		SimpleMetadataReaderFactory readerFactory = new SimpleMetadataReaderFactory(this.classLoader);
		List<ManagementConfiguration> configurations = new ArrayList<>();
		for (String className : loadFactoryNames()) {
			addConfiguration(readerFactory, configurations, className);
		}
		return configurations;
	}

	private void addConfiguration(SimpleMetadataReaderFactory readerFactory,
			List<ManagementConfiguration> configurations, String className) {
		try {
			MetadataReader metadataReader = readerFactory.getMetadataReader(className);
			configurations.add(new ManagementConfiguration(metadataReader));
		}
		catch (IOException ex) {
			throw new RuntimeException("Failed to read annotation metadata for '" + className + "'", ex);
		}
	}
	//基于springboot SPI机制加载配置类
	protected List<String> loadFactoryNames() {
		return SpringFactoriesLoader.loadFactoryNames(ManagementContextConfiguration.class, this.classLoader);
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * 管理配置类，可以根据order排序
	 */
	private static final class ManagementConfiguration implements Ordered {

		private final String className;

		private final int order;

		private final ManagementContextType contextType;

		ManagementConfiguration(MetadataReader metadataReader) {
			AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
			this.order = readOrder(annotationMetadata);
			this.className = metadataReader.getClassMetadata().getClassName();
			this.contextType = readContextType(annotationMetadata);
		}

		private ManagementContextType readContextType(AnnotationMetadata annotationMetadata) {
			Map<String, Object> annotationAttributes = annotationMetadata
					.getAnnotationAttributes(ManagementContextConfiguration.class.getName());
			return (annotationAttributes != null) ? (ManagementContextType) annotationAttributes.get("value")
					: ManagementContextType.ANY;
		}

		private int readOrder(AnnotationMetadata annotationMetadata) {
			Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(Order.class.getName());
			Integer order = (attributes != null) ? (Integer) attributes.get("value") : null;
			return (order != null) ? order : Ordered.LOWEST_PRECEDENCE;
		}

		String getClassName() {
			return this.className;
		}

		@Override
		public int getOrder() {
			return this.order;
		}

		ManagementContextType getContextType() {
			return this.contextType;
		}

	}

}
```

至此如何创建management.server.port和server.port端口不同的应用程序子上下文的源码已经分析完毕，我们了解了如何创建应用程序子上下文时间是如何触发的、应用程序的创建、配置类加载；学会了如何创建一个配置类并初始化相应的bean交给子应用程序上下文来管理。

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)