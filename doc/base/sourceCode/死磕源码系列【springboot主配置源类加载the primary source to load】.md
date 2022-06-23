### 死磕源码系列【springboot主配置源类加载the primary source to load】

通常使用springboot开发是会有一个主方法main方法入口：

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuartzBootStrap {
    public static void main(String[] args) {
        //参数QuartzBootStrap.class是主配置源类
        SpringApplication.run(QuartzBootStrap.class, args);
    }
}
```

进入到SpringApplication#run方法：

```java
	/**
	 * Static helper that can be used to run a {@link SpringApplication} from the
	 * specified sources using default settings and user supplied arguments.
	 * @param primarySources the primary sources to load
	 * @param args the application arguments (usually passed from a Java main method)
	 * @return the running {@link ApplicationContext}
	 */
	public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
		return new SpringApplication(primarySources).run(args);
	}
```

run方法是一个静态方法，传递主配置源参数，会将主配置源类优先注册到IOC容器，为后续扫描加载项目中的其它bean做准备；上述方法创建类一个SpringApplication对象，构造函数如下：

```java
	public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
		this.resourceLoader = resourceLoader;
		Assert.notNull(primarySources, "PrimarySources must not be null");
    //此处会对主配置源参数做初始化，后续注册到容器时会用到
		this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
		this.webApplicationType = WebApplicationType.deduceFromClasspath();
		setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class));
		setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
		this.mainApplicationClass = deduceMainApplicationClass();
	}
```

由SpringApplication#run进入SpringApplication#prepareContext方法：

```java
	private void prepareContext(ConfigurableApplicationContext context, ConfigurableEnvironment environment,
		...省略
		// Load the sources 加载主配置源类
		Set<Object> sources = getAllSources();
		Assert.notEmpty(sources, "Sources must not be empty");
		load(context, sources.toArray(new Object[0]));
		listeners.contextLoaded(context);
	}
```

获取主配置源类方法SpringApplication#getAllSources：

```java
public Set<Object> getAllSources() {
   Set<Object> allSources = new LinkedHashSet<>();
   if (!CollectionUtils.isEmpty(this.primarySources)) {
   		//此处获取到的主配置源类就是上述主方法传入的配置源类
      allSources.addAll(this.primarySources);
   }
   if (!CollectionUtils.isEmpty(this.sources)) {
      allSources.addAll(this.sources);
   }
   return Collections.unmodifiableSet(allSources);
}
```

BeanDefinitionLoader#load()方法加载主配置类并注册到IOC容器：

```java
	int load() {
		int count = 0;
		for (Object source : this.sources) {
			count += load(source);
		}
		return count;
	}

	private int load(Object source) {
		Assert.notNull(source, "Source must not be null");
		if (source instanceof Class<?>) {
			return load((Class<?>) source);
		}
		if (source instanceof Resource) {
			return load((Resource) source);
		}
		if (source instanceof Package) {
			return load((Package) source);
		}
		if (source instanceof CharSequence) {
			return load((CharSequence) source);
		}
		throw new IllegalArgumentException("Invalid source type " + source.getClass());
	}

	private int load(Class<?> source) {
		if (isGroovyPresent() && GroovyBeanDefinitionSource.class.isAssignableFrom(source)) {
			// Any GroovyLoaders added in beans{} DSL can contribute beans here
			GroovyBeanDefinitionSource loader = BeanUtils.instantiateClass(source, GroovyBeanDefinitionSource.class);
			load(loader);
		}
    //检查bean是否符合注册条件
		if (isEligible(source)) {
      //将配置类注册到IOC容器之中
			this.annotatedReader.register(source);
			return 1;
		}
		return 0;
	}
```



GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)