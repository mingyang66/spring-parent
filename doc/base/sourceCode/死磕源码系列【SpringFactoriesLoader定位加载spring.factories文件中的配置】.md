### 死磕源码系列【SpringFactoriesLoader定位加载spring.factories文件中的配置】

> META-INF/spring.factories文件对我们来说应该是最熟悉的了，如springboot开发自动化配置starter的时候配置类是要配置到这个文件后才能够实现自动的加载类到IOC容器之中；但是文件中的配置类是如何被定位、加载、初始化的呢？今天我们就来聊聊spring.factories文件定位、加载、实例化。

##### 1.首先看下spring.factories文件中的配置示例

先看下spring-boot-autoconfigure中的部分配置：

```java
# Initializers
org.springframework.context.ApplicationContextInitializer=\
org.springframework.boot.autoconfigure.SharedMetadataReaderFactoryContextInitializer,\
org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener

# Application Listeners
org.springframework.context.ApplicationListener=\
org.springframework.boot.autoconfigure.BackgroundPreinitializer

# Auto Configuration Import Listeners
org.springframework.boot.autoconfigure.AutoConfigurationImportListener=\
org.springframework.boot.autoconfigure.condition.ConditionEvaluationReportAutoConfigurationImportListener

# Auto Configuration Import Filters
org.springframework.boot.autoconfigure.AutoConfigurationImportFilter=\
org.springframework.boot.autoconfigure.condition.OnBeanCondition,\
org.springframework.boot.autoconfigure.condition.OnClassCondition,\
org.springframework.boot.autoconfigure.condition.OnWebApplicationCondition

# Auto Configure
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration,\
org.springframework.boot.autoconfigure.aop.AopAutoConfiguration,\
org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration,\
org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration,\
org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration,\
org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration
```

文件中的的配置是KEY-VALUE类型，多个value之间使用逗号分隔；springboot的自动装配过程最终会加载META-INF/spring.factories文件，而META-INF/spring.factories文件是由org.springframework.core.io.support.SpringFactoriesLoader类加载的，从classpath下的每个jar包中搜索所有的META-INF/spring.factories配置文件，然后将其解析为properties，最后放到内存字典中；

##### 2.SpringFactoriesLoader类定位装载配置

```java
public final class SpringFactoriesLoader {

	/**
	 * spring.factories文件在jar包中的位置，可以存在于多个不同的jar包
	 */
	public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";
	/**
	* classpath环境变量下所有jar包中的spring.factories配置文件都解析完成后存入字典
	*/
	private static final Map<ClassLoader, MultiValueMap<String, String>> cache = new ConcurrentReferenceHashMap<>();
	...

	private static Map<String, List<String>> loadSpringFactories(@Nullable ClassLoader classLoader) {
		MultiValueMap<String, String> result = cache.get(classLoader);
		if (result != null) {
			return result;
		}

		try {
      //定位-扫描所有jar包取得资源的URL
			Enumeration<URL> urls = (classLoader != null ?
					classLoader.getResources(FACTORIES_RESOURCE_LOCATION) :
					ClassLoader.getSystemResources(FACTORIES_RESOURCE_LOCATION));
			result = new LinkedMultiValueMap<>();
      //遍历所有的URL
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				UrlResource resource = new UrlResource(url);
        //将资源解析为properties
				Properties properties = PropertiesLoaderUtils.loadProperties(resource);
				for (Map.Entry<?, ?> entry : properties.entrySet()) {
          //获取资源key工厂的全限定类名
					String factoryTypeName = ((String) entry.getKey()).trim();
          //遍历value值
					for (String factoryImplementationName : StringUtils.commaDelimitedListToStringArray((String) entry.getValue())) {
            //将遍历结果放入的字典集合
						result.add(factoryTypeName, factoryImplementationName.trim());
					}
				}
			}
      //最终将SPI资源接口存放入内存，供系统初始化运行使用
			cache.put(classLoader, result);
			return result;
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Unable to load factories from location [" +
					FACTORIES_RESOURCE_LOCATION + "]", ex);
		}
	}
	...

}

```

到目前为止我们已经了解了SPI资源是如何加载定位、装载到系统的内存中的整个过程，但是我还有一个疑问，系统是在哪里触发的资源加载？

##### 3.触发spring.factories资源文件加载源码分析

- 系统启动入口main

```java
@SpringBootApplication
public class QuartzBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(QuartzBootStrap.class, args);
    }
}

```

- 进入到run方法内

```java
	public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
		return run(new Class<?>[] { primarySource }, args);
	}

	public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
		return new SpringApplication(primarySources).run(args);
	}
```

- 进入到SpringApplication构造函数

```java
public SpringApplication(Class<?>... primarySources) {
		this(null, primarySources);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
		this.resourceLoader = resourceLoader;
		Assert.notNull(primarySources, "PrimarySources must not be null");
		this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
		this.webApplicationType = WebApplicationType.deduceFromClasspath();
		//初始化的入口在这里，此处是为了获取ApplicationContextInitializer实例对象而加载了整个系统的spring.factories文件，以后需要获取SPI资源直接就可以从缓存中取
		setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class));
		//此处是获取监听器的SPI资源实例并进行初始化
    setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
		this.mainApplicationClass = deduceMainApplicationClass();
	}
```

进入getSpringFactoriesInstances方法

```java
	private <T> Collection<T> getSpringFactoriesInstances(Class<T> type, Class<?>[] parameterTypes, Object... args) {
		ClassLoader classLoader = getClassLoader();
		//调用SpringFactoriesLoader类加载spring.factories文件
		Set<String> names = new LinkedHashSet<>(SpringFactoriesLoader.loadFactoryNames(type, classLoader));
    //实例化获取到的类实例
		List<T> instances = createSpringFactoriesInstances(type, parameterTypes, classLoader, args, names);
		AnnotationAwareOrderComparator.sort(instances);
		return instances;
	}
```

实例化获取到的class:

```java
	private <T> List<T> createSpringFactoriesInstances(Class<T> type, Class<?>[] parameterTypes,
			ClassLoader classLoader, Object[] args, Set<String> names) {
		List<T> instances = new ArrayList<>(names.size());
		for (String name : names) {
			try {
				Class<?> instanceClass = ClassUtils.forName(name, classLoader);
				Assert.isAssignable(type, instanceClass);
				Constructor<?> constructor = instanceClass.getDeclaredConstructor(parameterTypes);
				//这里调用BeanUtils的instantiateClass方法来实例化
        T instance = (T) BeanUtils.instantiateClass(constructor, args);
				instances.add(instance);
			}
			catch (Throwable ex) {
				throw new IllegalArgumentException("Cannot instantiate " + type + " : " + name, ex);
			}
		}
		return instances;
	}
```

到这里spring.factories文件的定位、加载、部分类的实例化已经讲解完了，相信你已经对这一块有了一个清醒的认识。

GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

