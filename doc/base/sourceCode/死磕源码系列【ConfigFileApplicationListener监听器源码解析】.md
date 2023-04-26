### 死磕源码系列【ConfigFileApplicationListener监听器源码解析】

> ConfigFileApplicationListener监听器主要用来处理环境配置相关业务，其加载是在springboot启动时通过SPI方式获取（之前的文章已经将结果）；

ConfigFileApplicationListener监听器类继承SmartApplicationListener接口实现了supportsEventType方法，判定监听器实际上支持的时间类型：

```java
	@Override
	public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
		return ApplicationEnvironmentPreparedEvent.class.isAssignableFrom(eventType)
				|| ApplicationPreparedEvent.class.isAssignableFrom(eventType);
	}
```

监听器支持ApplicationEnvironmentPreparedEvent、ApplicationPreparedEvent事件，其中ApplicationEnvironmentPreparedEvent事件是在SpringApplication#prepareEnvironment方法中通过listeners.environmentPrepared(
environment)触发，ApplicationPreparedEvent事件是在SpringApplication#prepareContext方法中通过listeners.contextLoaded(
context)触发；

##### 看下事件发生时触发的方法onApplicationEvent（此方法是监听器类业务处理的入口）：

```java
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
    //事件会在应用程序启动后准备Environment环境时触发
		if (event instanceof ApplicationEnvironmentPreparedEvent) {
			onApplicationEnvironmentPreparedEvent((ApplicationEnvironmentPreparedEvent) event);
		}
    //时间会在应用程序已经启动，ApplicationContext已经准备好，但是还未调用refreshed方式时触发
		if (event instanceof ApplicationPreparedEvent) {
			onApplicationPreparedEvent(event);
		}
	}
```

------

先看ApplicationEnvironmentPreparedEvent时间分支，这个时间主要是用来加载properties及yml配置文件的业务处理；

##### onApplicationEnvironmentPreparedEvent方法处理与环境相关的bean后置处理器：

```java
	private void onApplicationEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event) {
    //通过SPI的方式加载环境相关EnvironmentPostProcessor后置处理器
		List<EnvironmentPostProcessor> postProcessors = loadPostProcessors();
    //因为ConfigFileApplicationListener监听器也实现了EnvironmentPostProcessor
    //所以也将当前类加入列表中
		postProcessors.add(this);
    //对EnvironmentPostProcessor实现类按照优先级排序
		AnnotationAwareOrderComparator.sort(postProcessors);
    //循环调用EnvironmentPostProcessor实现类的回调方法
		for (EnvironmentPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessEnvironment(event.getEnvironment(), event.getSpringApplication());
		}
	}
```

loadPostProcessors方法通过springboot SPI加载EnvironmentPostProcessor配置：

```java
	List<EnvironmentPostProcessor> loadPostProcessors() {
		return SpringFactoriesLoader.loadFactories(EnvironmentPostProcessor.class, getClass().getClassLoader());
	}
```

spring.factories中EnvironmentPostProcessor环境相关后置处理器配置如下：

```java
# Environment Post Processors
org.springframework.boot.env.EnvironmentPostProcessor=\
org.springframework.boot.cloud.CloudFoundryVcapEnvironmentPostProcessor,\
org.springframework.boot.env.SpringApplicationJsonEnvironmentPostProcessor,\
org.springframework.boot.env.SystemEnvironmentPropertySourceEnvironmentPostProcessor,\
org.springframework.boot.reactor.DebugAgentEnvironmentPostProcessor
```

##### 现在我们只看ConfigFileApplicationListener监听器类作为EnvironmentPostProcessor实现类提供的回调方法postProcessEnvironment：

```java
	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
    //添加配置文件属性源到指定的环境
		addPropertySources(environment, application.getResourceLoader());
	}

```

addPropertySources方法添加配置文件属性源到指定的环境：

```java
	protected void addPropertySources(ConfigurableEnvironment environment, ResourceLoader resourceLoader) {
    //添加RandomValuePropertySource属性源配置到systemEnvironment环境配置之后
		RandomValuePropertySource.addToEnvironment(environment);
    //加载候选配置源到环境属性源对象中
		new Loader(environment, resourceLoader).load();
	}
```

##### Loader类用来加载候选属性源及激活的配置文件，首先看下构造函数

```java
	Loader(ConfigurableEnvironment environment, ResourceLoader resourceLoader) {
    	//环境属性初始化
			this.environment = environment;
			this.placeholdersResolver = new PropertySourcesPlaceholdersResolver(this.environment);
			//构造函数传递参数resourceLoader为null,所以三元运算符计算后的记过是DefaultResourceLoader
      //DefaultResourceLoader类实现了ResourceLoader接口
    	this.resourceLoader = (resourceLoader != null) ? resourceLoader : new DefaultResourceLoader(null);
     //通过SPI方式获取PropertySourceLoader加载接口的实现类PropertiesPropertySourceLoader、YamlPropertySourceLoader
			this.propertySourceLoaders = SpringFactoriesLoader.loadFactories(PropertySourceLoader.class,
					getClass().getClassLoader());
		}
```

org.springframework.core.io.ResourceLoader接口加载classpath及file
system下的资源配置文件，通常ApplicationContext需要这样的功能，DefaultResourceLoader是一个独立的实现在ApplicationContext之外使用的资源加载程序：

```java
public interface ResourceLoader {

	/** 从类路径中加载的伪URL路径： "classpath:". */
	String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;


	/**
	 * 返回指定资源位置的资源handle句柄
	 * handle句柄应该始终是可重用的在源描述符，允许多个 Resource#getInputStream（）调用。
	 * 必须支持完全限定的URL,例如："file:C:/test.dat"
	 * 必须支持classpath类路径伪URL,例如："classpath:test.dat"
	 * 应该支持相对文件路径,例如："WEB-INF/test.dat"
	 * 这将是特定实现的，通常由ApplicationContext实现提供
	 * 注意：资源handle句柄并不意味着现有资源，你需要调用Resource#exists来检查是否存在
	 * @param 资源位置
	 * @return a corresponding Resource handle (never {@code null})
	 * @see #CLASSPATH_URL_PREFIX
	 * @see Resource#exists()
	 * @see Resource#getInputStream()
	 */
	Resource getResource(String location);

	/**
	 * 返回ResourceLoader资源加载器使用的类加载器
	 */
	@Nullable
	ClassLoader getClassLoader();

}

```

ResourceLoader资源加载接口实现类DefaultResourceLoader，被ResourceEditor使用，通常服务于AbstractApplicationContext，也可以独立使用，在ConfigFileApplicationListener监听器中就属于独立应用（此处不再详解此类，后面会详解）。

通过SPI方式获取PropertySourceLoader加载程序的spring.factories配置是：

```java
# PropertySource Loaders
org.springframework.boot.env.PropertySourceLoader=\
org.springframework.boot.env.PropertiesPropertySourceLoader,\
org.springframework.boot.env.YamlPropertySourceLoader
```

##### Loader类的load方法是加载环境配置文件的核心方法

```java
		void load() {
      //PropertySource的内部实现类，被ConfigFileApplicationListener用于指定操作过滤出指定的属性配置
      //DEFAULT_PROPERTIES = "defaultProperties"
      //DEFAULT_PROPERTIES是Set集合，包含spring.profiles.active、spring.profiles.include
			FilteredPropertySource.apply(this.environment, DEFAULT_PROPERTIES, LOAD_FILTERED_PROPERTY,
					(defaultProperties) -> {
            //候选配置文件后缀，如：application-test.properties指的是test
						this.profiles = new LinkedList<>();
            //已经处理过的配置文件后缀，如：application-test.properties指的是test
						this.processedProfiles = new LinkedList<>();
            //是否有激活的配置文件：spring.profiles.active
						this.activatedProfiles = false;
            //存储从配置文件加载并解析后的配置文件
						this.loaded = new LinkedHashMap<>();
            //初始化默认的profile配置
						initializeProfiles();
						while (!this.profiles.isEmpty()) {
							Profile profile = this.profiles.poll();
              //判定是否是默认配置文件
							if (isDefaultProfile(profile)) {
								addProfileToEnvironment(profile.getName());
							}
              //加载指定的配置文件并将加载的结果存入loaded属性中
							load(profile, this::getPositiveProfileFilter,
									addToLoaded(MutablePropertySources::addLast, false));
              //添加已经处理过的配置
							this.processedProfiles.add(profile);
						}
						load(null, this::getNegativeProfileFilter, addToLoaded(MutablePropertySources::addFirst, true));
            //将加载到的配置文件件属性配置添加到Environment环境
						addLoadedPropertySources();
						applyActiveProfiles(defaultProperties);
					});
		}
```

##### load方法加载配置文件

```java
private void load(String location, String name, Profile profile, DocumentFilterFactory filterFactory,
				DocumentConsumer consumer) {
  		//判定默认文件名是否为null,默认是：application
			if (!StringUtils.hasText(name)) {
				for (PropertySourceLoader loader : this.propertySourceLoaders) {
					if (canLoadFileExtension(loader, location)) {
						load(loader, location, profile, filterFactory.getDocumentFilter(profile), consumer);
						return;
					}
				}
				throw new IllegalStateException("File extension of config file location '" + location
						+ "' is not known to any PropertySourceLoader. If the location is meant to reference "
						+ "a directory, it must end in '/'");
			}
  		//定义Set集合
			Set<String> processed = new HashSet<>();
			for (PropertySourceLoader loader : this.propertySourceLoaders) {
				for (String fileExtension : loader.getFileExtensions()) {
          //如果文件扩展名已经存在processed集合中就不会执行加载配置文件的方法（即不会执行对应的PropertySourceLoader加载器）
          //此处是我们自定义加载器解决中文乱码的关键
					if (processed.add(fileExtension)) {
						loadForFileExtension(loader, location + name, "." + fileExtension, profile, filterFactory,
								consumer);
					}
				}
			}
		}
```

##### loadForFileExtension加载配置文件

```java
		//DocumentFilterFactory是使用DocumentFilter过滤器的工厂方法，包含一个getDocumentFilter方法用来创建给定的配置创建DocumentFilter过滤器
		private void loadForFileExtension(PropertySourceLoader loader, String prefix, String fileExtension,
				Profile profile, DocumentFilterFactory filterFactory, DocumentConsumer consumer) {
      //DocumentFilter用于限制何时加载Document的过滤器
			DocumentFilter defaultFilter = filterFactory.getDocumentFilter(null);
			DocumentFilter profileFilter = filterFactory.getDocumentFilter(profile);
			if (profile != null) {
				// Try profile-specific file & profile section in profile file (gh-340)
				String profileSpecificFile = prefix + "-" + profile + fileExtension;
				load(loader, profileSpecificFile, profile, defaultFilter, consumer);
				load(loader, profileSpecificFile, profile, profileFilter, consumer);
				// Try profile specific sections in files we've already processed
				for (Profile processedProfile : this.processedProfiles) {
					if (processedProfile != null) {
						String previouslyLoaded = prefix + "-" + processedProfile + fileExtension;
						load(loader, previouslyLoaded, profile, profileFilter, consumer);
					}
				}
			}
			// 如果存在，尝试普通文件的特定于概要文件部分
			load(loader, prefix + fileExtension, profile, profileFilter, consumer);
		}
```

##### load方法获取配置的资源对象，然后根据资源对象Resource调用加载配置方法：

```java
private void load(PropertySourceLoader loader, String location, Profile profile, DocumentFilter filter,
				DocumentConsumer consumer) {
  		//根据参数配置文件路径获取配置文件对应的Resource资源对象
  		//classpath开头的环境变量配置返回的是ClassPathResource对象
  		//file开头的环境变量配置路径返回的是FileSystemResource对象
			Resource[] resources = getResources(location);
			for (Resource resource : resources) {
				try {
          //如果配置文件资源不存在，则继续循环
					if (resource == null || !resource.exists()) {
						if (this.logger.isTraceEnabled()) {
							StringBuilder description = getDescription("Skipped missing config ", location, resource,
									profile);
							this.logger.trace(description);
						}
						continue;
					}
          //如果文件扩展名不存在，则继续循环
					if (!StringUtils.hasText(StringUtils.getFilenameExtension(resource.getFilename()))) {
						if (this.logger.isTraceEnabled()) {
							StringBuilder description = getDescription("Skipped empty config extension ", location,
									resource, profile);
							this.logger.trace(description);
						}
						continue;
					}
          //将资源配置文件路径拼接成为PropertySourceLoader加载程序name需要的格式
          //如：applicationConfig: [classpath:/application-test.properties]
					String name = "applicationConfig: [" + getLocationName(location, resource) + "]";
          //核心方法，传递资源加载程序PropertySourceLoader实例、配置文件name及资源Resource对象
          //返回的是加载到的配置文件对象，包含PropertySource对象、配置profiles、激活配置activeProfiles
          //包含配置includeProfiles属性
					List<Document> documents = loadDocuments(loader, name, resource);
          //判定返回资源配置文件是否为空
					if (CollectionUtils.isEmpty(documents)) {
						if (this.logger.isTraceEnabled()) {
							StringBuilder description = getDescription("Skipped unloaded config ", location, resource,
									profile);
							this.logger.trace(description);
						}
						continue;
					}
					List<Document> loaded = new ArrayList<>();
					for (Document document : documents) {
						if (filter.match(document)) {
							addActiveProfiles(document.getActiveProfiles());
							addIncludedProfiles(document.getIncludeProfiles());
							loaded.add(document);
						}
					}
					Collections.reverse(loaded);
					if (!loaded.isEmpty()) {
            //调用消费者回调函数，将加载到的文档存放入loaded属性
						loaded.forEach((document) -> consumer.accept(profile, document));
						if (this.logger.isDebugEnabled()) {
							StringBuilder description = getDescription("Loaded config file ", location, resource,
									profile);
							this.logger.debug(description);
						}
					}
				}
				catch (Exception ex) {
					StringBuilder description = getDescription("Failed to load property source from ", location,
							resource, profile);
					throw new IllegalStateException(description.toString(), ex);
				}
			}
		}
```

##### addToLoaded方法是DocumentConsumer函数式接口的回调的具体实现，将加载到的配置属性存入loaded属性值：

```java
		private DocumentConsumer addToLoaded(BiConsumer<MutablePropertySources, PropertySource<?>> addMethod,
				boolean checkForExisting) {
			return (profile, document) -> {
				if (checkForExisting) {
					for (MutablePropertySources merged : this.loaded.values()) {
						if (merged.contains(document.getPropertySource().getName())) {
							return;
						}
					}
				}
				MutablePropertySources merged = this.loaded.computeIfAbsent(profile,
						(k) -> new MutablePropertySources());
				addMethod.accept(merged, document.getPropertySource());
			};
		}
```

##### loadDocument方法调用PropertySourceLoader加载程序的真实加载方法将配置加载到内存当中：

```java
		
	//loader是指属性源配置加载程序实例
	//name指配置文件属性名，如：applicationConfig: [classpath:/application.properties]
	//配置文件资源对象，实际是ClassPathResource实现类对象
	private List<Document> loadDocuments(PropertySourceLoader loader, String name, Resource resource)
				throws IOException {
    	//创建用于保存多次加载同一文档的缓存键
			DocumentsCacheKey cacheKey = new DocumentsCacheKey(loader, resource);
    	//获取键对应的文档信息
			List<Document> documents = this.loadDocumentsCache.get(cacheKey);
    	//如果缓存中文档信息不存在
			if (documents == null) {
        //将在指定的配置文件资源
				List<PropertySource<?>> loaded = loader.load(name, resource);
        //将资源PropertySource属性资源对象转换为Document对象
				documents = asDocuments(loaded);
        //将加载到的资源放入缓存之中
				this.loadDocumentsCache.put(cacheKey, documents);
			}
			return documents;
		}
```

------

#### 拓展类讲解

##### Profile静态内部类，是配置可以被加载的Spring配置类（包含两个参数name和defaultProfile）

```java
private static class Profile {
		//配置文件名
		private final String name;
		//配置文件是否是默认配置文件，默认：false
		private final boolean defaultProfile;
		//创建一个指定配置文件名，非默认配置文件的Profile配置实例
		Profile(String name) {
			this(name, false);
		}
		//创建一个指定配置文件名，及时否是默认配置文件参数的Profile配置实例
		Profile(String name, boolean defaultProfile) {
			Assert.notNull(name, "Name must not be null");
			this.name = name;
			this.defaultProfile = defaultProfile;
		}

		String getName() {
			return this.name;
		}

		boolean isDefaultProfile() {
			return this.defaultProfile;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj == null || obj.getClass() != getClass()) {
				return false;
			}
			return ((Profile) obj).name.equals(this.name);
		}

		@Override
		public int hashCode() {
			return this.name.hashCode();
		}

		@Override
		public String toString() {
			return this.name;
		}

	}
```

------

GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)