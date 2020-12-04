死磕源码系列【springboot之OnClassCondition、OnBeanCondition、OnWebApplicationCondition条件注解源码解析】

在spring-boot-autoconfigurejar包中的spring.factories配置文件中有一个org.springframework.boot.autoconfigure.AutoConfigurationImportFilter自动化配置import过滤器，配置如下：

```java
# Auto Configuration Import Filters
org.springframework.boot.autoconfigure.AutoConfigurationImportFilter=\
org.springframework.boot.autoconfigure.condition.OnBeanCondition,\
org.springframework.boot.autoconfigure.condition.OnClassCondition,\
org.springframework.boot.autoconfigure.condition.OnWebApplicationCondition
```

上述三个类都是AutoConfigurationImportFilter自动化配置是否匹配接口的实现类，用于过滤自动化配置类及其它类是否符合条件，该类是在org.springframework.boot.autoconfigure.AutoConfigurationImportSelector#getAutoConfigurationImportFilters方法中加载到内存之中的：

```java
	protected List<AutoConfigurationImportFilter> getAutoConfigurationImportFilters() {
		return SpringFactoriesLoader.loadFactories(AutoConfigurationImportFilter.class, this.beanClassLoader);
	}
```

之前的一篇文章[死磕源码系列【AutoConfigurationMetadataLoader之加载自动化配置元数据源码详解】](https://mingyang.blog.csdn.net/article/details/110448665)已经讲解过通过这三个过滤器取过滤掉不符合条件的配置类，本文不再详解；

##### 1.OnClassCondition类在这三个类中优先级最高（@ConditionalOnClass、@ConditionalOnMissingClass），所以先讲解此类

```java
	private interface OutcomesResolver {

		ConditionOutcome[] resolveOutcomes();

	}
```

> 此接口是一个条件解析接口，返回当前配置类是否符合条件及日志信息，其有两个实现类StandardOutcomesResolver和ThreadedOutcomesResolver，其中ThreadedOutcomesResolver是采用线程异步的方式解析配置类，最终还是调用StandardOutcomesResolver的实现方法；

##### StandardOutcomesResolver标准条件匹配解析类：

```java
	private static final class StandardOutcomesResolver implements OutcomesResolver {
		//配置类集合
		private final String[] autoConfigurationClasses;
		//开始索引
		private final int start;
    //结束索引
		private final int end;
		//配置类注解元数据
		private final AutoConfigurationMetadata autoConfigurationMetadata;
    //类加载器
		private final ClassLoader beanClassLoader;
		//新建一个标准的条件匹配解析实例对象
		private StandardOutcomesResolver(String[] autoConfigurationClasses, int start, int end,
				AutoConfigurationMetadata autoConfigurationMetadata, ClassLoader beanClassLoader) {
			this.autoConfigurationClasses = autoConfigurationClasses;
			this.start = start;
			this.end = end;
			this.autoConfigurationMetadata = autoConfigurationMetadata;
			this.beanClassLoader = beanClassLoader;
		}
		//解析配置类是否符合条件并返回结果集
		@Override
		public ConditionOutcome[] resolveOutcomes() {
			return getOutcomes(this.autoConfigurationClasses, this.start, this.end, this.autoConfigurationMetadata);
		}
		//解析配置类是否符合条件并返回结果集
		private ConditionOutcome[] getOutcomes(String[] autoConfigurationClasses, int start, int end,
				AutoConfigurationMetadata autoConfigurationMetadata) {
      //新建一个条件匹配解析后的结果集数组
			ConditionOutcome[] outcomes = new ConditionOutcome[end - start];
			for (int i = start; i < end; i++) {
        //获取配置类
				String autoConfigurationClass = autoConfigurationClasses[i];
				if (autoConfigurationClass != null) {
          //获取自动化配置元数据key对应的值
					String candidates = autoConfigurationMetadata.get(autoConfigurationClass, "ConditionalOnClass");
          //自动化配置条件注解的条件存在
					if (candidates != null) {
            //解析条件类是否存在，如果不存在返回ConditionOutcome对象match为false及日志信息
            //如果符合条件，即类实例存在，则返回null
						outcomes[i - start] = getOutcome(candidates);
					}
				}
			}
			return outcomes;
		}
		//解析条件类是否存在，如果不存在返回ConditionOutcome对象match为false及日志信息
    //如果符合条件，即类实例存在，则返回null
		private ConditionOutcome getOutcome(String candidates) {
			try {
        //如果只有一个条件类存在
				if (!candidates.contains(",")) {
          //获取条件类验证的结果集
					return getOutcome(candidates, this.beanClassLoader);
				}
        //如果有多个条件类存在
				for (String candidate : StringUtils.commaDelimitedListToStringArray(candidates)) {
           //获取条件类验证的结果集
					ConditionOutcome outcome = getOutcome(candidate, this.beanClassLoader);
					if (outcome != null) {
						return outcome;
					}
				}
			}
			catch (Exception ex) {
				// We'll get another chance later
			}
			return null;
		}
 		//获取条件类验证的结果集
		private ConditionOutcome getOutcome(String className, ClassLoader classLoader) {
      //如果条件类不存在，则返回不匹配信息
			if (ClassNameFilter.MISSING.matches(className, classLoader)) {
				return ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionalOnClass.class)
						.didNotFind("required class").items(Style.QUOTE, className));
			}
      //如果条件类存在，则返回null
			return null;
		}

	}
```

##### ClassNameFilter类是FilteringSpringBootCondition类的一个内部枚举类：

```java
	protected enum ClassNameFilter {

		PRESENT {

			@Override
			public boolean matches(String className, ClassLoader classLoader) {
				return isPresent(className, classLoader);
			}

		},
		
		MISSING {

			@Override
			public boolean matches(String className, ClassLoader classLoader) {
        //如果条件类存在，则返回false,否则返回true
				return !isPresent(className, classLoader);
			}

		};

		abstract boolean matches(String className, ClassLoader classLoader);
		//通过反射的方式获取条件类是否存在
		static boolean isPresent(String className, ClassLoader classLoader) {
			if (classLoader == null) {
				classLoader = ClassUtils.getDefaultClassLoader();
			}
			try {
        //通过反射的方式获取指定的条件类的class实例对象
        //如果存在，则返回true,否则返回false
				resolve(className, classLoader);
				return true;
			}
			catch (Throwable ex) {
				return false;
			}
		}

	}

```

```java
	protected static Class<?> resolve(String className, ClassLoader classLoader) throws ClassNotFoundException {
		if (classLoader != null) {
      //通过反射的方式获取指定的条件类的class实例对象
			return Class.forName(className, false, classLoader);
		}
    //通过反射的方式获取指定的条件类的class实例对象
		return Class.forName(className);
	}
```

##### ThreadedOutcomesResolver基于线程的条件注解解析类

```java
	private static final class ThreadedOutcomesResolver implements OutcomesResolver {
		//线程对象
		private final Thread thread;
    //配置类注解解析后的结果集
		private volatile ConditionOutcome[] outcomes;
    //根据给定的解析器构建ThreadedOutcomesResolver实例对象
		private ThreadedOutcomesResolver(OutcomesResolver outcomesResolver) {
      //使用异步线程的模式在后台解析给定的配置类
			this.thread = new Thread(() -> this.outcomes = outcomesResolver.resolveOutcomes());
			this.thread.start();
		}

		@Override
		public ConditionOutcome[] resolveOutcomes() {
			try {
				this.thread.join();
			}
			catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			return this.outcomes;
		}

	}
```

##### OnClassCondition条件注解解析类

```java
@Order(Ordered.HIGHEST_PRECEDENCE)
class OnClassCondition extends FilteringSpringBootCondition {
	//根据给定的配置类元数据及配置类集合判定配置类是否符合条件
	@Override
	protected final ConditionOutcome[] getOutcomes(String[] autoConfigurationClasses,
			AutoConfigurationMetadata autoConfigurationMetadata) {
		// 如果有多个处理器可用，则将工作拆分并在后台线程中执行一半，使用一个额外的线程似乎可以童工最好的性能。
		// 线程越多，并不见得性能会更好，可能会更糟糕
		if (autoConfigurationClasses.length > 1 && Runtime.getRuntime().availableProcessors() > 1) 		{
      //如果配置类有多个，并且有多个处理器，则选择拆分处理的方法
			return resolveOutcomesThreaded(autoConfigurationClasses, autoConfigurationMetadata);
		}
		else {
      //如果只有一个配置类，则使用StandardOutcomesResolver解析类对配置类进行处理
			OutcomesResolver outcomesResolver = new StandardOutcomesResolver(autoConfigurationClasses, 0,
					autoConfigurationClasses.length, autoConfigurationMetadata, getBeanClassLoader());
			return outcomesResolver.resolveOutcomes();
		}
	}
   //如果配置类有多个，并且有多个处理器，则选择拆分处理的方法
  	private ConditionOutcome[] resolveOutcomesThreaded(String[] autoConfigurationClasses,
			AutoConfigurationMetadata autoConfigurationMetadata) {
      //获取配置类一半的数量
		int split = autoConfigurationClasses.length / 2;
    //创建线程解析器解析第一部分配置类
		OutcomesResolver firstHalfResolver = createOutcomesResolver(autoConfigurationClasses, 0, split,
				autoConfigurationMetadata);
      //创建标准的解析器解析第二部分配置类
		OutcomesResolver secondHalfResolver = new StandardOutcomesResolver(autoConfigurationClasses, split,
				autoConfigurationClasses.length, autoConfigurationMetadata, getBeanClassLoader());
      //使用标准解析器解析第二部分配置类
		ConditionOutcome[] secondHalf = secondHalfResolver.resolveOutcomes();
      //使用线程解析器解析第一部分配置类
		ConditionOutcome[] firstHalf = firstHalfResolver.resolveOutcomes();
      //创建解析后的结果数据集
		ConditionOutcome[] outcomes = new ConditionOutcome[autoConfigurationClasses.length];
      //将第一部分解析的结果放入数组
		System.arraycopy(firstHalf, 0, outcomes, 0, firstHalf.length);
      //将第二部分解析的结果放入数组
		System.arraycopy(secondHalf, 0, outcomes, split, secondHalf.length);
		return outcomes;
	}

	private OutcomesResolver createOutcomesResolver(String[] autoConfigurationClasses, int start, int end,
			AutoConfigurationMetadata autoConfigurationMetadata) {
    //新建一个标准的解析器
		OutcomesResolver outcomesResolver = new StandardOutcomesResolver(autoConfigurationClasses, start, end,
				autoConfigurationMetadata, getBeanClassLoader());
		try {
      //以标准解析器为参数新建线程异步解析器
			return new ThreadedOutcomesResolver(outcomesResolver);
		}
		catch (AccessControlException ex) {
			return outcomesResolver;
		}
	}
	}
```

##### 2.OnWebApplicationCondition用来检测容器的类型是否符合条件（@ConditionalOnWebApplication、ConditionalOnNotWebApplication），其优先级低于OnClassCondition类

```java
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
class OnWebApplicationCondition extends FilteringSpringBootCondition {
	//servlet对应的应用上下文类
	private static final String SERVLET_WEB_APPLICATION_CLASS = "org.springframework.web.context.support.GenericWebApplicationContext";
  //reactive应用上下文类
	private static final String REACTIVE_WEB_APPLICATION_CLASS = "org.springframework.web.reactive.HandlerResult";
	//根据自动化配置注解元数据获取指定配置类集合注解条件的匹配结果集
	@Override
	protected ConditionOutcome[] getOutcomes(String[] autoConfigurationClasses,
			AutoConfigurationMetadata autoConfigurationMetadata) {
    //新建配置类大小的结果集数组
		ConditionOutcome[] outcomes = new ConditionOutcome[autoConfigurationClasses.length];
		for (int i = 0; i < outcomes.length; i++) {
      //获取自动化配置类
			String autoConfigurationClass = autoConfigurationClasses[i];
			if (autoConfigurationClass != null) {
        //首先根据自动化配置类获取对应条件ConditionalOnWebApplication的配置，将其作为参数获取匹配结果
        //如果匹配则返回null,否则返回匹配结果及错误日志信息对象
				outcomes[i] = getOutcome(
						autoConfigurationMetadata.get(autoConfigurationClass, "ConditionalOnWebApplication"));
			}
		}
		return outcomes;
	}
  //首先根据自动化配置类获取对应条件ConditionalOnWebApplication的配置，将其作为参数获取匹配结果
  //如果匹配则返回null,否则返回匹配结果及错误日志信息对象
	private ConditionOutcome getOutcome(String type) {
    //如果类型为null,直接返回null(默认为已经匹配)
		if (type == null) {
			return null;
		}
		ConditionMessage.Builder message = ConditionMessage.forCondition(ConditionalOnWebApplication.class);
    //如果容器类型为servlet
		if (ConditionalOnWebApplication.Type.SERVLET.name().equals(type)) {
      //如果上下文类不存在，则返回不匹配信息
			if (!ClassNameFilter.isPresent(SERVLET_WEB_APPLICATION_CLASS, getBeanClassLoader())) {
				return ConditionOutcome.noMatch(message.didNotFind("servlet web application classes").atAll());
			}
		}
    //如果容器类型是reactive
		if (ConditionalOnWebApplication.Type.REACTIVE.name().equals(type)) {
      //如果上下文类不存在，则返回不匹配信息
			if (!ClassNameFilter.isPresent(REACTIVE_WEB_APPLICATION_CLASS, getBeanClassLoader())) {
				return ConditionOutcome.noMatch(message.didNotFind("reactive web application classes").atAll());
			}
		}
    //如果两个上下文类都不存在，则返回不匹配信息
		if (!ClassNameFilter.isPresent(SERVLET_WEB_APPLICATION_CLASS, getBeanClassLoader())
				&& !ClassUtils.isPresent(REACTIVE_WEB_APPLICATION_CLASS, getBeanClassLoader())) {
			return ConditionOutcome.noMatch(message.didNotFind("reactive or servlet web application classes").atAll());
		}
    //上下文存在，返回null
		return null;
	}
}
```

##### 3.OnBeanCondition类用来检测bean是否存在（@ConditionalOnBean、@ConditionalOnMissingBean、@ConditionalOnSingleCandidate）

```java
//优先级在三个过滤其中最低
@Order(Ordered.LOWEST_PRECEDENCE)
class OnBeanCondition extends FilteringSpringBootCondition implements ConfigurationCondition {
	//获取bean所处的阶段
	@Override
	public ConfigurationPhase getConfigurationPhase() {
		return ConfigurationPhase.REGISTER_BEAN;
	}
  //根据自动化配置注解元数据判定指定配置类是否符合条件
	@Override
	protected final ConditionOutcome[] getOutcomes(String[] autoConfigurationClasses,
			AutoConfigurationMetadata autoConfigurationMetadata) {
    //创建自动化配置类匹配结果集类
		ConditionOutcome[] outcomes = new ConditionOutcome[autoConfigurationClasses.length];
		for (int i = 0; i < outcomes.length; i++) {
      //获取自动化配置类
			String autoConfigurationClass = autoConfigurationClasses[i];
			if (autoConfigurationClass != null) {
        //获取自动化配置对应的条件注解元数据信息
				Set<String> onBeanTypes = autoConfigurationMetadata.getSet(autoConfigurationClass, "ConditionalOnBean");
        //获取条件匹配结果
				outcomes[i] = getOutcome(onBeanTypes, ConditionalOnBean.class);
				if (outcomes[i] == null) {
          //获取配置类条件注解ConditionalOnSingleCandidate的元数据
					Set<String> onSingleCandidateTypes = autoConfigurationMetadata.getSet(autoConfigurationClass,
							"ConditionalOnSingleCandidate");
          //获取条件匹配结果
					outcomes[i] = getOutcome(onSingleCandidateTypes, ConditionalOnSingleCandidate.class);
				}
			}
		}
		return outcomes;
	}
 //根据条件注解bean的类型及注解获取匹配结果
	private ConditionOutcome getOutcome(Set<String> requiredBeanTypes, Class<? extends Annotation> annotation) {
    //通过过滤器及反射的方式确定bean是否存在
		List<String> missing = filter(requiredBeanTypes, ClassNameFilter.MISSING, getBeanClassLoader());
    //如果不存在，则返回不匹配信息
		if (!missing.isEmpty()) {
			ConditionMessage message = ConditionMessage.forCondition(annotation)
					.didNotFind("required type", "required types").items(Style.QUOTE, missing);
			return ConditionOutcome.noMatch(message);
		}
    //如果存在则返回null
		return null;
	}
```

父类FilteringSpringBootCondition#filter方法源码：

```java
	protected final List<String> filter(Collection<String> classNames, ClassNameFilter classNameFilter,
			ClassLoader classLoader) {
    //如果类名不存在，则直接返回空数组
		if (CollectionUtils.isEmpty(classNames)) {
			return Collections.emptyList();
		}
		List<String> matches = new ArrayList<>(classNames.size());
		for (String candidate : classNames) {
      //根据反射的方式确定确定指定的类是否存在，如果不存在则返回true,否则返回false
			if (classNameFilter.matches(candidate, classLoader)) {
				matches.add(candidate);
			}
		}
		return matches;
	}
```

##### 4.上述三个过滤器类都是FilteringSpringBootCondition抽象类的子类，其getOutcomes方法都是通过此类作为入口调用的

```java
abstract class FilteringSpringBootCondition extends SpringBootCondition
		implements AutoConfigurationImportFilter, BeanFactoryAware, BeanClassLoaderAware {

	private BeanFactory beanFactory;

	private ClassLoader beanClassLoader;

	@Override
	public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
    //获取条件评估报告类
		ConditionEvaluationReport report = ConditionEvaluationReport.find(this.beanFactory);
    //获取配置类匹配结果集
		ConditionOutcome[] outcomes = getOutcomes(autoConfigurationClasses, autoConfigurationMetadata);
    //创建boolean数组
		boolean[] match = new boolean[outcomes.length];
		for (int i = 0; i < outcomes.length; i++) {
      //如果结果为null,则匹配，否则根据具体结果集判定
			match[i] = (outcomes[i] == null || outcomes[i].isMatch());
      //如果不匹配，则将配置类及匹配信息存入条件评估报告对象中
			if (!match[i] && outcomes[i] != null) {
        //打印trace级别的日志
				logOutcome(autoConfigurationClasses[i], outcomes[i]);
				if (report != null) {
          //将数据存入条件评估报告对象集合
					report.recordConditionEvaluation(autoConfigurationClasses[i], this, outcomes[i]);
				}
			}
		}
		return match;
	}
	//获取配置类匹配结果集，具体由子类实现
	protected abstract ConditionOutcome[] getOutcomes(String[] autoConfigurationClasses,
			AutoConfigurationMetadata autoConfigurationMetadata);
			
	}
```

总结：上述三个条件过滤器类判定class、bean及容器类别都是通过反射的方式来进行判定。

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)