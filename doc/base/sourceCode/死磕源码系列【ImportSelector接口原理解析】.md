### 死磕源码系列【ImportSelector接口原理解析】

> ImportSelector接口是spring中导入外部配置的核心接口，根据给定的条件（通常是一个或多个注释属性）判定要导入那个配置类，在spring自动化配置和@EnableXXX中都有它的存在；

##### 1.ImportSelector接口源码解析

```java
/**
 * Interface to be implemented by types that determine which @{@link Configuration}
 * class(es) should be imported based on a given selection criteria, usually one or
 * more annotation attributes.
 *
 * <p>An {@link ImportSelector} may implement any of the following
 * {@link org.springframework.beans.factory.Aware Aware} interfaces,
 * and their respective methods will be called prior to {@link #selectImports}:
 * <ul>
 * <li>{@link org.springframework.context.EnvironmentAware EnvironmentAware}</li>
 * <li>{@link org.springframework.beans.factory.BeanFactoryAware BeanFactoryAware}</li>
 * <li>{@link org.springframework.beans.factory.BeanClassLoaderAware BeanClassLoaderAware}</li>
 * <li>{@link org.springframework.context.ResourceLoaderAware ResourceLoaderAware}</li>
 * </ul>
 *
 * <p>Alternatively, the class may provide a single constructor with one or more of
 * the following supported parameter types:
 * <ul>
 * <li>{@link org.springframework.core.env.Environment Environment}</li>
 * <li>{@link org.springframework.beans.factory.BeanFactory BeanFactory}</li>
 * <li>{@link java.lang.ClassLoader ClassLoader}</li>
 * <li>{@link org.springframework.core.io.ResourceLoader ResourceLoader}</li>
 * </ul>
 *
 * <p>{@code ImportSelector} implementations are usually processed in the same way
 * as regular {@code @Import} annotations, however, it is also possible to defer
 * selection of imports until all {@code @Configuration} classes have been processed
 * (see {@link DeferredImportSelector} for details).
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see DeferredImportSelector
 * @see Import
 * @see ImportBeanDefinitionRegistrar
 * @see Configuration
 */
public interface ImportSelector {

	/**
	 * Select and return the names of which class(es) should be imported based on
	 * the {@link AnnotationMetadata} of the importing @{@link Configuration} class.
	 * @return the class names, or an empty array if none
	 */
	String[] selectImports(AnnotationMetadata importingClassMetadata);

	/**
	 * Return a predicate for excluding classes from the import candidates, to be
	 * transitively applied to all classes found through this selector's imports.
	 * <p>If this predicate returns {@code true} for a given fully-qualified
	 * class name, said class will not be considered as an imported configuration
	 * class, bypassing class file loading as well as metadata introspection.
	 * @return the filter predicate for fully-qualified candidate class names
	 * of transitively imported configuration classes, or {@code null} if none
	 * @since 5.2.4
	 */
	@Nullable
	default Predicate<String> getExclusionFilter() {
		return null;
	}

}
```

接口文档已经说的很明白，其主要作用是收集需要导入的配置类，如果该接口的实现类同时实现了org.springframework.beans.factory.Aware相关接口，如：EnvironmentAware、BeanFactoryAware、BeanClassLoaderAware、ResourceLoaderAware等，那么在调用其selectImports方法之前先调用上述接口中的回调方法；如果需要在所有的@Configuration处理完再导入，可以实现DeferredImportSelector接口；

------

##### 2.DeferredImportSelector接口源码解析

DeferredImportSelector接口是ImportSelector接口的子接口，该接口会在所有的@Configuration配置类（不包括自动化配置类，即spring.factories文件中的配置类）处理完成后运行；当选择器和@Conditional条件注解一起使用时是特别有用的，此接口还可以和接口Ordered或者@Ordered一起使用，定义多个选择器的优先级；

```java
/**
 * A variation of {@link ImportSelector} that runs after all {@code @Configuration} beans
 * have been processed. This type of selector can be particularly useful when the selected
 * imports are {@code @Conditional}.
 *
 * <p>Implementations can also extend the {@link org.springframework.core.Ordered}
 * interface or use the {@link org.springframework.core.annotation.Order} annotation to
 * indicate a precedence against other {@link DeferredImportSelector DeferredImportSelectors}.
 *
 * <p>Implementations may also provide an {@link #getImportGroup() import group} which
 * can provide additional sorting and filtering logic across different selectors.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @since 4.0
 */
public interface DeferredImportSelector extends ImportSelector {

	/**
	 * 返回指定的导入结果集
	 */
	@Nullable
	default Class<? extends Group> getImportGroup() {
		return null;
	}


	/**
	 * 用于从不同DeferredImportSelector中获取需要导入类的结果集
	 */
	interface Group {

		/**
		 * 根据AnnotationMetadata注解元数据获取@Configuration配置的@Import注解导入的DeferredImportSelector选择器对应的bean
		 */
		void process(AnnotationMetadata metadata, DeferredImportSelector selector);

		/**
		 * 返回类应该导入的Entry
		 */
		Iterable<Entry> selectImports();


		/**
		 * 存放要导入类的全限定名及AnnotationMetadata注解元数据
		 */
		class Entry {

			private final AnnotationMetadata metadata;

			private final String importClassName;

			public Entry(AnnotationMetadata metadata, String importClassName) {
				this.metadata = metadata;
				this.importClassName = importClassName;
			}

			/**
			 * 返回要引入的Configuration类的AnnotationMetadata注解元数据
			 */
			public AnnotationMetadata getMetadata() {
				return this.metadata;
			}

			/**
			 * 返回要导入类的全限定名
			 */
			public String getImportClassName() {
				return this.importClassName;
			}

			@Override
			public boolean equals(@Nullable Object other) {
				if (this == other) {
					return true;
				}
				if (other == null || getClass() != other.getClass()) {
					return false;
				}
				Entry entry = (Entry) other;
				return (this.metadata.equals(entry.metadata) && this.importClassName.equals(entry.importClassName));
			}

			@Override
			public int hashCode() {
				return (this.metadata.hashCode() * 31 + this.importClassName.hashCode());
			}

			@Override
			public String toString() {
				return this.importClassName;
			}
		}
	}

}
```

------

##### 3.示例，AutoConfigurationImportSelector是DeferredImportSelector接口的实现类，用于处理EnableAutoConfiguration自动化配置，

我们知道SpringFactoriesLoader类是自动化配置的核心类，用来将spring.factories配置文件中定义的类加载到内存之中，供后面的程序将其注册到IOC容器之中；AutoConfigurationImportSelector类是DeferredImportSelector接口的一个子类，它的作用就是将SpringFactoriesLoader类加载到内中的配置类获取到，交给后置处理器加载到内存中（不是本文重点）；

AutoConfigurationGroup是一个静态内部类，实现了DeferredImportSelector.Group接口，所以其作用是根据注解的AnnotationMetadata元数据获取导入的DeferredImportSelector接口实现类对应的自动化配置类；

- AutoConfigurationImportSelector.AutoConfigurationGroup类的核心方法process用于获取自动化配置类：

```java
		//存放配置类全限定名和注解元数据类
		private final Map<String, AnnotationMetadata> entries = new LinkedHashMap<>();
		//存放配置类的实体对象（包含需要导入的配置类、排除的配置类）
		private final List<AutoConfigurationEntry> autoConfigurationEntries = new ArrayList<>();		
		@Override
		public void process(AnnotationMetadata annotationMetadata, DeferredImportSelector deferredImportSelector) {
			//获取spring.factories配置文件中的配置类（包括需要导入的、不需要导入的）
			AutoConfigurationEntry autoConfigurationEntry = ((AutoConfigurationImportSelector) deferredImportSelector)
					.getAutoConfigurationEntry(annotationMetadata);
			this.autoConfigurationEntries.add(autoConfigurationEntry);
			for (String importClassName : autoConfigurationEntry.getConfigurations()) {
				this.entries.putIfAbsent(importClassName, annotationMetadata);
			}
		}
```

- AutoConfigurationEntry类用于存放排除掉的配置类，以及需要导入的配置类：

```java
	protected static class AutoConfigurationEntry {
		//需要导入的配置类
		private final List<String> configurations;
		//排除不用导入的配置类
		private final Set<String> exclusions;
  }  
```

- AutoConfigurationImportSelector#getAutoConfigurationEntry方法获取基于配置类注解的AnnotationMetaData元数据导入Configuration配置类

```java
	protected AutoConfigurationEntry getAutoConfigurationEntry(AnnotationMetadata annotationMetadata) {
		if (!isEnabled(annotationMetadata)) {
			return EMPTY_ENTRY;
		}
    //获取注解的属性配置（exclude和excludeName）
		AnnotationAttributes attributes = getAttributes(annotationMetadata);
    //获取自动化配置文件spirng.factories中的配置类
		List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
    //删除List中重复的配置类（去重方法值得参考）
		configurations = removeDuplicates(configurations);
    //获取排除导入的配置类（包括spring.autoconfigure.exclude属性配置及注解属性exclude和excludeName）
		Set<String> exclusions = getExclusions(annotationMetadata, attributes);
    //检验排除类
		checkExcludedClasses(configurations, exclusions);
    //删除掉排除的类
		configurations.removeAll(exclusions);
    //获取过滤器，并对配置类进行过滤
		configurations = getConfigurationClassFilter().filter(configurations);
    //触发自动化配置导入事件
		fireAutoConfigurationImportEvents(configurations, exclusions);
		return new AutoConfigurationEntry(configurations, exclusions);
	}
```

-
AutoConfigurationImportSelector#getCandidateConfigurations方法用于获取spring.factories配置文件中的配置类（其实际获取是直接从SpringFactoriesLoader类中的cache获取的，已经在初始化器阶段加载到缓存中了）：

```java
	protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
    //获取自动化配置对应spring.factories文件中的配置类，
		List<String> configurations = SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(),
				getBeanClassLoader());
		return configurations;
	}
```

返回加载配置类：

```java
	protected Class<?> getSpringFactoriesLoaderFactoryClass() {
		return EnableAutoConfiguration.class;
	}
```

- AutoConfigurationImportSelector#removeDuplicates方法删除重复的配置类

```java
	protected final <T> List<T> removeDuplicates(List<T> list) {
		return new ArrayList<>(new LinkedHashSet<>(list));
	}
```

很好的去重思路，以后可以参考使用；

- AutoConfigurationImportSelector#getExclusions获取排除导入的配置类

```java
	protected Set<String> getExclusions(AnnotationMetadata metadata, AnnotationAttributes attributes) {
		Set<String> excluded = new LinkedHashSet<>();
		//获取exclude属性指定的配置类
		excluded.addAll(asList(attributes, "exclude"));
		//获取excludeName属性指定的配置类
		excluded.addAll(Arrays.asList(attributes.getStringArray("excludeName")));
		//获取spring.autoconfigure.exclude属性指定的配置类
		excluded.addAll(getExcludeAutoConfigurationsProperty());
		return excluded;
	}
```

AutoConfigurationImportSelector#getExcludeAutoConfigurationsProperty获取spring.autoconfigure.exclude属性配置类

```java
	private static final String PROPERTY_NAME_AUTOCONFIGURE_EXCLUDE = "spring.autoconfigure.exclude";
	protected List<String> getExcludeAutoConfigurationsProperty() {
		Environment environment = getEnvironment();
		if (environment == null) {
			return Collections.emptyList();
		}
		if (environment instanceof ConfigurableEnvironment) {
			Binder binder = Binder.get(environment);
      //获取配置文件中排除导入配置类
			return binder.bind(PROPERTY_NAME_AUTOCONFIGURE_EXCLUDE, String[].class).map(Arrays::asList)
					.orElse(Collections.emptyList());
		}
		String[] excludes = environment.getProperty(PROPERTY_NAME_AUTOCONFIGURE_EXCLUDE, String[].class);
		return (excludes != null) ? Arrays.asList(excludes) : Collections.emptyList();
	}
```

- AutoConfigurationImportSelector#getConfigurationClassFilter

```java
	private ConfigurationClassFilter getConfigurationClassFilter() {
		if (this.configurationClassFilter == null) {
			List<AutoConfigurationImportFilter> filters = getAutoConfigurationImportFilters();
			for (AutoConfigurationImportFilter filter : filters) {
				invokeAwareMethods(filter);
			}
			this.configurationClassFilter = new ConfigurationClassFilter(this.beanClassLoader, filters);
		}
		return this.configurationClassFilter;
	}
```

- AutoConfigurationImportSelector#fireAutoConfigurationImportEvents

```java
	private void fireAutoConfigurationImportEvents(List<String> configurations, Set<String> exclusions) {
		List<AutoConfigurationImportListener> listeners = getAutoConfigurationImportListeners();
		if (!listeners.isEmpty()) {
			AutoConfigurationImportEvent event = new AutoConfigurationImportEvent(this, configurations, exclusions);
			for (AutoConfigurationImportListener listener : listeners) {
				invokeAwareMethods(listener);
				listener.onAutoConfigurationImportEvent(event);
			}
		}
	}
```

------

- AutoConfigurationImportSelector.AutoConfigurationGroup#selectImports获取上述process方法处理后的配置类

```java
		@Override
		public Iterable<Entry> selectImports() {
			if (this.autoConfigurationEntries.isEmpty()) {
				return Collections.emptyList();
			}
      //获取所有需要排除的配置类
			Set<String> allExclusions = this.autoConfigurationEntries.stream()
					.map(AutoConfigurationEntry::getExclusions).flatMap(Collection::stream).collect(Collectors.toSet());
      //获取所有经过自动化配置过滤器的配置类
			Set<String> processedConfigurations = this.autoConfigurationEntries.stream()
					.map(AutoConfigurationEntry::getConfigurations).flatMap(Collection::stream)
					.collect(Collectors.toCollection(LinkedHashSet::new));
      //排除过滤后配置类中需要排除的类
			processedConfigurations.removeAll(allExclusions);

			return sortAutoConfigurations(processedConfigurations, getAutoConfigurationMetadata()).stream()
					.map((importClassName) -> new Entry(this.entries.get(importClassName), importClassName))
					.collect(Collectors.toList());
		}
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)