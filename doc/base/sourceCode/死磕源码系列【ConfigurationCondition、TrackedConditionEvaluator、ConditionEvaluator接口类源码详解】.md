### 死磕源码系列【ConfigurationCondition、TrackedConditionEvaluator、ConditionEvaluator接口类源码详解】

>
ConfigurationCondition接口继承自Condition接口，Condition接口只有一个matches方法，用来判定是否符合条件；ConfigurationCondition接口中有一个方法getConfigurationPhase，返回一个枚举值。

源码如下：

```java
public interface ConfigurationCondition extends Condition {

	/**
	 * Return the {@link ConfigurationPhase} in which the condition should be evaluated.
	 */
	ConfigurationPhase getConfigurationPhase();


	/**
	 * 根据条件评估出的过滤阶段枚举值
	 */
	enum ConfigurationPhase {

		/**
		 * 配置类是否需要在将其转换为ConfigurationClass之前进行评估是否符合条件，否则直接过滤掉
		 */
		PARSE_CONFIGURATION,

		/**
		 * 配置类是否需要在将其注册到IOC容器之前进行评估是否符合条件，否则直接过滤掉
		 */
		REGISTER_BEAN
	}

}

```

>
总结来说上述配置阶段枚举值的意思就是在什么阶段对bean进行过滤，PARSE_CONFIGURATION值的意思是将bean转换为ConfigurationClass类之前进行过滤，REGISTER_BEAN值的意思是在将bean的ConfigurationClass转换为BeanDefinition并注册到IOC容器之前进行过滤；

------

##### ConfigurationClassBeanDefinitionReader.TrackedConditionEvaluator内部类源码详解

```java
	private final ConditionEvaluator conditionEvaluator;
/**
	 * 评估 {@code @Conditional} 注解, 根据结果判定是否需要将类引入并注入IOC容器
	 */
	private class TrackedConditionEvaluator {

		private final Map<ConfigurationClass, Boolean> skipped = new HashMap<>();

		public boolean shouldSkip(ConfigurationClass configClass) {
      //获取是否已经存在配置类的计算结果
			Boolean skip = this.skipped.get(configClass);
			if (skip == null) {
        //判定配置类是否是通过@Import注解引入的类
				if (configClass.isImported()) {
					boolean allSkipped = true;
          //获取配置类是被哪些配置类通过@Import引入的集合
					for (ConfigurationClass importedBy : configClass.getImportedBy()) {
            //判定importedBy配置类是否需要跳过，如果需要跳过那么当前配置类也是需要跳过
						if (!shouldSkip(importedBy)) {
							allSkipped = false;
							break;
						}
					}
					if (allSkipped) {
            //如果importedBy配置类需要跳过，那么@Import引入的也需要跳过
						skip = true;
					}
				}
        //判定非@Import注解引入的候选类是否需要跳过
				if (skip == null) {
					skip = conditionEvaluator.shouldSkip(configClass.getMetadata(), ConfigurationPhase.REGISTER_BEAN);
				}
        //将需要跳过的配置类存放入缓存，方便后面直接使用
				this.skipped.put(configClass, skip);
			}
			return skip;
		}
	}

```

conditionEvaluator.shouldSkip方法源码：

```java
	/**
	 * 判定@Conditional注解标注的类是否需要跳过
	 */
	public boolean shouldSkip(@Nullable AnnotatedTypeMetadata metadata, @Nullable ConfigurationPhase phase) {
    //如果配置类没有被@Conditional注解标注，直接返回，不在往下判定
		if (metadata == null || !metadata.isAnnotated(Conditional.class.getName())) {
			return false;
		}
		//判定是否需要跳过的阶段默认是REGISTER_BEAN阶段
		if (phase == null) {
      //如果元数据是AnnotationMetadata的实例
      //并且是@Component、@ComponentScan、@Import、@ImportResource注解标注的配置类
      //或者@Bean标注的配置类，则评估为配置阶段判定是否需要跳过配置类
			if (metadata instanceof AnnotationMetadata &&
					ConfigurationClassUtils.isConfigurationCandidate((AnnotationMetadata) metadata)) {
				return shouldSkip(metadata, ConfigurationPhase.PARSE_CONFIGURATION);
			}
      //在将ConfigurationClass转换为BeanDefinition并注入IOC容器之前评估是否跳过
			return shouldSkip(metadata, ConfigurationPhase.REGISTER_BEAN);
		}

		List<Condition> conditions = new ArrayList<>();
    //获取@Conditional注解对应的所有条件配置类
		for (String[] conditionClasses : getConditionClasses(metadata)) {
			for (String conditionClass : conditionClasses) {
        //将条件类实例化
				Condition condition = getCondition(conditionClass, this.context.getClassLoader());
				conditions.add(condition);
			}
		}
		//对条件配置类进行排序
		AnnotationAwareOrderComparator.sort(conditions);
		//对条件类进行循环判定是否符合条件
		for (Condition condition : conditions) {
			ConfigurationPhase requiredPhase = null;
			if (condition instanceof ConfigurationCondition) {
				requiredPhase = ((ConfigurationCondition) condition).getConfigurationPhase();
			}
      //判定，如果不符合条件则返回true,即跳过当前配置类
			if ((requiredPhase == null || requiredPhase == phase) && !condition.matches(this.context, metadata)) {
				return true;
			}
		}

		return false;
	}

```

ConfigurationClassUtils#isConfigurationCandidate方法源码：

```java
	private static final Set<String> candidateIndicators = new HashSet<>(8);

	static {
		candidateIndicators.add(Component.class.getName());
		candidateIndicators.add(ComponentScan.class.getName());
		candidateIndicators.add(Import.class.getName());
		candidateIndicators.add(ImportResource.class.getName());
	}
//苹果配置类是否需要在配置阶段（即将bean转换为ConfigurationClass之前进行评估是否需要跳过）
public static boolean isConfigurationCandidate(AnnotationMetadata metadata) {
		//接口直接返回false
		if (metadata.isInterface()) {
			return false;
		}

		//判定元数据是否是@Component、@ComponentScan、@Import、@ImportResource注解对应的注解元数据
    //如果是则评估为true
		for (String indicator : candidateIndicators) {
			if (metadata.isAnnotated(indicator)) {
				return true;
			}
		}

		//最后来判定@Bean标注的方法
		try {
			return metadata.hasAnnotatedMethods(Bean.class.getName());
		}
		catch (Throwable ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Failed to introspect @Bean methods on class [" + metadata.getClassName() + "]: " + ex);
			}
			return false;
		}
	}
```

ConditionEvaluator#getConditionClasses方法源码（获取条件配置类）：

```java
	private List<String[]> getConditionClasses(AnnotatedTypeMetadata metadata) {
    //获取@Conditional注解的所有属性值
		MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(Conditional.class.getName(), true);
    //获取条件配置类
		Object values = (attributes != null ? attributes.get("value") : null);
		return (List<String[]>) (values != null ? values : Collections.emptyList());
	}
```

ConditionEvaluator#getCondition条件配置类实例化：

```java
	private Condition getCondition(String conditionClassName, @Nullable ClassLoader classloader) {
		Class<?> conditionClass = ClassUtils.resolveClassName(conditionClassName, classloader);
		return (Condition) BeanUtils.instantiateClass(conditionClass);
	}
```

GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)