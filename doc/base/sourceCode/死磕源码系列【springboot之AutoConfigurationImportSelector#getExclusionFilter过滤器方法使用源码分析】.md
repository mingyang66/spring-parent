### 死磕源码系列【springboot之AutoConfigurationImportSelector#getExclusionFilter过滤器方法使用源码分析】

AutoConfigurationImportSelector类是通过@EnableAutoConfiguration注解引入的，主要作用就是将自动化配置类加载到内存之中，除了这些还有一个强大的功能就是过滤掉不符合条件注解配置类；其中自动化配置类的过滤原理及流程已经分析过了，请参考：[死磕源码系列【springboot之OnClassCondition、OnBeanCondition、OnWebApplicationCondition条件注解源码解析】](https://mingyang.blog.csdn.net/article/details/110562664),本文只讲解基于AutoConfigurationImportSelector#getExclusionFilter过滤器的方法使用原理；

```java
	//函数式方法，验证指定的配置类是否符合条件
  @Override
	public Predicate<String> getExclusionFilter() {
		return this::shouldExclude;
	}
	//使用条件注解过滤器判定配置类是否符合条件，如果不符合返回true,符合返回false
	private boolean shouldExclude(String configurationClassName) {
		return getConfigurationClassFilter().filter(Collections.singletonList(configurationClassName)).isEmpty();
	}
```

自动化配置类的过滤是通过AutoConfigurationImportSelector#getAutoConfigurationEntry方法完成的，那么这个过滤功能会在哪里派上用场呢？其实就是在ConfigurationClassParser#processImports方法中被调用的,源码如下：

```java
	//默认过滤器
	private static final Predicate<String> DEFAULT_EXCLUSION_FILTER = className ->
			(className.startsWith("java.lang.annotation.") || className.startsWith("org.springframework.stereotype."));

//configClass 配置类的ConfigurationClass对象
//currentSourceClass 配置类的一个简单包装器对象
//配置类通过@Import注解引入的类集合
//exclusionFilter就是上述说明的函数式接口条件注解过滤器
//checkForCircularImports 是否检查@Import循环引用
private void processImports(ConfigurationClass configClass, SourceClass currentSourceClass,
			Collection<SourceClass> importCandidates, Predicate<String> exclusionFilter,
			boolean checkForCircularImports) {
		//如果配置类未通过@Import引入任何类，则直接返回
		if (importCandidates.isEmpty()) {
			return;
		}
		//循环引用处理
		if (checkForCircularImports && isChainedImportOnStack(configClass)) {
			this.problemReporter.error(new CircularImportProblem(configClass, this.importStack));
		}
		else {
			this.importStack.push(configClass);
			try {
        //遍历引用的配置类包装对象
				for (SourceClass candidate : importCandidates) {
          //引入类是否是ImportSelector接口的实例
					if (candidate.isAssignable(ImportSelector.class)) {
						// 候选类是一个ImportSelector接口子类，通过反射获取其class实例对象
						Class<?> candidateClass = candidate.loadClass();
            //将class对象转换成一个ImportSelector对象
						ImportSelector selector = ParserStrategyUtils.instantiateClass(candidateClass, ImportSelector.class,
								this.environment, this.resourceLoader, this.registry);
            //获取selector选择器中的注解条件过滤器（上述说明的过滤器）
						Predicate<String> selectorFilter = selector.getExclusionFilter();
						if (selectorFilter != null) {
              //如果过滤器存在，则和默认过滤器组成or关系
							exclusionFilter = exclusionFilter.or(selectorFilter);
						}
            //如果selector是DeferredImportSelector实例对象，则推迟到后面进行自动化配置
						if (selector instanceof DeferredImportSelector) {
							this.deferredImportSelectorHandler.handle(configClass, (DeferredImportSelector) selector);
						}
						else {
              //获取ImportSelector对象引入的所有配置类集合
							String[] importClassNames = selector.selectImports(currentSourceClass.getMetadata());
              //将所有的配置类转换成SourceClass包装器类集合
              //此方法将会调用顾虑器方法判定配置类是否符合条件
							Collection<SourceClass> importSourceClasses = asSourceClasses(importClassNames, exclusionFilter);
              //通过递归的方式将引入的配置类加入到配置类集合
							processImports(configClass, currentSourceClass, importSourceClasses, exclusionFilter, false);
						}
					}
          //判定import引入的配置类是否是ImportBeanDefinitionRegistrar实例对象
					else if (candidate.isAssignable(ImportBeanDefinitionRegistrar.class)) {
						// 配置类是ImportBeanDefinitionRegistrar实例对象
						// 通过反射方式获取其class对象
						Class<?> candidateClass = candidate.loadClass();
            //将class实例对象转换成bean对象
						ImportBeanDefinitionRegistrar registrar =
								ParserStrategyUtils.instantiateClass(candidateClass, ImportBeanDefinitionRegistrar.class,
										this.environment, this.resourceLoader, this.registry);
            //将ImportBeanDefinitionRegistrar对象添加到当前配置类ConfigurationClass对象属性之中
						configClass.addImportBeanDefinitionRegistrar(registrar, currentSourceClass.getMetadata());
					}
					else {
						// 候选类即不是ImportSelector 也不是 ImportBeanDefinitionRegistrar ->
						// 将其作为@Configuration配置类处理（包括普通的bean）
						this.importStack.registerImport(
								currentSourceClass.getMetadata(), candidate.getMetadata().getClassName());
            //将配置类放入到ConfigurationClassParser的configurationClasses属性中
            //设置ConfigurationClass属性importedBy值为configClass，后面会通过此属性值注册bean definition
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
	}
```

asSourceClasses方法过滤配置类是否符合条件并将其转换成基本的包装器：

```java
	/**
	 * Factory method to obtain a {@link SourceClass} collection from class names.
	 */
	private Collection<SourceClass> asSourceClasses(String[] classNames, Predicate<String> filter) throws IOException {
    //新建基本包装器容器对象
		List<SourceClass> annotatedClasses = new ArrayList<>(classNames.length);
		for (String className : classNames) {
      //将配置转换成基本包装器并放入容器之中
			annotatedClasses.add(asSourceClass(className, filter));
		}
		return annotatedClasses;
	}

	/**
	 * Factory method to obtain a {@link SourceClass} from a class name.
	 */
	SourceClass asSourceClass(@Nullable String className, Predicate<String> filter) throws IOException {
    //如果配置类名为null,或者配置类不符合过滤器条件，则直接返回默认包装器对象
		if (className == null || filter.test(className)) {
			return this.objectSourceClass;
		}
		if (className.startsWith("java")) {
			// Never use ASM for core java types
			try {
				return new SourceClass(ClassUtils.forName(className, this.resourceLoader.getClassLoader()));
			}
			catch (ClassNotFoundException ex) {
				throw new NestedIOException("Failed to load class [" + className + "]", ex);
			}
		}
    //返回配置类的包装器对象
		return new SourceClass(this.metadataReaderFactory.getMetadataReader(className));
	}
```

上述代码是加载经过扫描获取到的配置类使用过滤器功能的源码分析，那么自动化配置又是如何使用过滤器的呢？

```java
	public void parse(Set<BeanDefinitionHolder> configCandidates) {
		for (BeanDefinitionHolder holder : configCandidates) {
			BeanDefinition bd = holder.getBeanDefinition();
			try {
				if (bd instanceof AnnotatedBeanDefinition) {
          //加载扫描到bean使用过滤器的入口
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
		//这里是加载自动化配置使用过滤器的入口
		this.deferredImportSelectorHandler.process();
	}
```

process方法：

```java
	public void process() {
			List<DeferredImportSelectorHolder> deferredImports = this.deferredImportSelectors;
			this.deferredImportSelectors = null;
			try {
				if (deferredImports != null) {
					DeferredImportSelectorGroupingHandler handler = new DeferredImportSelectorGroupingHandler();
					deferredImports.sort(DEFERRED_IMPORT_COMPARATOR);
					deferredImports.forEach(handler::register);
          //此处调用核心处理方法
					handler.processGroupImports();
				}
			}
			finally {
				this.deferredImportSelectors = new ArrayList<>();
			}
		}
```

processGroupImports方法处理自动化配置类：

```java
		public void processGroupImports() {
			for (DeferredImportSelectorGrouping grouping : this.groupings.values()) {
        //获取DeferredImportSelector实例对象的过滤器
				Predicate<String> exclusionFilter = grouping.getCandidateFilter();
        //此处是获取自动化配置类（spring.factories中配置的类）并通过过滤器获取符合条件的配置类的入口
        //然后轮询配置类，获取配置类上通过@Import注解引入的类，并将其加入到配置类集合
				grouping.getImports().forEach(entry -> {
          //获取配置类的ConfigurationClass对象
					ConfigurationClass configurationClass = this.configurationClasses.get(entry.getMetadata());
					try {
            //通过递归的方式处理配置类，并将其引入的配置类注入配置类集合
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

总结：springboot条件注解过滤器用在三个地方，第一是在springboot通过扫描包的形式将配置类加入到配置类集合的过程中，如果配置类ImportSelector的实例（非DeferredImportSelector实例）对象时会通过过滤器判定配置类是否符合条件；第二在获取spring.factories配置文件的自动化配置类时会通过过滤器判定配置类是否符合条件；第三在获取自动化配置类后将通过@Import注解加入配置类集合之前通过过滤器判定是否符合条件；

GitHub地址：[https://github.com/mingyang66/spring-parent]（https://github.com/mingyang66/spring-parent）