### 死磕源码系列【ConfigurationClassPostProcessor类定位、加载、注册、实例化bean到IOC容器原理详解】

>
ConfigurationClassPostProcessor类是BeanFactory的一个后处理器类，因此它的主要功能是参与BeanFactory的构建，这个类实现了BeanDefinitionRegistryPostProcessor接口，有两个核心实现方法postProcessBeanDefinitionRegistry（定位、加载、解析、注册相关注解，如：@Controller、@Service、@Component等注解类到IOC容器之中，自动化配置类的解析、注册）、postProcessBeanFactory（添加CGLIB增强处理及ImportAwareBeanPostProcessor后处理类）

##### 1.看到ConfigurationClassPostProcessor类我们应该想如下几个问题

- 如何解析@SpringBootApplication注解及定位要扫描的包（默认扫描主类所在的包）

- 项目中的bean是如何定位、加载、解析、注册到IOC容器之中的

-
自动化配置相关类是如何解析、注册到IOC容器中的，自动化配置类的定位加载可以参考：https://mingyang.blog.csdn.net/article/details/108681609

- @EnableAutoConfiguration注解中的@Import是如何将AutoConfigurationImportSelector类定位、加载、解析、注册到IOC容器之中

##### 2.首先以BeanFactory后置处理器的postProcessBeanDefinitionRegistry方法为主线分析源码

  ```java
  	@Override
  	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
  		int registryId = System.identityHashCode(registry);
  		if (this.registriesPostProcessed.contains(registryId)) {
  			throw new IllegalStateException(
  					"postProcessBeanDefinitionRegistry already called on this post-processor against " + registry);
  		}
  		if (this.factoriesPostProcessed.contains(registryId)) {
  			throw new IllegalStateException(
  					"postProcessBeanFactory already called on this post-processor against " + registry);
  		}
  		this.registriesPostProcessed.add(registryId);
  		//此处是系统的核心入口
  		processConfigBeanDefinitions(registry);
  	}
  ```

进入processConfigBeanDefinitions方法，省略部分代码：

  ```java
  	public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
  		List<BeanDefinitionHolder> configCandidates = new ArrayList<>();
  		String[] candidateNames = registry.getBeanDefinitionNames();
  
  		for (String beanName : candidateNames) {
  			BeanDefinition beanDef = registry.getBeanDefinition(beanName);
  			if (beanDef.getAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE) != null) {
  				if (logger.isDebugEnabled()) {
  					logger.debug("Bean definition has already been processed as a configuration class: " + beanDef);
  				}
  			}
        //checkConfigurationClassCandidate方法会判断是否是一个配置类（或此配置类是否开启代理），并为BeanDefinition设置属性为lite或者full.
        //这里设置属性值为lite或full是为了后面使用
        //如果Configuration配置proxyBeanMethods代理为true则为full
        //如果加了@Bean、@Component、@ComponentScan、@Import、@ImportResource注解，则设置为lite
        //如果配置类上被@Order注解标注，则设置BeanDefinition的order属性值
  			else if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory)) {
          //这里一般解析出来的是主方法main(springboot2.3.3版本)
  				configCandidates.add(new BeanDefinitionHolder(beanDef, beanName));
  			}
  		}
  		//如果没有发现任何配置类，则直接返回
  		if (configCandidates.isEmpty()) {
  			return;
  		}
  
  		//如果适用，则按照先前确定的@Order的值排序，在上述checkConfigurationClassCandidate方法中有设置BeanDefinition的order属性的处理过程
  		configCandidates.sort((bd1, bd2) -> {
  			int i1 = ConfigurationClassUtils.getOrder(bd1.getBeanDefinition());
  			int i2 = ConfigurationClassUtils.getOrder(bd2.getBeanDefinition());
  			return Integer.compare(i1, i2);
  		});
  
  		// Detect any custom bean name generation strategy supplied through the enclosing application context
  		SingletonBeanRegistry sbr = null;
  		if (registry instanceof SingletonBeanRegistry) {
  			sbr = (SingletonBeanRegistry) registry;
  			if (!this.localBeanNameGeneratorSet) {
  				BeanNameGenerator generator = (BeanNameGenerator) sbr.getSingleton(
  						AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR);
  				if (generator != null) {
  					this.componentScanBeanNameGenerator = generator;
  					this.importBeanNameGenerator = generator;
  				}
  			}
  		}
  
  		if (this.environment == null) {
  			this.environment = new StandardEnvironment();
  		}
  
  		//创建解析每一个@Configuration类的ConfigurationClassParser实例
  		ConfigurationClassParser parser = new ConfigurationClassParser(
  				this.metadataReaderFactory, this.problemReporter, this.environment,
  				this.resourceLoader, this.componentScanBeanNameGenerator, registry);
  		//存放上述解析后配置类的BeanDefinitionHolder
      //存放spring boot 自动化配置类及其相关的BeanDefinitionHolder
      //存放主方法扫描包下所有的Bean
  		Set<BeanDefinitionHolder> candidates = new LinkedHashSet<>(configCandidates);
  		Set<ConfigurationClass> alreadyParsed = new HashSet<>(configCandidates.size());
  		do {
        //核心方法，解析总动画配置类、主方法扫描包小所有的bean、@Import导入的bean
        //这一步包含了IOC容器的定位、加载两部（但是自动化配置类的定位加载不在这里）
  			parser.parse(candidates);
        //对配置类中的@Bean method方法进行校验
  			parser.validate();
  			//获取所有bean,包括主方法扫描包下的bean、自动化配置bean、@Import导入的bean
  			Set<ConfigurationClass> configClasses = new LinkedHashSet<>(parser.getConfigurationClasses());
  			configClasses.removeAll(alreadyParsed);
  
  			//创建读取完全填充好的ConfigurationClass实例的读取器，通过其BeanDefinitionRegistry注册  			  //Bean定义 
  			if (this.reader == null) {
  				this.reader = new ConfigurationClassBeanDefinitionReader(
  						registry, this.sourceExtractor, this.resourceLoader, this.environment,
  						this.importBeanNameGenerator, parser.getImportRegistry());
  			}
        //核心方法，将完全填充好的ConfigurationClass实例转化成BeanDefinition注册入IOC容器
  			this.reader.loadBeanDefinitions(configClasses);
  			alreadyParsed.addAll(configClasses);
  
  			candidates.clear();
  			if (registry.getBeanDefinitionCount() > candidateNames.length) {
  				String[] newCandidateNames = registry.getBeanDefinitionNames();
  				Set<String> oldCandidateNames = new HashSet<>(Arrays.asList(candidateNames));
  				Set<String> alreadyParsedClasses = new HashSet<>();
  				for (ConfigurationClass configurationClass : alreadyParsed) {
  					alreadyParsedClasses.add(configurationClass.getMetadata().getClassName());
  				}
  				for (String candidateName : newCandidateNames) {
  					if (!oldCandidateNames.contains(candidateName)) {
  						BeanDefinition bd = registry.getBeanDefinition(candidateName);
  						if (ConfigurationClassUtils.checkConfigurationClassCandidate(bd, this.metadataReaderFactory) &&
  								!alreadyParsedClasses.contains(bd.getBeanClassName())) {
  							candidates.add(new BeanDefinitionHolder(bd, candidateName));
  						}
  					}
  				}
  				candidateNames = newCandidateNames;
  			}
  		}
  		while (!candidates.isEmpty());
  
  		// Register the ImportRegistry as a bean in order to support ImportAware @Configuration classes
  		if (sbr != null && !sbr.containsSingleton(IMPORT_REGISTRY_BEAN_NAME)) {
  			sbr.registerSingleton(IMPORT_REGISTRY_BEAN_NAME, parser.getImportRegistry());
  		}
  
  		if (this.metadataReaderFactory instanceof CachingMetadataReaderFactory) {
  			// Clear cache in externally provided MetadataReaderFactory; this is a no-op
  			// for a shared cache since it'll be cleared by the ApplicationContext.
  			((CachingMetadataReaderFactory) this.metadataReaderFactory).clearCache();
  		}
  	}
  ```

-
checkConfigurationClassCandidate方法用来判定类是否是一个配置类，并为BeanDefinition设置full或者lite属性，如果配置类proxyBeanMethods属性为true,即开启了代理模式，则设置为full;如果添加了@Bean、@Component、@ComponentScan、@Import、@ImportResource注解则为lite;如果配置类上被@Order注解标注，则设置BeanDefinition的order属性值；

```java
	public static boolean checkConfigurationClassCandidate(
			BeanDefinition beanDef, MetadataReaderFactory metadataReaderFactory) {

		//...省略...
		//获取@Configuration注解的属性值，如果存在
		Map<String, Object> config = metadata.getAnnotationAttributes(Configuration.class.getName());
    //Bean被@Configuration标注，并且proxyBeanMethods属性为true,则属性被设置为full
		if (config != null && !Boolean.FALSE.equals(config.get("proxyBeanMethods"))) {
			beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_FULL);
		}
    //Bean被@Configuration标注，并且添加了@Bean、@Component、@ComponentScan、@Import、@ImportResource注解，则属性设置为lite
		else if (config != null || isConfigurationCandidate(metadata)) {
			beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_LITE);
		}
		else {
			return false;
		}

		//如果配置类上被@Order注解标注，则设置BeanDefinition的order属性值
		Integer order = getOrder(metadata);
		if (order != null) {
			beanDef.setAttribute(ORDER_ATTRIBUTE, order);
		}

		return true;
	}
```

- 核心方法parse

```java
	public void parse(Set<BeanDefinitionHolder> configCandidates) {
		for (BeanDefinitionHolder holder : configCandidates) {
			BeanDefinition bd = holder.getBeanDefinition();
			try {
				if (bd instanceof AnnotatedBeanDefinition) {
          //核心方法，解析自定义或默认扫描包，并将包下的bean全部取出转换为ConfigurationClass
					parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName());
				}
				else if (bd instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) bd).hasBeanClass()) {
					parse(((AbstractBeanDefinition) bd).getBeanClass(), holder.getBeanName());
				}
				else {
					parse(bd.getBeanClassName(), holder.getBeanName());
				}
			}
			catch (BeanDefinitionStoreException ex) {
				throw ex;
			}
			catch (Throwable ex) {
				throw new BeanDefinitionStoreException(
						"Failed to parse configuration class [" + bd.getBeanClassName() + "]", ex);
			}
		}
		//核心方法
		this.deferredImportSelectorHandler.process();
	}
```

###### 2.1上述源码parse源码核心方法是processConfigurationClass

```java
	protected final void parse(AnnotationMetadata metadata, String beanName) throws IOException {
		processConfigurationClass(new ConfigurationClass(metadata, beanName), DEFAULT_EXCLUSION_FILTER);
	}
```

-
processConfigurationClass方法使用递归的方法解析配置类，将配置类、@ComponmentScan扫描包下的bean、@Import引入的非DefferedImportedSelector类注册到IOC容器，源码如下；

```java
protected void processConfigurationClass(ConfigurationClass configClass, Predicate<String> filter) throws IOException {
		if (this.conditionEvaluator.shouldSkip(configClass.getMetadata(), ConfigurationPhase.PARSE_CONFIGURATION)) {
			return;
		}

		ConfigurationClass existingClass = this.configurationClasses.get(configClass);
		if (existingClass != null) {
			if (configClass.isImported()) {
				if (existingClass.isImported()) {
					existingClass.mergeImportedBy(configClass);
				}
				// Otherwise ignore new imported config class; existing non-imported class overrides it.
				return;
			}
			else {
				// Explicit bean definition found, probably replacing an import.
				// Let's remove the old one and go with the new one.
				this.configurationClasses.remove(configClass);
				this.knownSuperclasses.values().removeIf(configClass::equals);
			}
		}

		//递归处理处理配置类及其超类
		SourceClass sourceClass = asSourceClass(configClass, filter);
		do {
      //核心处理方法
			sourceClass = doProcessConfigurationClass(configClass, sourceClass, filter);
		}
		while (sourceClass != null);
		//将处理后的类放入内存集合
		this.configurationClasses.put(configClass, configClass);
	}
```

- doProcessConfigurationClass方法

```java
	@Nullable
	protected final SourceClass doProcessConfigurationClass(
			ConfigurationClass configClass, SourceClass sourceClass, Predicate<String> filter)
			throws IOException {

		if (configClass.getMetadata().isAnnotated(Component.class.getName())) {
			//内部类处理
			processMemberClasses(configClass, sourceClass, filter);
		}

		//处理@PropertySource注解
		for (AnnotationAttributes propertySource : AnnotationConfigUtils.attributesForRepeatable(
				sourceClass.getMetadata(), PropertySources.class,
				org.springframework.context.annotation.PropertySource.class)) {
			if (this.environment instanceof ConfigurableEnvironment) {
				processPropertySource(propertySource);
			}
			else {
				logger.info("Ignoring @PropertySource annotation on [" + sourceClass.getMetadata().getClassName() +
						"]. Reason: Environment must implement ConfigurableEnvironment");
			}
		}
		//处理配置类上加@ComponentScan注解，并将扫描包下的所有bean转换成填充后的ConfigurationClass
		//此处其实就是应用程序将自定义的bean加载到IOC容器的定位、加载过程 
    //因为扫描出的类可能也加了@ComponentScan和@ComponentScans注解，因此需要进行递归解析
		Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable(
				sourceClass.getMetadata(), ComponentScans.class, ComponentScan.class);
		if (!componentScans.isEmpty() &&
				!this.conditionEvaluator.shouldSkip(sourceClass.getMetadata(), ConfigurationPhase.REGISTER_BEAN)) {
			for (AnnotationAttributes componentScan : componentScans) {
        //解析@ComponentScan和@ComponentScans配置包含的类，如basePackages="com.emily.boot"
        //那么会在这一步将这个包及其子包下的所有类解析成BeanDefinition，然后注册到IOC容器之中
				Set<BeanDefinitionHolder> scannedBeanDefinitions =
						this.componentScanParser.parse(componentScan, sourceClass.getMetadata().getClassName());
				//通过上一步扫描包com.emily.boot下的类，有可能扫描出来的类上也加了@ComponentScan和@ComponentScans注解，所以需要循环遍历一次，进行递归解析，直到类上没有@ComponentScan和@ComponentScans注解为止；
				for (BeanDefinitionHolder holder : scannedBeanDefinitions) {
					BeanDefinition bdCand = holder.getBeanDefinition().getOriginatingBeanDefinition();
					if (bdCand == null) {
						bdCand = holder.getBeanDefinition();
					}
          //判定是否是一个配置类，并设置full或lite属性，上述已讲解
					if (ConfigurationClassUtils.checkConfigurationClassCandidate(bdCand, this.metadataReaderFactory)) {
            //通过递归方式解析
						parse(bdCand.getBeanClassName(), holder.getBeanName());
					}
				}
			}
		}

		//处理@Import注解
		processImports(configClass, sourceClass, getImports(sourceClass), filter, true);

		//处理@ImportResource注解
		AnnotationAttributes importResource =
				AnnotationConfigUtils.attributesFor(sourceClass.getMetadata(), ImportResource.class);
		if (importResource != null) {
			String[] resources = importResource.getStringArray("locations");
			Class<? extends BeanDefinitionReader> readerClass = importResource.getClass("reader");
			for (String resource : resources) {
				String resolvedResource = this.environment.resolveRequiredPlaceholders(resource);
				configClass.addImportedResource(resolvedResource, readerClass);
			}
		}

		//处理加了@Bean的注解方法
		Set<MethodMetadata> beanMethods = retrieveBeanMethodMetadata(sourceClass);
		for (MethodMetadata methodMetadata : beanMethods) {
			configClass.addBeanMethod(new BeanMethod(methodMetadata, configClass));
		}

		//处理接口的默认方法实现，从JDK8开始，接口中的方法可以有自己的默认实现，因此如果这个接口中的方法加了@Bean注解，也需要被解析
		processInterfaces(configClass, sourceClass);

		//解析父类，如果被解析的配置类继承了某个类，那么配置类的父类也会被进行解析
		if (sourceClass.getMetadata().hasSuperClass()) {
			String superclass = sourceClass.getMetadata().getSuperClassName();
			if (superclass != null && !superclass.startsWith("java") &&
					!this.knownSuperclasses.containsKey(superclass)) {
				this.knownSuperclasses.put(superclass, configClass);
				// Superclass found, return its annotation metadata and recurse
				return sourceClass.getSuperClass();
			}
		}

		// No superclass -> processing is complete
		return null;
	}
```

- processImports方法，此处是读取@Import引入的类，如：@EnableAutoConfiguration注解中@Import类AutoConfigurationImportSelector，为自动化配置类的初始化做准备

```java
private void processImports(ConfigurationClass configClass, SourceClass currentSourceClass,
			Collection<SourceClass> importCandidates, Predicate<String> exclusionFilter,
			boolean checkForCircularImports) {

		if (importCandidates.isEmpty()) {
			return;
		}
		//检查是否是循环引用
		if (checkForCircularImports && isChainedImportOnStack(configClass)) {
			this.problemReporter.error(new CircularImportProblem(configClass, this.importStack));
		}
		else {
			this.importStack.push(configClass);
			try {
				for (SourceClass candidate : importCandidates) {
          //检验配置类Import引入的类是否是ImportSelector子类
					if (candidate.isAssignable(ImportSelector.class)) {
						// Candidate class is an ImportSelector -> delegate to it to determine imports
						Class<?> candidateClass = candidate.loadClass();
            //初始化引用选择器类
						ImportSelector selector = ParserStrategyUtils.instantiateClass(candidateClass, ImportSelector.class,
								this.environment, this.resourceLoader, this.registry);
						Predicate<String> selectorFilter = selector.getExclusionFilter();
						if (selectorFilter != null) {
							exclusionFilter = exclusionFilter.or(selectorFilter);
						}
            //判定引用选择器是否是DeferredImportSelector接口的实例
            //如果是则应用选择器将会在所有的配置类（不包括自动化配置类，即：spring.factories）都加载完毕后加载
						if (selector instanceof DeferredImportSelector) {
							this.deferredImportSelectorHandler.handle(configClass, (DeferredImportSelector) selector);
						}
						else {
              //获取引入的类，然后使用递归方式将这些类中同样添加了@Import注解引用的类
							String[] importClassNames = selector.selectImports(currentSourceClass.getMetadata());
							Collection<SourceClass> importSourceClasses = asSourceClasses(importClassNames, exclusionFilter);
							processImports(configClass, currentSourceClass, importSourceClasses, exclusionFilter, false);
						}
					}
					else if (candidate.isAssignable(ImportBeanDefinitionRegistrar.class)) {
						// Candidate class is an ImportBeanDefinitionRegistrar ->
						// delegate to it to register additional bean definitions
						Class<?> candidateClass = candidate.loadClass();
						ImportBeanDefinitionRegistrar registrar =
								ParserStrategyUtils.instantiateClass(candidateClass, ImportBeanDefinitionRegistrar.class,
										this.environment, this.resourceLoader, this.registry);
						configClass.addImportBeanDefinitionRegistrar(registrar, currentSourceClass.getMetadata());
					}
					else {
						// Candidate class not an ImportSelector or ImportBeanDefinitionRegistrar ->
						// process it as an @Configuration class
						this.importStack.registerImport(
								currentSourceClass.getMetadata(), candidate.getMetadata().getClassName());
						processConfigurationClass(candidate.asConfigClass(configClass), exclusionFilter);
					}
				}
			}
			catch (BeanDefinitionStoreException ex) {
				throw ex;
			}
			catch (Throwable ex) {
				throw new BeanDefinitionStoreException(
						"Failed to process import candidates for configuration class [" +
						configClass.getMetadata().getClassName() + "]", ex);
			}
			finally {
				this.importStack.pop();
			}
		}
```

###### 2.2核心方法deferredImportSelectorHandler（此处是对自动化配置类进行操作的入口）

```java
		public void process() {
      //此处获取@Import注解导入器
			List<DeferredImportSelectorHolder> deferredImports = this.deferredImportSelectors;
			this.deferredImportSelectors = null;
			try {
				if (deferredImports != null) {
					DeferredImportSelectorGroupingHandler handler = new DeferredImportSelectorGroupingHandler();
					deferredImports.sort(DEFERRED_IMPORT_COMPARATOR);
					deferredImports.forEach(handler::register);
          //核心方法，从springboot SPI加载过的自动化配置类容器中读取配置类（SpringFactoriesLoader）
					handler.processGroupImports();
				}
			}
			finally {
				this.deferredImportSelectors = new ArrayList<>();
			}
		}
```

- processGroupImports类源码

```java
		public void processGroupImports() {
			for (DeferredImportSelectorGrouping grouping : this.groupings.values()) {
				Predicate<String> exclusionFilter = grouping.getCandidateFilter();
        //getImports方法是读取自动化配置类的核心入口
        //读取spring.factories配置文件中的配置类，并过滤符合条件的配置类（如：过滤掉排除的配置类）
				grouping.getImports().forEach(entry -> {
					ConfigurationClass configurationClass = this.configurationClasses.get(entry.getMetadata());
					try {
            //配置类中可能会包含@Import注解引入的类，通过此方法将引入的类注入
						processImports(configurationClass, asSourceClass(configurationClass, exclusionFilter),
								Collections.singleton(asSourceClass(entry.getImportClassName(), exclusionFilter)),
								exclusionFilter, false);
					}
					catch (BeanDefinitionStoreException ex) {
						throw ex;
					}
					catch (Throwable ex) {
						throw new BeanDefinitionStoreException(
								"Failed to process import candidates for configuration class [" +
										configurationClass.getMetadata().getClassName() + "]", ex);
					}
				});
			}
		}
```

- getImports核心方法

```java
		public Iterable<Group.Entry> getImports() {
			for (DeferredImportSelectorHolder deferredImport : this.deferredImports) {
        //核心方法，此方法会去SpringFactoriesLoader类中加载自动化配置类
				this.group.process(deferredImport.getConfigurationClass().getMetadata(),
						deferredImport.getImportSelector());
			}
      //获取已经取得的配置类
			return this.group.selectImports();
		}
```

GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)