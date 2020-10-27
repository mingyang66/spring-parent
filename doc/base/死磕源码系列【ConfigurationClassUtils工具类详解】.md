### 死磕源码系列【ConfigurationClassUtils工具类详解】

- ConfigurationClassUtils类是用于ConfigurationClass配置类的实用程序，是一个抽象类，只可以在框架内部使用；
- 如果bean定义是一个被@Configuration标注的JavaConfig配置类（且proxyBeanMethods为false），则bean定义属性设置为full标记;
- 如果bean定义被@Component、@ComponentScan、@Import、@ImportResource注解标注或者方法被@Bean标注，则bean定义属性设置为lite。
- 如果bean定义被设置了full或者lite属性，则如果存在设置bean定义的order属性

##### ConfigurationClassUtils类属性源码解析

```java
	//configuration class如果是@Configuration注解标注的类属性标注为full
	public static final String CONFIGURATION_CLASS_FULL = "full";
	//非@Configuration注解标注的类（@Component、@Import等注解标注）属性标注为lite
	public static final String CONFIGURATION_CLASS_LITE = "lite";
	//即值：org.springframework.context.annotation.ConfigurationClassPostProcessor.configurationClass作为属性配置类型标记属性的key
	public static final String CONFIGURATION_CLASS_ATTRIBUTE =
			Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, "configurationClass");
//即值：org.springframework.context.annotation.ConfigurationClassPostProcessor.order配置属性配置类排序的属性key
	private static final String ORDER_ATTRIBUTE =
			Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, "order");


	private static final Log logger = LogFactory.getLog(ConfigurationClassUtils.class);
	//字典，存储标注配置类的注解
	private static final Set<String> candidateIndicators = new HashSet<>(8);

	static {
		candidateIndicators.add(Component.class.getName());
		candidateIndicators.add(ComponentScan.class.getName());
		candidateIndicators.add(Import.class.getName());
		candidateIndicators.add(ImportResource.class.getName());
	}
```

检查给定的bean定义是否是配置类（或在configuration/component类中声明的嵌套组件类，也要自动注册）的候选项，并相应地标记它。

```java
	public static boolean checkConfigurationClassCandidate(
			BeanDefinition beanDef, MetadataReaderFactory metadataReaderFactory) {
		//获取bean定义的class类名
		String className = beanDef.getBeanClassName();
		if (className == null || beanDef.getFactoryMethodName() != null) {
			return false;
		}

		AnnotationMetadata metadata;
		if (beanDef instanceof AnnotatedBeanDefinition &&
				className.equals(((AnnotatedBeanDefinition) beanDef).getMetadata().getClassName())) {
			// Can reuse the pre-parsed metadata from the given BeanDefinition...
			metadata = ((AnnotatedBeanDefinition) beanDef).getMetadata();
		}
		else if (beanDef instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) beanDef).hasBeanClass()) {
			// 检查已经加载的类，如果存在的话
			// 因为我们甚至可能无法加载这个类的类文件
      //获取bean定义的class实例对象，如果class实例是下面四种类或接口的相同、子类、父接口等任何一种情况，直接返回
			Class<?> beanClass = ((AbstractBeanDefinition) beanDef).getBeanClass();
			if (BeanFactoryPostProcessor.class.isAssignableFrom(beanClass) ||
					BeanPostProcessor.class.isAssignableFrom(beanClass) ||
					AopInfrastructureBean.class.isAssignableFrom(beanClass) ||
					EventListenerFactory.class.isAssignableFrom(beanClass)) {
				return false;
			}
      //使用反射为给定类创建新的AnnotationMetadata实例
			metadata = AnnotationMetadata.introspect(beanClass);
		}
		else {
			try {
        //获取className的MetadataReader实例
				MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(className);
        //读取底层类的完整注释元数据，包括带注释方法的元数据
				metadata = metadataReader.getAnnotationMetadata();
			}
			catch (IOException ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Could not find class file for introspecting configuration annotations: " +
							className, ex);
				}
				return false;
			}
		}
		//获取bean定义的元数据被@Configuration注解标注的属性字典值
		Map<String, Object> config = metadata.getAnnotationAttributes(Configuration.class.getName());
    //如果bean被@Configuration注解标注，且属性proxyBeanMethods为false(使用代理模式)
    //则将bean定义标记为full
		if (config != null && !Boolean.FALSE.equals(config.get("proxyBeanMethods"))) {
			beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_FULL);
		}
    //如果bean被@Configuration注解标注，且被注解@Component、@ComponentScan、@Import、@ImportResource
    //或@Bean标记的方法，则将bean定义标记为lite
		else if (config != null || isConfigurationCandidate(metadata)) {
			beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_LITE);
		}
		else {
			return false;
		}

		// bean定义是一个标记为full或lite的候选项，如果设置order则设置order属性值
		Integer order = getOrder(metadata);
		if (order != null) {
      //设置bean定义的order值
			beanDef.setAttribute(ORDER_ATTRIBUTE, order);
		}

		return true;
	}
```

##### isConfigurationCandidate检查给定的元数据，以查找给定的候选配置类是否被指定的注解标注

```java
	public static boolean isConfigurationCandidate(AnnotationMetadata metadata) {
		//不考虑接口或注解
		if (metadata.isInterface()) {
			return false;
		}

		// 检查是否被注解@Component、@ComponentScan、@Import、@ImportResource标注
		for (String indicator : candidateIndicators) {
			if (metadata.isAnnotated(indicator)) {
				return true;
			}
		}

		// 最后检查是否有@Bean标注的方法
		try {
			return metadata.hasAnnotatedMethods(Bean.class.getName());
		}
		catch (Throwable ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Failed to introspect @Bean methods on class [" + metadata.getClassName() + "]: " + ex);
			}
			return false;
		}
	}
```

##### getOrder方法获取被@Order标注的排序值

```java
	@Nullable
	public static Integer getOrder(AnnotationMetadata metadata) {
		Map<String, Object> orderAttributes = metadata.getAnnotationAttributes(Order.class.getName());
		return (orderAttributes != null ? ((Integer) orderAttributes.get(AnnotationUtils.VALUE)) : null);
	}
```



GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)