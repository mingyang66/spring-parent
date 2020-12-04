### 死磕源码系列【springboot之ConditionEvaluationReport记录报告和日志条件评估详情源码解析】

> ConditionEvaluationReport用来记录自动化配置过程中条件匹配的详细信息及日志信息；

##### 1.ConditionOutcome类输出条件匹配及日志信息

```java
public class ConditionOutcome {
	//是否匹配
	private final boolean match;
 //日志信息
	private final ConditionMessage message;
	}
```

##### 2.Condition函数是接口定义一个class必须匹配给定的条件后才可以定义为BeanDefinition注册入容器

```java
@FunctionalInterface
public interface Condition {

	/**
	 * 判定条件是否匹配
	 * @param context 条件上下文
	 * @param metadata 配置类注解元数据或者方法元数据
	 */
	boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata);

}
```

##### 3.ConditionAndOutcome是一个静态内部类包含Condition判定是否匹配，ConditionOutcome包含匹配结果及日志信息

```java
	public static class ConditionAndOutcome {
		//条件匹配对象
		private final Condition condition;
    //存储评估匹配条件之后的结果及日志信息
		private final ConditionOutcome outcome;
		//评估匹配条件及输出结果
		public ConditionAndOutcome(Condition condition, ConditionOutcome outcome) {
			this.condition = condition;
			this.outcome = outcome;
		}
		//获取评估匹配判定对象
		public Condition getCondition() {
			return this.condition;
		}
		//获取评估匹配输出结果
		public ConditionOutcome getOutcome() {
			return this.outcome;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			ConditionAndOutcome other = (ConditionAndOutcome) obj;
			return (ObjectUtils.nullSafeEquals(this.condition.getClass(), other.condition.getClass())
					&& ObjectUtils.nullSafeEquals(this.outcome, other.outcome));
		}

		@Override
		public int hashCode() {
			return this.condition.getClass().hashCode() * 31 + this.outcome.hashCode();
		}

		@Override
		public String toString() {
			return this.condition.getClass() + " " + this.outcome;
		}

	}
```

##### 4.ConditionAndOutcomes存储多个评估匹配输出结果对象ConditionAndOutcome

```java
public static class ConditionAndOutcomes implements Iterable<ConditionAndOutcome> {
		//ConditionAndOutcome评估匹配输出对象集合
		private final Set<ConditionAndOutcome> outcomes = new LinkedHashSet<>();
		//向集合中新增一个评估条件匹配及输出结果对象
		public void add(Condition condition, ConditionOutcome outcome) {
			this.outcomes.add(new ConditionAndOutcome(condition, outcome));
		}

		/**
		 * 判定集合中的所有评估匹配条件是否匹配
		 * @return {@code true} if a full match
		 */
		public boolean isFullMatch() {
			for (ConditionAndOutcome conditionAndOutcomes : this) {
				if (!conditionAndOutcomes.getOutcome().isMatch()) {
					return false;
				}
			}
			return true;
		}
		//将ConditionAndOutcome对象转换为迭代器模式
		@Override
		public Iterator<ConditionAndOutcome> iterator() {
			return Collections.unmodifiableSet(this.outcomes).iterator();
		}

	}
```

##### 5.AncestorsMatchedCondition条件匹配类不支持此操作异常

```java
	private static class AncestorsMatchedCondition implements Condition {

		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			throw new UnsupportedOperationException();
		}

	}
```



##### 6.ConditionEvaluationReport存储条件评估报告类

```java
public final class ConditionEvaluationReport {
	//评估条件报告类在容器中的bean名称
	private static final String BEAN_NAME = "autoConfigurationReport";
	//创建一个父的条件匹配对象
	private static final AncestorsMatchedCondition ANCESTOR_CONDITION = new AncestorsMatchedCondition();
	//存放类名或方法名（key）,条件评估输出对象（value）
	private final SortedMap<String, ConditionAndOutcomes> outcomes = new TreeMap<>();
	//是否是原始条件匹配对象
	private boolean addedAncestorOutcomes;
	//父的条件评估报告对象
	private ConditionEvaluationReport parent;
	//记录已经从条件评估中排除的类名称
	private final List<String> exclusions = new ArrayList<>();
	//记录作为条件评估的候选类名称
	private final Set<String> unconditionalClasses = new HashSet<>();

	/**
	 * 提供一个私有的无参构造函数
	 */
	private ConditionEvaluationReport() {
	}

	/**
	 * 记录条件评估的发生信息
	 * @param source 类名或者方法名
	 * @param condition 评估条件
	 * @param outcome 评估条件输出
	 */
	public void recordConditionEvaluation(String source, Condition condition, ConditionOutcome outcome) {
		Assert.notNull(source, "Source must not be null");
		Assert.notNull(condition, "Condition must not be null");
		Assert.notNull(outcome, "Outcome must not be null");
    //删除作为条件评估的候选类名称
		this.unconditionalClasses.remove(source);
		if (!this.outcomes.containsKey(source)) {
      //将类名或方法名（key）及条件评估输出存入集合
			this.outcomes.put(source, new ConditionAndOutcomes());
		}
    //设置条件评估对象及输出对象存入集合
		this.outcomes.get(source).add(condition, outcome);
    //设置addedAncestorOutcomes为false，即：匹配条件不是原始匹配对象
		this.addedAncestorOutcomes = false;
	}

	/**
	 * 记录已经从条件评估中排除的类名称
	 */
	public void recordExclusions(Collection<String> exclusions) {
		Assert.notNull(exclusions, "exclusions must not be null");
		this.exclusions.addAll(exclusions);
	}

	/**
	 * 记录作为条件评估的候选类名称
	 */
	public void recordEvaluationCandidates(List<String> evaluationCandidates) {
		Assert.notNull(evaluationCandidates, "evaluationCandidates must not be null");
		this.unconditionalClasses.addAll(evaluationCandidates);
	}

	/**
	 * 返回匹配的结果集
	 */
	public Map<String, ConditionAndOutcomes> getConditionAndOutcomesBySource() {
		if (!this.addedAncestorOutcomes) {
			this.outcomes.forEach((source, sourceOutcomes) -> {
				if (!sourceOutcomes.isFullMatch()) {
					addNoMatchOutcomeToAncestors(source);
				}
			});
			this.addedAncestorOutcomes = true;
		}
		return Collections.unmodifiableMap(this.outcomes);
	}
	//将未匹配的输出结果替换为默认的未匹配输出对象
	private void addNoMatchOutcomeToAncestors(String source) {
		String prefix = source + "$";
		this.outcomes.forEach((candidateSource, sourceOutcomes) -> {
			if (candidateSource.startsWith(prefix)) {
				ConditionOutcome outcome = ConditionOutcome
						.noMatch(ConditionMessage.forCondition("Ancestor " + source).because("did not match"));
				sourceOutcomes.add(ANCESTOR_CONDITION, outcome);
			}
		});
	}

	/**
	 * 返回从条件评估中已经排除的类的名称集合
	 */
	public List<String> getExclusions() {
		return Collections.unmodifiableList(this.exclusions);
	}

	/**
	 * 返回已经经过条件评估但是不符合条件类的结果集
	 * @return the names of the unconditional classes
	 */
	public Set<String> getUnconditionalClasses() {
		Set<String> filtered = new HashSet<>(this.unconditionalClasses);
		filtered.removeAll(this.exclusions);
		return Collections.unmodifiableSet(filtered);
	}

	/**
	 * 返回条件评估对象的父对象
	 */
	public ConditionEvaluationReport getParent() {
		return this.parent;
	}

	/**
	 * 从容器对象中获取条件评估报告对象
	 */
	public static ConditionEvaluationReport find(BeanFactory beanFactory) {
		if (beanFactory != null && beanFactory instanceof ConfigurableBeanFactory) {
			return ConditionEvaluationReport.get((ConfigurableListableBeanFactory) beanFactory);
		}
		return null;
	}

	/**
	 * 从容器中获取ConditionEvaluationReport对象，如果不存在，则创建并注入到IOC容器中
	 */
	public static ConditionEvaluationReport get(ConfigurableListableBeanFactory beanFactory) {
		synchronized (beanFactory) {
			ConditionEvaluationReport report;
      //判定容器中是否存在条件评估报告类对象，如果存在，则取出来
			if (beanFactory.containsSingleton(BEAN_NAME)) {
				report = beanFactory.getBean(BEAN_NAME, ConditionEvaluationReport.class);
			}
			else {
        //如果容器中不存在，则创建并注入到IOC容器之中
				report = new ConditionEvaluationReport();
				beanFactory.registerSingleton(BEAN_NAME, report);
			}
      //设置当前条件评估报告对象父对象
			locateParent(beanFactory.getParentBeanFactory(), report);
			return report;
		}
	}
	//设置当前条件评估报告的父对象
	private static void locateParent(BeanFactory beanFactory, ConditionEvaluationReport report) {
		if (beanFactory != null && report.parent == null && beanFactory.containsBean(BEAN_NAME)) {
			report.parent = beanFactory.getBean(BEAN_NAME, ConditionEvaluationReport.class);
		}
	}

	public ConditionEvaluationReport getDelta(ConditionEvaluationReport previousReport) {
    //新建一个条件评估报告类
		ConditionEvaluationReport delta = new ConditionEvaluationReport();
		this.outcomes.forEach((source, sourceOutcomes) -> {
      //获取指定类的条件匹配及评估结果类
			ConditionAndOutcomes previous = previousReport.outcomes.get(source);
			if (previous == null || previous.isFullMatch() != sourceOutcomes.isFullMatch()) {
				sourceOutcomes.forEach((conditionAndOutcome) -> delta.recordConditionEvaluation(source,
						conditionAndOutcome.getCondition(), conditionAndOutcome.getOutcome()));
			}
		});
		List<String> newExclusions = new ArrayList<>(this.exclusions);
		newExclusions.removeAll(previousReport.getExclusions());
		delta.recordExclusions(newExclusions);
		List<String> newUnconditionalClasses = new ArrayList<>(this.unconditionalClasses);
		newUnconditionalClasses.removeAll(previousReport.unconditionalClasses);
		delta.unconditionalClasses.addAll(newUnconditionalClasses);
		return delta;
	}
	}
```

总结：本文主要讲解条件注解匹配相关类源码及条件注解评估报告容器类；

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

