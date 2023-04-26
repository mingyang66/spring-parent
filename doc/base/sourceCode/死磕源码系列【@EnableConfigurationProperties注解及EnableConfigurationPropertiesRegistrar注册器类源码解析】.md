### 死磕源码系列【@EnableConfigurationProperties注解及EnableConfigurationPropertiesRegistrar注册器类源码解析】

>
@EnableConfigurationProperties注解通常和@ConfigurationProperties注解一起使用，将标记了@ConfigurationProperties的bean绑定配置文件中的属性并将其注册到IOC容器之中。

##### 首先看下@EnableConfigurationProperties注解的源码

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EnableConfigurationPropertiesRegistrar.class)
public @interface EnableConfigurationProperties {

	/**
	 * 配置属性验证器的bean名称
	 * @since 2.2.0
	 */
	String VALIDATOR_BEAN_NAME = "configurationPropertiesValidator";

	/**
	 * 指定被@ConfigurationProperties注解标注的bean将会被注册到IOC容器
	 */
	Class<?>[] value() default {};

}
```

>
上述注解中核心是@Import注解引入EnableConfigurationPropertiesRegistrar类,此类是ImportBeanDefinitionRegistrar注册器接口的子类，将会注册属性配置、绑定等所需要bean;

------

##### EnableConfigurationPropertiesRegistrar注册器源码

```java
class EnableConfigurationPropertiesRegistrar implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
    //注册基础设施bean
		registerInfrastructureBeans(registry);
    //创建注册器代理类
		ConfigurationPropertiesBeanRegistrar beanRegistrar = new ConfigurationPropertiesBeanRegistrar(registry);
    //获取注解@EnableConfigurationProperties指定的被@ConfigurationProperties注解标注的bean实例对象
    //forEach循环调用注册器方法将@ConfigurationProperties标注的bean注册到IOC容器之中
		getTypes(metadata).forEach(beanRegistrar::register);
	}
	//获取注解@EnableConfigurationProperties指定的被@ConfigurationProperties注解标注的bean实例对象
	private Set<Class<?>> getTypes(AnnotationMetadata metadata) {
		return metadata.getAnnotations().stream(EnableConfigurationProperties.class)
				.flatMap((annotation) -> Arrays.stream(annotation.getClassArray(MergedAnnotation.VALUE)))
				.filter((type) -> void.class != type).collect(Collectors.toSet());
	}
	//将基础设施bean注册到IOC容器之中
	@SuppressWarnings("deprecation")
	static void registerInfrastructureBeans(BeanDefinitionRegistry registry) {
    //将BeanPostProcessor bean后置处理器ConfigurationPropertiesBindingPostProcessor注册到IOC容器
		ConfigurationPropertiesBindingPostProcessor.register(registry);
    //将提供属性绑定的BoundConfigurationProperties类注册到IOC容器之中
		BoundConfigurationProperties.register(registry);
    //将ConfigurationBeanFactoryMetadata类注册到IOC容器之中
		ConfigurationBeanFactoryMetadata.register(registry);
	}

}
```

------

##### ConfigurationPropertiesBeanRegistrar是BeanDefinitionRegistry注册器的代理实现类，代理类被EnableConfigurationPropertiesRegistrar和ConfigurationPropertiesScanRegistrar注册器类使用，用于将@ConfigurationProperties标注的bean注册为一个bean定义,源码如下：

```java
final class ConfigurationPropertiesBeanRegistrar {
	//bean定义注册器
	private final BeanDefinitionRegistry registry;
	//IOC容器工厂类
	private final BeanFactory beanFactory;
	//创建一个bean定义注册器属性配置代理实现类
	ConfigurationPropertiesBeanRegistrar(BeanDefinitionRegistry registry) {
		this.registry = registry;
		this.beanFactory = (BeanFactory) this.registry;
	}
	//将@ConfigurationProperties注解标注的bean注册到IOC容器
	void register(Class<?> type) {
		MergedAnnotation<ConfigurationProperties> annotation = MergedAnnotations
				.from(type, SearchStrategy.TYPE_HIERARCHY).get(ConfigurationProperties.class);
		register(type, annotation);
	}

	void register(Class<?> type, MergedAnnotation<ConfigurationProperties> annotation) {
		String name = getName(type, annotation);
		if (!containsBeanDefinition(name)) {
			registerBeanDefinition(name, type, annotation);
		}
	}
	//获取带有@ConfigurationProperties注解标注bean前缀的beanName
	private String getName(Class<?> type, MergedAnnotation<ConfigurationProperties> annotation) {
		String prefix = annotation.isPresent() ? annotation.getString("prefix") : "";
		return (StringUtils.hasText(prefix) ? prefix + "-" + type.getName() : type.getName());
	}

	private boolean containsBeanDefinition(String name) {
		return containsBeanDefinition(this.beanFactory, name);
	}
	//判定容器中是否包含指定name的bean定义
	private boolean containsBeanDefinition(BeanFactory beanFactory, String name) {
		if (beanFactory instanceof ListableBeanFactory
				&& ((ListableBeanFactory) beanFactory).containsBeanDefinition(name)) {
			return true;
		}
		if (beanFactory instanceof HierarchicalBeanFactory) {
			return containsBeanDefinition(((HierarchicalBeanFactory) beanFactory).getParentBeanFactory(), name);
		}
		return false;
	}

	private void registerBeanDefinition(String beanName, Class<?> type,
			MergedAnnotation<ConfigurationProperties> annotation) {
		Assert.state(annotation.isPresent(), () -> "No " + ConfigurationProperties.class.getSimpleName()
				+ " annotation found on  '" + type.getName() + "'.");
		this.registry.registerBeanDefinition(beanName, createBeanDefinition(beanName, type));
	}
	//根据beanName和class实例创建BeanDefinition
	private BeanDefinition createBeanDefinition(String beanName, Class<?> type) {
    //如果绑定方式是使用构造函数的绑定方式
		if (BindMethod.forType(type) == BindMethod.VALUE_OBJECT) {
			return new ConfigurationPropertiesValueObjectBeanDefinition(this.beanFactory, beanName, type);
		}
		GenericBeanDefinition definition = new GenericBeanDefinition();
		definition.setBeanClass(type);
		return definition;
	}

}

```

------

##### ConfigurationPropertiesBindingPostProcessor是BeanPostProcessor接口的实现类，用来绑定被注解@ConfigurationProperties标注的bean及配置文件中的PropertySources属性配置

```java
public class ConfigurationPropertiesBindingPostProcessor
		implements BeanPostProcessor, PriorityOrdered, ApplicationContextAware, InitializingBean {

	/**
	 * 后处理器的bean名称
	 */
	public static final String BEAN_NAME = ConfigurationPropertiesBindingPostProcessor.class.getName();
//应用程序上下文
	private ApplicationContext applicationContext;
//bean定义注册中心实现类
	private BeanDefinitionRegistry registry;
	//配置文件绑定实现类
	private ConfigurationPropertiesBinder binder;
	//初始化应用程序上下文属性
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	//初始化注册中心及binder属性值
	@Override
	public void afterPropertiesSet() throws Exception {
		// We can't use constructor injection of the application context because
		// it causes eager factory bean initialization
		this.registry = (BeanDefinitionRegistry) this.applicationContext.getAutowireCapableBeanFactory();
		this.binder = ConfigurationPropertiesBinder.get(this.applicationContext);
	}
	
	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 1;
	}
	//bean初始化之前调用初始化方法
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		bind(ConfigurationPropertiesBean.get(this.applicationContext, bean, beanName));
		return bean;
	}
	
	private void bind(ConfigurationPropertiesBean bean) {
    //如果bean为null,或者bean在IOC容器中并且是VALUE_OBJECT注册方式
		if (bean == null || hasBoundValueObject(bean.getName())) {
			return;
		}
		Assert.state(bean.getBindMethod() == BindMethod.JAVA_BEAN, "Cannot bind @ConfigurationProperties for bean '"
				+ bean.getName() + "'. Ensure that @ConstructorBinding has not been applied to regular bean");
		try {
      //bean是JAVA_BEAN绑定方式时通过递归的方式将配置文件中的属性绑定到bean对象中
			this.binder.bind(bean);
		}
		catch (Exception ex) {
			throw new ConfigurationPropertiesBindException(bean, ex);
		}
	}
	//判定@ConfigurationProperties注解标注的类是否已经注册到IOC容器中，并且该是对象是ConfigurationPropertiesValueObjectBeanDefinition类型的实例，即：是构造函数绑定方式
	private boolean hasBoundValueObject(String beanName) {
		return this.registry.containsBeanDefinition(beanName) && this.registry
				.getBeanDefinition(beanName) instanceof ConfigurationPropertiesValueObjectBeanDefinition;
	}

	/**
	 * 如果容器中不存在ConfigurationPropertiesBindingPostProcessor对应的BeanDefinition，则将其注册到IOC		* 容器之中，如果ConfigurationPropertiesBinder在容器中不存在也将其注册到容器之中
	 */
	public static void register(BeanDefinitionRegistry registry) {
		Assert.notNull(registry, "Registry must not be null");
		if (!registry.containsBeanDefinition(BEAN_NAME)) {
			GenericBeanDefinition definition = new GenericBeanDefinition();
			definition.setBeanClass(ConfigurationPropertiesBindingPostProcessor.class);
			definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			registry.registerBeanDefinition(BEAN_NAME, definition);
		}
		ConfigurationPropertiesBinder.register(registry);
	}

}
```

------

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

