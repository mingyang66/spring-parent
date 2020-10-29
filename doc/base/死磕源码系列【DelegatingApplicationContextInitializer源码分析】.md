### 死磕源码系列【DelegatingApplicationContextInitializer源码分析】

> DelegatingApplicationContextInitializer初始化器实现类是ApplicationContextInitializer接口的一个代理实现类，提供了一个属性key(context.initializer.classes)在配置文件中配置自定义的初始化器实现类；当然，也可以使用SPI方式在spring.factories配置文件配置org.springframework.context.ApplicationContextInitializer作为key来配置，两种方式都可以，本文重点讲解第一种方式。

DelegatingApplicationContextInitializer源码分析：

```java
public class DelegatingApplicationContextInitializer
		implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

	// 配置属性key

	private static final String PROPERTY_NAME = "context.initializer.classes";
	//默认优先级，在所有的初始化器中默认是最高的
	private int order = 0;
	//初始化器回调方法
	@Override
	public void initialize(ConfigurableApplicationContext context) {
		ConfigurableEnvironment environment = context.getEnvironment();
    //获取配置的自定义初始化器实例集合
		List<Class<?>> initializerClasses = getInitializerClasses(environment);
		if (!initializerClasses.isEmpty()) {
			applyInitializerClasses(context, initializerClasses);
		}
	}

	private List<Class<?>> getInitializerClasses(ConfigurableEnvironment env) {
    //获取配置文件中配置的自定义初始化器
		String classNames = env.getProperty(PROPERTY_NAME);
		List<Class<?>> classes = new ArrayList<>();
		if (StringUtils.hasLength(classNames)) {
      //使用逗号分割初始化器
			for (String className : StringUtils.tokenizeToStringArray(classNames, ",")) {
        //将初始化器class实例添加到集合
				classes.add(getInitializerClass(className));
			}
		}
		return classes;
	}
	//将指定初始化器className实例化
	private Class<?> getInitializerClass(String className) throws LinkageError {
		try {
			Class<?> initializerClass = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
			Assert.isAssignable(ApplicationContextInitializer.class, initializerClass);
			return initializerClass;
		}
		catch (ClassNotFoundException ex) {
			throw new ApplicationContextException("Failed to load context initializer class [" + className + "]", ex);
		}
	}

	private void applyInitializerClasses(ConfigurableApplicationContext context, List<Class<?>> initializerClasses) {
		Class<?> contextClass = context.getClass();
		List<ApplicationContextInitializer<?>> initializers = new ArrayList<>();
		for (Class<?> initializerClass : initializerClasses) {
      //将初始化器的class实例转换成具体的bean对象
			initializers.add(instantiateInitializer(contextClass, initializerClass));
		}
    //调用初始化器的初始化方法
		applyInitializers(context, initializers);
	}
	//将初始化器的class实例转换成具体的bean对象
	private ApplicationContextInitializer<?> instantiateInitializer(Class<?> contextClass, Class<?> initializerClass) {
		Class<?> requireContextClass = GenericTypeResolver.resolveTypeArgument(initializerClass,
				ApplicationContextInitializer.class);
		Assert.isAssignable(requireContextClass, contextClass,
				String.format(
						"Could not add context initializer [%s] as its generic parameter [%s] is not assignable "
								+ "from the type of application context used by this context loader [%s]: ",
						initializerClass.getName(), requireContextClass.getName(), contextClass.getName()));
		return (ApplicationContextInitializer<?>) BeanUtils.instantiateClass(initializerClass);
	}
	//调用初始化器的初始化方法
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void applyInitializers(ConfigurableApplicationContext context,
			List<ApplicationContextInitializer<?>> initializers) {
		initializers.sort(new AnnotationAwareOrderComparator());
		for (ApplicationContextInitializer initializer : initializers) {
			initializer.initialize(context);
		}
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

}
```

------

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)