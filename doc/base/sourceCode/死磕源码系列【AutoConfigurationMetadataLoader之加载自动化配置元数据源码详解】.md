### 死磕源码系列【AutoConfigurationMetadataLoader之加载自动化配置元数据源码详解】

>
spring-autoconfigure-metadata.properties配置文件配置JavaConfig自动化配置类注解的元数据信息，这些原数据信息可以帮助springboot提前过滤掉不符合条件的配置类，提高加载启动应用程序的效率。

##### 1.AutoConfigurationMetadata接口提供自动化配置类注释处理器元数据的访问

```java
public interface AutoConfigurationMetadata {

	/**
	 * 如果指定的类名由注解处理器处理过，则返回true
	 */
	boolean wasProcessed(String className);

	/**
	 * 从元数据中获取Integer值
	 * @param className 类名
	 * @param key 元数据key
	 * @return 元数据值或null
	 */
	Integer getInteger(String className, String key);

	/**
	 * 从元数据中获取Integer值
	 * @param className 类名
	 * @param key 元数据key
	 * @param defaultValue 默认值
	 * @return 元数据值或者默认值
	 */
	Integer getInteger(String className, String key, Integer defaultValue);

	/**
	 * 从元数据中获取Set集合值
	 * @param className 类名
	 * @param key 元数据key
	 * @return 元数据值或null
	 */
	Set<String> getSet(String className, String key);

	/**
	 * 从元数据中获取一个Set集合值
	 * @param className 类名
	 * @param key 元数据key
	 * @param defaultValue 默认值
	 * @return 元数据值或默认值
	 */
	Set<String> getSet(String className, String key, Set<String> defaultValue);

	/**
	 * 从原数据中获取字符串值
	 * @param className 类名
	 * @param key 元数据key
	 * @return 元数据值或null
	 */
	String get(String className, String key);

	/**
	 * 从元数据中获取字符串值
	 * @param className 类名
	 * @param key 元数据key
	 * @param defaultValue 默认值
	 * @return 元数据值或null
	 */
	String get(String className, String key, String defaultValue);

}
```

##### 2.PropertiesAutoConfigurationMetadata内部类是AutoConfigurationMetadata的唯一实现类

```java
	private static class PropertiesAutoConfigurationMetadata implements AutoConfigurationMetadata {

		private final Properties properties;
		//Properties属性对象作为构造函数的参数
		PropertiesAutoConfigurationMetadata(Properties properties) {
			this.properties = properties;
		}
		//如果properties对象中包含指定的类，则返回true
		@Override
		public boolean wasProcessed(String className) {
			return this.properties.containsKey(className);
		}
		//获取Integer类型的元数据
		@Override
		public Integer getInteger(String className, String key) {
			return getInteger(className, key, null);
		}
		//获取Integer类型的元数据
		@Override
		public Integer getInteger(String className, String key, Integer defaultValue) {
			String value = get(className, key);
			return (value != null) ? Integer.valueOf(value) : defaultValue;
		}
		//根据类名及key获取自动化配置类元数据集合
		@Override
		public Set<String> getSet(String className, String key) {
			return getSet(className, key, null);
		}
		//根据类名及key获取自动化配置类元数据集合
		@Override
		public Set<String> getSet(String className, String key, Set<String> defaultValue) {
			String value = get(className, key);
			return (value != null) ? StringUtils.commaDelimitedListToSet(value) : defaultValue;
		}
		//根据类名及key获取自动化配置类的元数据
		@Override
		public String get(String className, String key) {
			return get(className, key, null);
		}
		//根据类名及key获取自动化配置类的元数据值
		@Override
		public String get(String className, String key, String defaultValue) {
			String value = this.properties.getProperty(className + "." + key);
			return (value != null) ? value : defaultValue;
		}

	}

```

##### 3.AutoConfigurationMetadataLoader是自动化配置元数据处理器类

```java
final class AutoConfigurationMetadataLoader {
	//自动化配置元数据配置文件
	protected static final String PATH = "META-INF/spring-autoconfigure-metadata.properties";

	private AutoConfigurationMetadataLoader() {
	}
	//加载元数据
	static AutoConfigurationMetadata loadMetadata(ClassLoader classLoader) {
		return loadMetadata(classLoader, PATH);
	}
	//此方法会将所有jar包下的自动化配置元数据加载到properties属性对象
	static AutoConfigurationMetadata loadMetadata(ClassLoader classLoader, String path) {
		try {
			Enumeration<URL> urls = (classLoader != null) ? classLoader.getResources(path)
					: ClassLoader.getSystemResources(path);
			Properties properties = new Properties();
			while (urls.hasMoreElements()) {
				properties.putAll(PropertiesLoaderUtils.loadProperties(new UrlResource(urls.nextElement())));
			}
      //获取自动化配置元数据对象
			return loadMetadata(properties);
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Unable to load @ConditionalOnClass location [" + path + "]", ex);
		}
	}
	//以自动化配置元数据属性对象properties为参数创建元数据对象
	static AutoConfigurationMetadata loadMetadata(Properties properties) {
		return new PropertiesAutoConfigurationMetadata(properties);
	}
	}
```

------

##### 4.ConfigurationClassFilter配置类过滤器

```java
	private static class ConfigurationClassFilter {
		//自动化配置注解原数据集合类
		private final AutoConfigurationMetadata autoConfigurationMetadata;
		//过滤器类，spring.factories配置文件中org.springframework.boot.autoconfigure.AutoConfigurationImportFilter指定的引入过滤器
		private final List<AutoConfigurationImportFilter> filters;

		ConfigurationClassFilter(ClassLoader classLoader, List<AutoConfigurationImportFilter> filters) {
      //通过自动化配置注解元数据集合对象获取元数据集
			this.autoConfigurationMetadata = AutoConfigurationMetadataLoader.loadMetadata(classLoader);
			this.filters = filters;
		}
		//使用指定的过滤器将配置集合中的配置类进行过滤，不符合条件直接去掉，提升后续系统性能
		List<String> filter(List<String> configurations) {
			long startTime = System.nanoTime();
      //将配置类转换为数组
			String[] candidates = StringUtils.toStringArray(configurations);
      //是否有需要跳过的配置类（即：不符合过滤器条件的类）
			boolean skipped = false;
			for (AutoConfigurationImportFilter filter : this.filters) {
				boolean[] match = filter.match(candidates, this.autoConfigurationMetadata);
				for (int i = 0; i < match.length; i++) {
          //如果有不匹配的配置类将数组中的值置为null,skipped属性赋值为true
					if (!match[i]) {
						candidates[i] = null;
						skipped = true;
					}
				}
			}
      //如果没有需要跳过的配置类（配置类都通过了过滤器条件），则直接返回
			if (!skipped) {
				return configurations;
			}
			List<String> result = new ArrayList<>(candidates.length);
      //去除掉数组中值为null的数据
			for (String candidate : candidates) {
				if (candidate != null) {
					result.add(candidate);
				}
			}
			if (logger.isTraceEnabled()) {
				int numberFiltered = configurations.size() - result.size();
				logger.trace("Filtered " + numberFiltered + " auto configuration class in "
						+ TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + " ms");
			}
			return result;
		}

	}
```

##### 5.AutoConfigurationImportSelector#getAutoConfigurationImportFilters获取spring.factories配置文件中的过滤器类

```java
	protected List<AutoConfigurationImportFilter> getAutoConfigurationImportFilters() {
		return SpringFactoriesLoader.loadFactories(AutoConfigurationImportFilter.class, this.beanClassLoader);
	}
```

spring.factories配置文件：

```properties
# Auto Configuration Import Filters
org.springframework.boot.autoconfigure.AutoConfigurationImportFilter=\
org.springframework.boot.autoconfigure.condition.OnBeanCondition,\
org.springframework.boot.autoconfigure.condition.OnClassCondition,\
org.springframework.boot.autoconfigure.condition.OnWebApplicationCondition
```

##### 6.AutoConfigurationImportSelector#getConfigurationClassFilter获取ConfigurationClassFilter对象

```java

	private ConfigurationClassFilter getConfigurationClassFilter() {
		if (this.configurationClassFilter == null) {
      //获取过滤器集合
			List<AutoConfigurationImportFilter> filters = getAutoConfigurationImportFilters();
			for (AutoConfigurationImportFilter filter : filters) {
        //调用过滤器的aware初始化属性方法
				invokeAwareMethods(filter);
			}
      //创建配置类过滤器集合对象
			this.configurationClassFilter = new ConfigurationClassFilter(this.beanClassLoader, filters);
		}
		return this.configurationClassFilter;
	}
```

##### 7.AutoConfigurationImportSelector#getAutoConfigurationEntry

```java
	protected AutoConfigurationEntry getAutoConfigurationEntry(AnnotationMetadata annotationMetadata) {
    //判定自动化配置是否开启
		if (!isEnabled(annotationMetadata)) {
			return EMPTY_ENTRY;
		}
    //获取注解属性对象
		AnnotationAttributes attributes = getAttributes(annotationMetadata);
    //获取自动化配置类集合
		List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
    //去重
		configurations = removeDuplicates(configurations);
    //获取需要排除类的集合
		Set<String> exclusions = getExclusions(annotationMetadata, attributes);
    //校验排除配置类的有效性
		checkExcludedClasses(configurations, exclusions);
    //从集合中删除要排除的类
		configurations.removeAll(exclusions);
    //使用过滤器过滤配置类，将不符合条件的去除掉
		configurations = getConfigurationClassFilter().filter(configurations);
    //自动化配置类import之后触发事件
		fireAutoConfigurationImportEvents(configurations, exclusions);
		return new AutoConfigurationEntry(configurations, exclusions);
	}
```

此方法会被AutoConfigurationImportSelector.AutoConfigurationGroup#process方法调用，而process方法会在配置类都以ConfigurationClass对象的形式加入到内存之后被ConfigurationClassParser.DeferredImportSelectorGrouping#getImports调用，具体的在之前的文章已经分析过了，大家可以翻阅之前的文章查看；

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)