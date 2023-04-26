### 死磕源码系列【springcloud之@EnableDiscoveryClient More than one implementation及加不加@EnableDiscoveryClient注解都可以起作用原理分析】

最近做consul作为注册中心主方法main上添加与不添加@EnableDiscoveryClient启用服务发现注解都可以正常的使用服务发现及注册功能，这点上有些疑惑，所以就扒拉扒拉源码，看看实现原理到底是什么；

##### 1.看下注解@EnableDiscoveryClient源码

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(EnableDiscoveryClientImportSelector.class)
public @interface EnableDiscoveryClient {

	/**
	 * 如果为true,服务注册将自动注册本地服务
	 */
	boolean autoRegister() default true;

}
```

此注解通过@Import注解引入了一个EnableDiscoveryClientImportSelector类，具体核心自动化配置在此类中完成；

##### 2.EnableDiscoveryClientImportSelector类源码分析

```java

//此类实现了SpringFactoryImportSelector抽象类，而SpringFactoryImportSelector类又实现了DeferredImportSelector接口，看到DeferredImportSelector接口如果你了解springboot自动化配置就应该很清楚在哪里会调用了，此处不再详解，不了解的可以翻看我之前的文章
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class EnableDiscoveryClientImportSelector
		extends SpringFactoryImportSelector<EnableDiscoveryClient> {

	@Override
	public String[] selectImports(AnnotationMetadata metadata) {
    //此处调用父类SpringFactoryImportSelector的selectImports方法，此方法从spring.factories配置文件中读取key为org.springframework.cloud.client.discovery.EnableDiscoveryClient的自动化配置类
		String[] imports = super.selectImports(metadata);
		//获取@EnableDiscoveryClient注解的属性配置
		AnnotationAttributes attributes = AnnotationAttributes.fromMap(
				metadata.getAnnotationAttributes(getAnnotationClass().getName(), true));
    //获取属性注解autoRegister的属性值
		boolean autoRegister = attributes.getBoolean("autoRegister");
    //如果为true，则开启服务自动化注册
		if (autoRegister) {
			List<String> importsList = new ArrayList<>(Arrays.asList(imports));
      //将AutoServiceRegistrationConfiguration类加入到自动注册IOC容器集合
			importsList.add(
					"org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationConfiguration");
			imports = importsList.toArray(new String[0]);
		}
		else {
      //将spring.cloud.service-registry.auto-registration.enabled配置为false的结果加入到环境中
			Environment env = getEnvironment();
			if (ConfigurableEnvironment.class.isInstance(env)) {
				ConfigurableEnvironment configEnv = (ConfigurableEnvironment) env;
				LinkedHashMap<String, Object> map = new LinkedHashMap<>();
				map.put("spring.cloud.service-registry.auto-registration.enabled", false);
				MapPropertySource propertySource = new MapPropertySource(
						"springCloudDiscoveryClient", map);
				configEnv.getPropertySources().addLast(propertySource);
			}

		}

		return imports;
	}
	//是否开启了springcloud服务发现功能
	@Override
	protected boolean isEnabled() {
		return getEnvironment().getProperty("spring.cloud.discovery.enabled",
				Boolean.class, Boolean.TRUE);
	}
	//是否有默认工厂，默认：true
	@Override
	protected boolean hasDefaultFactory() {
		return true;
	}

}

```

看下父类SpringFactoryImportSelector的实现

```java
public abstract class SpringFactoryImportSelector<T>
		implements DeferredImportSelector, BeanClassLoaderAware, EnvironmentAware {

	private final Log log = LogFactory.getLog(SpringFactoryImportSelector.class);

	private ClassLoader beanClassLoader;

	private Class<T> annotationClass;

	private Environment environment;

	@SuppressWarnings("unchecked")
	protected SpringFactoryImportSelector() {
		this.annotationClass = (Class<T>) GenericTypeResolver
				.resolveTypeArgument(this.getClass(), SpringFactoryImportSelector.class);
	}

	@Override
	public String[] selectImports(AnnotationMetadata metadata) {
    //判定是否开启了自动化注册服务，如果false,直接返回空字符串数组
		if (!isEnabled()) {
			return new String[0];
		}
    //获取注解@EnableDiscoveryClient的属性
		AnnotationAttributes attributes = AnnotationAttributes.fromMap(
				metadata.getAnnotationAttributes(this.annotationClass.getName(), true));

		Assert.notNull(attributes, "No " + getSimpleName() + " attributes found. Is "
				+ metadata.getClassName() + " annotated with @" + getSimpleName() + "?");

		// 获取所有的自动化配置类，其配置文件中key为org.springframework.cloud.client.discovery.EnableDiscoveryClient，其中consul作为配置中心是配置文件中没有任何值，只有enurke时才有
		List<String> factories = new ArrayList<>(new LinkedHashSet<>(SpringFactoriesLoader
				.loadFactoryNames(this.annotationClass, this.beanClassLoader)));
    //如果配置类集合为空，并且没有默认的工厂方法，则抛出异常
		if (factories.isEmpty() && !hasDefaultFactory()) {
			throw new IllegalStateException("Annotation @" + getSimpleName()
					+ " found, but there are no implementations. Did you forget to include a starter?");
		}
		//如果上述自动化配置类集合中的配置类多与1个将会打印一个警告信息
		if (factories.size() > 1) {
			// there should only ever be one DiscoveryClient, but there might be more than
			// one factory
			this.log.warn("More than one implementation " + "of @" + getSimpleName()
					+ " (now relying on @Conditionals to pick one): " + factories);
		}

		return factories.toArray(new String[factories.size()]);
	}
	...
}
```

看过EnableDiscoveryClientImportSelector及其父类SpringFactoryImportSelector的源码你应该会发现此处的自动化配置是通过key为org.springframework.cloud.client.discovery.EnableDiscoveryClient的配置来实现的，这是你会想到做微服务相关的自动化配置是可以不使用org.springframework.boot.autoconfigure.EnableAutoConfiguration作为key来配置了，这样做是不是很美好；然后扒拉扒拉的把微服务相关的自动化配置改成org.springframework.cloud.client.discovery.EnableDiscoveryClient做为key，然后启动服务，发现可以正常启用及加载配置类，perfect；但是在仔细看启动日志发现有一个警告warn日志（More
than one implementation of @EnableDiscoveryClient (now relying on @Conditionals to pick one):
），瞬间感觉心情不美丽了，虽然程序正常启动没问题，但是总感觉有点啥问题，然后又开始了使劲的查找，在网上找到一篇文章[What's the difference between EnableEurekaClient and EnableDiscoveryClient?](https://stackoverflow.com/questions/31976236/whats-the-difference-between-enableeurekaclient-and-enablediscoveryclient)

[Ask Question](https://stackoverflow.com/questions/31976236/whats-the-difference-between-enableeurekaclient-and-enablediscoveryclient)
,上面有这样一段解释There are multiple implementations of "Discovery Service" (
eureka, [consul](https://github.com/spring-cloud/spring-cloud-consul), [zookeeper](https://github.com/spring-cloud/spring-cloud-zookeeper)). `@EnableDiscoveryClient`
lives in [spring-cloud-commons](https://github.com/spring-cloud/spring-cloud-commons) and picks the implementation on
the classpath. `@EnableEurekaClient` lives
in [spring-cloud-netflix](https://github.com/spring-cloud/spring-cloud-netflix/) and only works for eureka. If eureka is
on your classpath, they are effectively the same.

意思是这样的，服务发现有多种实现方式（consul、zookeeper、eureka）,其中客户端注解@EnableDiscoveryClient是基于spring-cloud-commons实现，@EnableEurekaClient是基于spring-cloud-netflix实现，如果是基于consul或zookeeper实现则使用@EnableDiscoveryClient注解启用服务（不建议使用org.springframework.cloud.client.discovery.EnableDiscoveryClient作为key进行自动化配置，否则会有一条警告信息），如果是使用eureka则使用@EnableEurekaClient启用服务（可以通过org.springframework.cloud.client.discovery.EnableDiscoveryClient=org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration）；

------

##### consul做服务注册及发现主方法加上@EnableDiscoveryClient和去掉一样起作用原因分析

>
上述源码我们已经看到了EnableDiscoveryClientImportSelector类的作用是将org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationConfiguration配置类注入到IOC容器之中，那为啥将@EnableDiscoveryClient注解去掉之后服务注册及发现功能一样其作用呢？接下来我们就对这做一个详细的分析；

##### AutoServiceRegistrationConfiguration源码

```java
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AutoServiceRegistrationProperties.class)
@ConditionalOnProperty(value = "spring.cloud.service-registry.auto-registration.enabled",
		matchIfMissing = true)
public class AutoServiceRegistrationConfiguration {

}
```

>
配置类有两个作用，第一将AutoServiceRegistrationProperties属性配置类注入IOC容器，其二只有在spring.cloud.service-registry.auto-registration.enabled为true时才将配置类注入IOC容器，默认就为true;

分析：既然@EnableDiscoveryClient注解不配置AutoServiceRegistrationConfiguration一样可以注入到IOC容器，那么说明肯定是有其他的地方有注入，看下spring.factories配置文件中的自动化配置有一个AutoServiceRegistrationAutoConfiguration自动化配置类，再看下其源码：

```java
@Configuration(proxyBeanMethods = false)
@Import(AutoServiceRegistrationConfiguration.class)
@ConditionalOnProperty(value = "spring.cloud.service-registry.auto-registration.enabled",
		matchIfMissing = true)
public class AutoServiceRegistrationAutoConfiguration {

	@Autowired(required = false)
	private AutoServiceRegistration autoServiceRegistration;

	@Autowired
	private AutoServiceRegistrationProperties properties;

	@PostConstruct
	protected void init() {
		if (this.autoServiceRegistration == null && this.properties.isFailFast()) {
			throw new IllegalStateException("Auto Service Registration has "
					+ "been requested, but there is no AutoServiceRegistration bean");
		}
	}

}
```

看到上面有一个@Import将AutoServiceRegistrationConfiguration配置类注入IOC容器，这里就说明了如果EnableDiscoveryClientImportSelector类没有将配置类注入IOC容器，那么此处就会将其注入，问题已经找到，可以愉快的玩转微服务了。。。

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)