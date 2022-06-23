### 死磕源码系列【ImportBeanDefinitionRegistrar源码详解】

> @Configuration配置类通过@Import注解将ImportBeanDefinitionRegistrar接口的实现类添加其它bean到IOC容器中，这样可以方便的修改bean或自定义BeanDefinition；而且此过程是在所有的配置类都已经注册到IOC容器之后才触发；

##### 1.ConfigurationClassPostProcessor#processConfigBeanDefinitions方法是spring容器加载bean的入口（省略大部分代码）

```java
			//扫描指定包下所有bean、加载自动化配置类等等bean的入口方法
			parser.parse(candidates);
			parser.validate();

			Set<ConfigurationClass> configClasses = new LinkedHashSet<>(parser.getConfigurationClasses());
			//此处方法入口会将ImportBeanDefinitionRegistrar接口实现类自定义的bean注册到IOC容器
			this.reader.loadBeanDefinitions(configClasses);
```



##### 2.源码分析org.springframework.context.annotation.ConfigurationClassParser#processImports

```java
/**
* 此方法用来处理其他配置类通过@Import注解引入的类的注册处理
*/
private void processImports(ConfigurationClass configClass, SourceClass currentSourceClass,
			Collection<SourceClass> importCandidates, Predicate<String> exclusionFilter,
			boolean checkForCircularImports) {

		if (importCandidates.isEmpty()) {
			return;
		}
		//检查配置类是否存在循环引用
		if (checkForCircularImports && isChainedImportOnStack(configClass)) {
			this.problemReporter.error(new CircularImportProblem(configClass, this.importStack));
		}
		else {
			this.importStack.push(configClass);
			try {
				for (SourceClass candidate : importCandidates) {
          //判定候选类是不是ImportSelector的实例对象
					if (candidate.isAssignable(ImportSelector.class)) {
						// 候选类是ImportSelector -> delegate来确认导入
						Class<?> candidateClass = candidate.loadClass();
            //实例化ImportSelector对象
						ImportSelector selector = ParserStrategyUtils.instantiateClass(candidateClass, ImportSelector.class,
								this.environment, this.resourceLoader, this.registry);
						Predicate<String> selectorFilter = selector.getExclusionFilter();
						if (selectorFilter != null) {
							exclusionFilter = exclusionFilter.or(selectorFilter);
						}
            //判定是不是DeferredImportSelector（会在所有的配置类注册完成后加载候选类）对象
						if (selector instanceof DeferredImportSelector) {
              //将选择器添加到DeferredImportSelectorHandler实例中，预留到所有配置类加载完成后统一处理自动化配置类
							this.deferredImportSelectorHandler.handle(configClass, (DeferredImportSelector) selector);
						}
						else {
              //通过ImportSelector选择器获取候选的配置类
							String[] importClassNames = selector.selectImports(currentSourceClass.getMetadata());
							Collection<SourceClass> importSourceClasses = asSourceClasses(importClassNames, exclusionFilter);
              //通过递归的方式判定候选配置类是否存在@Import引入类（实现ImportSelector、DeferredImportSelector或者普通bean实例），如果存在获取并处理，否则直接注入到IOC容器
							processImports(configClass, currentSourceClass, importSourceClasses, exclusionFilter, false);
						}
					}
          //判定@Import注解引入的是否ImportBeanDefinitionRegistrar实例（在接口实现类可以自定义需要的BeanDefinition到容器）
					else if (candidate.isAssignable(ImportBeanDefinitionRegistrar.class)) {
						// 候选类是beanImportBeanDefinitionRegistrar ->
						// 委托给它注册其它bean
						Class<?> candidateClass = candidate.loadClass();
            //ImportBeanDefinitionRegistrar实现类实例化
						ImportBeanDefinitionRegistrar registrar =
								ParserStrategyUtils.instantiateClass(candidateClass, ImportBeanDefinitionRegistrar.class,
										this.environment, this.resourceLoader, this.registry);
            //将ImportBeanDefinitionRegistrar实例添加到配置类的ConfigurationClass实例变量importBeanDefinitionRegistrars中，供所有配置类加载完成后统一处理
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

##### 3.ConfigurationClassBeanDefinitionReader#loadBeanDefinitions

```java
	//读取配置类ConfigurationClass，判定是不是ImportBeanDefinitionRegistrar实现类
	public void loadBeanDefinitions(Set<ConfigurationClass> configurationModel) {
		TrackedConditionEvaluator trackedConditionEvaluator = new TrackedConditionEvaluator();
		for (ConfigurationClass configClass : configurationModel) {
			loadBeanDefinitionsForConfigurationClass(configClass, trackedConditionEvaluator);
		}
	}
```

##### 4.ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForConfigurationClass

```java
	//读取一个单独的ConfigurationClass类，注册bean本身（@Import引入的普通类）或者@Configuration配置类的bean方法
	private void loadBeanDefinitionsForConfigurationClass(
			ConfigurationClass configClass, TrackedConditionEvaluator trackedConditionEvaluator) {

		if (trackedConditionEvaluator.shouldSkip(configClass)) {
			String beanName = configClass.getBeanName();
			if (StringUtils.hasLength(beanName) && this.registry.containsBeanDefinition(beanName)) {
				this.registry.removeBeanDefinition(beanName);
			}
			this.importRegistry.removeImportingClass(configClass.getMetadata().getClassName());
			return;
		}
		//注册@Import引入的BeanDefinition到IOC容器
		if (configClass.isImported()) {
			registerBeanDefinitionForImportedConfigurationClass(configClass);
		}
    //注册@Configuration配置类中@Bean注解标注的类到IOC容器
		for (BeanMethod beanMethod : configClass.getBeanMethods()) {
			loadBeanDefinitionsForBeanMethod(beanMethod);
		}
		//此处是将@ImportResource引入的资源注入IOC容器入口
		loadBeanDefinitionsFromImportedResources(configClass.getImportedResources());
    //此处是核心，调用回调方法注册自定义bean的入口
		loadBeanDefinitionsFromRegistrars(configClass.getImportBeanDefinitionRegistrars());
	}
	//此处会循环调用注册bean的方法
	private void loadBeanDefinitionsFromRegistrars(Map<ImportBeanDefinitionRegistrar, AnnotationMetadata> registrars) {
		registrars.forEach((registrar, metadata) ->
				registrar.registerBeanDefinitions(metadata, this.registry, this.importBeanNameGenerator));
	}
```



GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)