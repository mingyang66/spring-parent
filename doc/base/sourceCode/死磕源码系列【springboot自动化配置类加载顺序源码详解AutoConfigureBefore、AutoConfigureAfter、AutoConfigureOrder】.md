### 死磕源码系列【springboot自动化配置类加载顺序源码详解AutoConfigureBefore、AutoConfigureAfter、AutoConfigureOrder】

>
springboot的自动化配置类默认的加载及初始化顺序是按照字母顺序来的，当然也可以通过AutoConfigureBefore、AutoConfigureAfter、AutoConfigureOrder三个注解类来控制，下面我们就看一下底层源码是如何实现的；

##### 1.查看自动化配置类加载顺序源码AutoConfigurationImportSelector.AutoConfigurationGroup#sortAutoConfigurations

```java
	
//autoConfigurationMetadata：自动化配置注解元数据对象，是PropertiesAutoConfigurationMetadata的实例对象
private List<String> sortAutoConfigurations(Set<String> configurations,
				AutoConfigurationMetadata autoConfigurationMetadata) {
    	//此处调用实际排序的方法
  		//MetadataReaderFactory是MetadataReader元数据读取器工厂类
			return new AutoConfigurationSorter(getMetadataReaderFactory(), autoConfigurationMetadata)
					.getInPriorityOrder(configurations);
		}
```

##### 2.AutoConfigurationSorter#getInPriorityOrder排序方法

```java
	List<String> getInPriorityOrder(Collection<String> classNames) {
		AutoConfigurationClasses classes = new AutoConfigurationClasses(this.metadataReaderFactory,
				this.autoConfigurationMetadata, classNames);
		List<String> orderedClassNames = new ArrayList<>(classNames);
		// Initially sort alphabetically
    //首先按照字母升序排序
		Collections.sort(orderedClassNames);
		// Then sort by order
    // 按照@AutoConfigureOrder指定的顺序排续
		orderedClassNames.sort((o1, o2) -> {
      //如果配置类没有指定order,则getOrder方法将会给与默认值0
			int i1 = classes.get(o1).getOrder();
			int i2 = classes.get(o2).getOrder();
			return Integer.compare(i1, i2);
		});
		// 接下来按照@AutoConfigureBefore @AutoConfigureAfter注解调整顺序
		orderedClassNames = sortByAnnotation(classes, orderedClassNames);
		return orderedClassNames;
	}
```

##### 3.AutoConfigurationSorter#sortByAnnotation方法按照@AutoConfigureBefore @AutoConfigureAfter注解调整配置类的顺序

```java
	private List<String> sortByAnnotation(AutoConfigurationClasses classes, List<String> classNames) {
		List<String> toSort = new ArrayList<>(classNames);
    //获取所有的配置类
		toSort.addAll(classes.getAllNames());
		Set<String> sorted = new LinkedHashSet<>();
		Set<String> processing = new LinkedHashSet<>();
		while (!toSort.isEmpty()) {
			doSortByAfterAnnotation(classes, toSort, sorted, processing, null);
		}
		sorted.retainAll(classNames);
		return new ArrayList<>(sorted);
	}
```

##### 4.AutoConfigurationSorter#doSortByAfterAnnotation方法通过递归的方式调整配置类的顺讯

```java
	private void doSortByAfterAnnotation(AutoConfigurationClasses classes, List<String> toSort, Set<String> sorted,
			Set<String> processing, String current) {
		if (current == null) {
      //从要排序集合删除第一个排序类
			current = toSort.remove(0);
		}
    //将正在处理类添加到集合
		processing.add(current);
    //getClassesRequestedAfter方法获取指定类的after、before集合的并集
		for (String after : classes.getClassesRequestedAfter(current)) {
			Assert.state(!processing.contains(after),
					"AutoConfigure cycle detected between " + current + " and " + after);
      //通过递归的方式将当前类的after、before类添加到已排序集合
			if (!sorted.contains(after) && toSort.contains(after)) {
				doSortByAfterAnnotation(classes, toSort, sorted, processing, after);
			}
		}
    //将正在处理类添加到集合
		processing.remove(current);
    //将处理过的类添加到已处理集合
		sorted.add(current);
	}
```

##### 5.内部类4.AutoConfigurationSorter#AutoConfigurationClass源码详解

```java
	private static class AutoConfigurationClass {
		//配置类名
		private final String className;

		private final MetadataReaderFactory metadataReaderFactory;

		private final AutoConfigurationMetadata autoConfigurationMetadata;

		private volatile AnnotationMetadata annotationMetadata;
		//配置类的before类集合
		private volatile Set<String> before;
		//配置类的after集合
		private volatile Set<String> after;

		AutoConfigurationClass(String className, MetadataReaderFactory metadataReaderFactory,
				AutoConfigurationMetadata autoConfigurationMetadata) {
			this.className = className;
			this.metadataReaderFactory = metadataReaderFactory;
			this.autoConfigurationMetadata = autoConfigurationMetadata;
		}

		boolean isAvailable() {
			try {
        //判定当前配置类是否已经被自动化配置处理器类处理过
				if (!wasProcessed()) {
          //如果没有被自动化配置处理器处理过，则通过工厂方法获取注解元数据对象
					getAnnotationMetadata();
				}
				return true;
			}
			catch (Exception ex) {
				return false;
			}
		}
		//获取@AutoConfigureBefore注解指定的值（通过注解元数据方式获取）
    //如果不存在，则通过工厂方法获取注解元数据，然后获取注解属性值
		Set<String> getBefore() {
			if (this.before == null) {
				this.before = (wasProcessed() ? this.autoConfigurationMetadata.getSet(this.className,
						"AutoConfigureBefore", Collections.emptySet()) : getAnnotationValue(AutoConfigureBefore.class));
			}
			return this.before;
		}
		//获取@AutoConfigureAfter注解指定的值（通过注解元数据方式获取）
    //如果不存在，则通过工厂方法获取注解元数据，然后获取注解属性值
		Set<String> getAfter() {
			if (this.after == null) {
				this.after = (wasProcessed() ? this.autoConfigurationMetadata.getSet(this.className,
						"AutoConfigureAfter", Collections.emptySet()) : getAnnotationValue(AutoConfigureAfter.class));
			}
			return this.after;
		}
		//获取配置类order值，默认值是：0
		private int getOrder() {
			if (wasProcessed()) {
				return this.autoConfigurationMetadata.getInteger(this.className, "AutoConfigureOrder",
						AutoConfigureOrder.DEFAULT_ORDER);
			}
			Map<String, Object> attributes = getAnnotationMetadata()
					.getAnnotationAttributes(AutoConfigureOrder.class.getName());
			return (attributes != null) ? (Integer) attributes.get("value") : AutoConfigureOrder.DEFAULT_ORDER;
		}
		//判定类是否正在被注解处理器处理
		private boolean wasProcessed() {
			return (this.autoConfigurationMetadata != null
					&& this.autoConfigurationMetadata.wasProcessed(this.className));
		}
		//获取注解的属性值
		private Set<String> getAnnotationValue(Class<?> annotation) {
			Map<String, Object> attributes = getAnnotationMetadata().getAnnotationAttributes(annotation.getName(),
					true);
			if (attributes == null) {
				return Collections.emptySet();
			}
			Set<String> value = new LinkedHashSet<>();
			Collections.addAll(value, (String[]) attributes.get("value"));
			Collections.addAll(value, (String[]) attributes.get("name"));
			return value;
		}
		//获取类的MetadataReader对象
		private AnnotationMetadata getAnnotationMetadata() {
			if (this.annotationMetadata == null) {
				try {
					MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(this.className);
					this.annotationMetadata = metadataReader.getAnnotationMetadata();
				}
				catch (IOException ex) {
					throw new IllegalStateException("Unable to read meta-data for class " + this.className, ex);
				}
			}
			return this.annotationMetadata;
		}

	}
```

##### 6.org.springframework.boot.autoconfigure.AutoConfigurationSorter.AutoConfigurationClasses自动化配置类集合

```java
	private static class AutoConfigurationClasses {

		private final Map<String, AutoConfigurationClass> classes = new HashMap<>();

		AutoConfigurationClasses(MetadataReaderFactory metadataReaderFactory,
				AutoConfigurationMetadata autoConfigurationMetadata, Collection<String> classNames) {
			addToClasses(metadataReaderFactory, autoConfigurationMetadata, classNames, true);
		}

		Set<String> getAllNames() {
			return this.classes.keySet();
		}

		private void addToClasses(MetadataReaderFactory metadataReaderFactory,
				AutoConfigurationMetadata autoConfigurationMetadata, Collection<String> classNames, boolean required) {
			for (String className : classNames) {
				if (!this.classes.containsKey(className)) {
          //获取自动化配置类实例对象
					AutoConfigurationClass autoConfigurationClass = new AutoConfigurationClass(className,
							metadataReaderFactory, autoConfigurationMetadata);
          //判定配置类是否可用
					boolean available = autoConfigurationClass.isAvailable();
					if (required || available) {
            //将符合条件的配置类加入到配置类集合
						this.classes.put(className, autoConfigurationClass);
					}
          //如果当前类有效，递归处理@AutoConfigureBefore、@AutoConfigureAfter注解指定的类
					if (available) {
						addToClasses(metadataReaderFactory, autoConfigurationMetadata,
								autoConfigurationClass.getBefore(), false);
						addToClasses(metadataReaderFactory, autoConfigurationMetadata,
								autoConfigurationClass.getAfter(), false);
					}
				}
			}
		}
		//获取指定类的自动化配置类对象
		AutoConfigurationClass get(String className) {
			return this.classes.get(className);
		}
		//获取指定类after、before类的集合
		Set<String> getClassesRequestedAfter(String className) {
      //获取指定类after属性对应的集合
			Set<String> classesRequestedAfter = new LinkedHashSet<>(get(className).getAfter());
      //获取before属性对应的集合
			this.classes.forEach((name, autoConfigurationClass) -> {
				if (autoConfigurationClass.getBefore().contains(className)) {
					classesRequestedAfter.add(name);
				}
			});
			return classesRequestedAfter;
		}

	}
```

总结：自动化配置类的加载顺序是先按照字母升序排列、再按照@AutoConfigureOrder指定的顺讯排序、最后按照@AutoConfigureAfter和@AutoConfigureBefore注解指定的相对顺序排列；另外这里面还涉及了自动化配置的原数据的读取问题，如果在原数据配置文件中配置了注解相关配置，则可以直接拿来用；否则需要通过元数据读取器工厂类获取元数据读取器，然后再获取注解属性值信息，效率相对较低；

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)