死磕源码系列【springboot之ConditionEvaluator#shouldSkip条件注解评估类判定是否应该跳过指定注解】

条件判定接口org.springframework.context.annotation.Condition是一个函数是接口，只有一个matchs方法用来判定是否符合指定的条件，结合@Conditional注解一起使用

```java
@FunctionalInterface
public interface Condition {

	/**
	 * 判定条件是否匹配
	 * @param context 条件上下文
	 * @param metadata 类或者方法的元数据对象
	 */
	boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata);

}
```

##### 1.示例判定当前是否是mac os系统

```java
public class MacOsCondition implements Condition {
    /**
     * 匹配操作系统类型
     */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String osName = context.getEnvironment().getProperty("os.name");
        if (StringUtils.equalsIgnoreCase(osName, "Mac OS X")) {
            return true;
        }
        return false;
    }
}
```

然后再@Configuration配置类上加@Conditional(MacOsCondition.class)就可以根据判定的结果来决定是否将配置类加入到IOC容器之中。

##### 2.条件注解是如何其作用的呢？ConditionEvaluator#shouldSkip判定方法给出了很清晰的使用方法

```java
	
//判定基于@Conditional注解的配置类是否应该忽略
public boolean shouldSkip(@Nullable AnnotatedTypeMetadata metadata, @Nullable ConfigurationPhase phase) {
    //首先判定配置类是否存在注解，然后判定注解中是否包含@Conditional注解
		if (metadata == null || !metadata.isAnnotated(Conditional.class.getName())) {
			return false;
		}
		//如果注册过程为空
		if (phase == null) {
      //配置类上被注解标注，并且也被@Configuration或@Bean等注解标注
			if (metadata instanceof AnnotationMetadata &&
					ConfigurationClassUtils.isConfigurationCandidate((AnnotationMetadata) metadata)) {
        //将当前注册阶段标记为转换为配置类阶段继续判定
				return shouldSkip(metadata, ConfigurationPhase.PARSE_CONFIGURATION);
			}
			return shouldSkip(metadata, ConfigurationPhase.REGISTER_BEAN);
		}

		List<Condition> conditions = new ArrayList<>();
  //首先获取@Conditional注解属性value指定的条件判定类
		for (String[] conditionClasses : getConditionClasses(metadata)) {
			for (String conditionClass : conditionClasses) {
        //获取条件判定类的Condition实例对象
				Condition condition = getCondition(conditionClass, this.context.getClassLoader());
				conditions.add(condition);
			}
		}
		//将条件判定类排序
		AnnotationAwareOrderComparator.sort(conditions);

		for (Condition condition : conditions) {
			ConfigurationPhase requiredPhase = null;
			if (condition instanceof ConfigurationCondition) {
				requiredPhase = ((ConfigurationCondition) condition).getConfigurationPhase();
			}
      //调用具体条件判定类的matches方法判定是否匹配
			if ((requiredPhase == null || requiredPhase == phase) && !condition.matches(this.context, metadata)) {
				return true;
			}
		}

		return false;
	}
//获取@Conditional条件注解的属性值，即条件判定类
	private List<String[]> getConditionClasses(AnnotatedTypeMetadata metadata) {
		MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(Conditional.class.getName(), true);
		Object values = (attributes != null ? attributes.get("value") : null);
		return (List<String[]>) (values != null ? values : Collections.emptyList());
	}
	//获取条件判定类的Condition实例对象
	private Condition getCondition(String conditionClassName, @Nullable ClassLoader classloader) {
		Class<?> conditionClass = ClassUtils.resolveClassName(conditionClassName, classloader);
		return (Condition) BeanUtils.instantiateClass(conditionClass);
	}
```

总结：其实很简单，就是根据Condition接口的实现类来判定是否需要加载指定的配置类到容器之中；

GitHub地址：[https://github.com/mingyang66/spring-parent]（https://github.com/mingyang66/spring-parent）