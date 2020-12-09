死磕源码系列【springcloud之BootstrapImportSelector将BootstrapConfiguration配置加载到容器源码分析】

在BootstrapApplicationListener监听器中会将BootstrapImportSelectorConfiguration配置类注入到IOC容器，配置类上有一个@Import注解将BootstrapImportSelector类注入容器并获取spring.factories配置中key为org.springframework.cloud.bootstrap.BootstrapConfiguration的配置组件；

```java
@Configuration(proxyBeanMethods = false)
@Import(BootstrapImportSelector.class)
public class BootstrapImportSelectorConfiguration {

}
```

##### BootstrapImportSelector类是DeferredImportSelector个延迟selector，会在所有的类加载完成后加载spring.factories配置文件中的配置：

```java
public class BootstrapImportSelector implements EnvironmentAware, DeferredImportSelector {

	private Environment environment;

	private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public String[] selectImports(AnnotationMetadata annotationMetadata) {
    //获取当前的类加载器
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		// 通过SpringFactoriesLoader获取配置spring.factories中key为org.springframework.cloud.bootstrap.BootstrapConfiguration的组件
		List<String> names = new ArrayList<>(SpringFactoriesLoader
				.loadFactoryNames(BootstrapConfiguration.class, classLoader));
    //获取属性配置文件中key为spring.cloud.bootstrap.sources的组件，并将其加入到集合
		names.addAll(Arrays.asList(StringUtils.commaDelimitedListToStringArray(
				this.environment.getProperty("spring.cloud.bootstrap.sources", ""))));

		List<OrderedAnnotatedElement> elements = new ArrayList<>();
		for (String name : names) {
			try {
        // 将组将包装成OrderedAnnotatedElement对象加入集合，包含类名、order对象、order值
				elements.add(
						new OrderedAnnotatedElement(this.metadataReaderFactory, name));
			}
			catch (IOException e) {
				continue;
			}
		}
    //按照优先级排序
		AnnotationAwareOrderComparator.sort(elements);
		//获取所有的组件类名
		String[] classNames = elements.stream().map(e -> e.name).toArray(String[]::new);

		return classNames;
	}
	}
```

总结：此类会将springcloud中bootstrap上下文需要加载的组件注入到IOC容器，其spring.factories配置文件中的配置key为org.springframework.cloud.bootstrap.BootstrapConfiguration。



GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)