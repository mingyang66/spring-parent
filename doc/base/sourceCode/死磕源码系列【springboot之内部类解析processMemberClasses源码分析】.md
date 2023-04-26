### 死磕源码系列【springboot之内部类解析processMemberClasses源码分析】

在将配置类解析为ConfigurationClass配置类时经常会遇到内部配置类的情况，遇到这种情况是如何解析的呢？本文对这一块进行一一解释；

##### ConfigurationClassParser#processMemberClasses方法解析内部类

```java
//注册配置类成员嵌套类
private void processMemberClasses(ConfigurationClass configClass, SourceClass sourceClass,
			Predicate<String> filter) throws IOException {
    //获取配置类的嵌套内部类
		Collection<SourceClass> memberClasses = sourceClass.getMemberClasses();
    //如果嵌套内部类存在
		if (!memberClasses.isEmpty()) {
      //创建指定大小的内部类集合
			List<SourceClass> candidates = new ArrayList<>(memberClasses.size());
			for (SourceClass memberClass : memberClasses) {
        //验证嵌套类是否是配置类（即：通过@Configuration、@Component、@Bean等注解标注）
				if (ConfigurationClassUtils.isConfigurationCandidate(memberClass.getMetadata()) &&
						!memberClass.getMetadata().getClassName().equals(configClass.getMetadata().getClassName())) {
					candidates.add(memberClass);
				}
			}
      //对嵌套内部类进行排序
			OrderComparator.sort(candidates);
			for (SourceClass candidate : candidates) {
				if (this.importStack.contains(configClass)) {
					this.problemReporter.error(new CircularImportProblem(configClass, this.importStack));
				}
				else {
					this.importStack.push(configClass);
					try {
            //使用递归的方式将嵌套内部类当成普通的类来处理
						processConfigurationClass(candidate.asConfigClass(configClass), filter);
					}
					finally {
						this.importStack.pop();
					}
				}
			}
		}
	}
```

上述处理嵌套内部类还有一个比较核心的方法getMemberClasses，此方法获取配置类的嵌套内部类：

```java
		public Collection<SourceClass> getMemberClasses() throws IOException {
			Object sourceToProcess = this.source;
			if (sourceToProcess instanceof Class) {
				Class<?> sourceClass = (Class<?>) sourceToProcess;
				try {
					Class<?>[] declaredClasses = sourceClass.getDeclaredClasses();
					List<SourceClass> members = new ArrayList<>(declaredClasses.length);
					for (Class<?> declaredClass : declaredClasses) {
						members.add(asSourceClass(declaredClass, DEFAULT_EXCLUSION_FILTER));
					}
					return members;
				}
				catch (NoClassDefFoundError err) {
					// getDeclaredClasses() failed because of non-resolvable dependencies
					// -> fall back to ASM below
					sourceToProcess = metadataReaderFactory.getMetadataReader(sourceClass.getName());
				}
			}

			// 基于ASM的解析，对于不可以解析的类也是安全的
      // 获取元数据读取器
			MetadataReader sourceReader = (MetadataReader) sourceToProcess;
      // 获取配置类的嵌套内部类
			String[] memberClassNames = sourceReader.getClassMetadata().getMemberClassNames();
      // 新建嵌套内部类集合
			List<SourceClass> members = new ArrayList<>(memberClassNames.length);
			for (String memberClassName : memberClassNames) {
				try {
          // 将嵌套类转换成简单的包装类，并且经过默认过滤器的过滤
					members.add(asSourceClass(memberClassName, DEFAULT_EXCLUSION_FILTER));
				}
				catch (IOException ex) {
					// Let's skip it if it's not resolvable - we're just looking for candidates
					if (logger.isDebugEnabled()) {
						logger.debug("Failed to resolve member class [" + memberClassName +
								"] - not considering it as a configuration class candidate");
					}
				}
			}
			return members;
		}
```

上述方法只有下半部分是常使用的，上半部分基本很少使用，此处不再做一一解析；

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)