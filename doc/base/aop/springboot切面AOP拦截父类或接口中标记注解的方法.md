#### springboot切面AOP拦截父类或接口中标记注解的方法

##### 一、注解的继承性回顾

1. 被@Inherited元注解标注的注解标注在类上的时候，子类可以继承父类上的注解。
2. 注解未被@Inherited元注解标注的，该注解标注在类上时，子类不会继承父类上标注的注解。
3. 注解标注在接口上，其子类及子接口都不会继承该注解
4. 注解标注在类或接口方法上，其子类重写该方法不会继承父类或接口中方法上标记的注解

> 根据注解继承的特性，我们再做AOP切面拦截的时候会遇到拦截不到的问题，今天我们就讲解下对这些特殊情况如何解决，对源码不做过渡深入的讲解。

##### 二、注解标注在父类、接口、父类方法、接口方法上如何通过子类拦截，首先了解下几个核心类

AnnotationMatchingPointcut切点类是用来判定是否需要拦截类、父类、实现接口中方法，其中一共四个构造函数如下：

```java
	/**
	 * Create a new AnnotationMatchingPointcut for the given annotation type.
	 * @param classAnnotationType the annotation type to look for at the class level
	 */
public AnnotationMatchingPointcut(Class<? extends Annotation> classAnnotationType) {
		this(classAnnotationType, false);
	}

	/**
	 * Create a new AnnotationMatchingPointcut for the given annotation type.
	 * @param classAnnotationType the annotation type to look for at the class level
	 * @param checkInherited whether to also check the superclasses and interfaces
	 * as well as meta-annotations for the annotation type
	 * @see AnnotationClassFilter#AnnotationClassFilter(Class, boolean)
	 */
	public AnnotationMatchingPointcut(Class<? extends Annotation> classAnnotationType, boolean checkInherited) {
		this.classFilter = new AnnotationClassFilter(classAnnotationType, checkInherited);
		this.methodMatcher = MethodMatcher.TRUE;
	}

	/**
	 * Create a new AnnotationMatchingPointcut for the given annotation types.
	 * @param classAnnotationType the annotation type to look for at the class level
	 * (can be {@code null})
	 * @param methodAnnotationType the annotation type to look for at the method level
	 * (can be {@code null})
	 */
	public AnnotationMatchingPointcut(@Nullable Class<? extends Annotation> classAnnotationType,
			@Nullable Class<? extends Annotation> methodAnnotationType) {

		this(classAnnotationType, methodAnnotationType, false);
	}

	/**
	 * Create a new AnnotationMatchingPointcut for the given annotation types.
	 * @param classAnnotationType the annotation type to look for at the class level
	 * (can be {@code null})
	 * @param methodAnnotationType the annotation type to look for at the method level
	 * (can be {@code null})
	 * @param checkInherited whether to also check the superclasses and interfaces
	 * as well as meta-annotations for the annotation type
	 * @since 5.0
	 * @see AnnotationClassFilter#AnnotationClassFilter(Class, boolean)
	 * @see AnnotationMethodMatcher#AnnotationMethodMatcher(Class, boolean)
	 */
	public AnnotationMatchingPointcut(@Nullable Class<? extends Annotation> classAnnotationType,
			@Nullable Class<? extends Annotation> methodAnnotationType, boolean checkInherited) {

		Assert.isTrue((classAnnotationType != null || methodAnnotationType != null),
				"Either Class annotation type or Method annotation type needs to be specified (or both)");

		if (classAnnotationType != null) {
			this.classFilter = new AnnotationClassFilter(classAnnotationType, checkInherited);
		}
		else {
			this.classFilter = new AnnotationCandidateClassFilter(methodAnnotationType);
		}

		if (methodAnnotationType != null) {
			this.methodMatcher = new AnnotationMethodMatcher(methodAnnotationType, checkInherited);
		}
		else {
			this.methodMatcher = MethodMatcher.TRUE;
		}
	}
```

> 以上四个构造函数，支持仅标记在类上注解、仅标记方法上的注解、即指定标记类上且标记在方法上的注解（是否拦截父类及接口上标记的方法）三种构造方式。

通过上述四个构造函数可以构造如下几种切点类型：

- 注解标注在当前类，只拦截当前类的方法
- 注解标注在当前类的父类上，拦截父类及子类中的方法
- 注解标注在当前类实现的接口上 ，拦截接口方法的实现方法
- 注解标注在当前类的方法上，拦截当前类的方法
- 注解标注在当前类父类或接口的方法上，拦截父类的方法或者当前类实现方法。

通过上述切点可以构造出我们需要的大多数场景，如果需要更灵活的实现还需要结合ComposablePointcut类，此类可以实现类级别标注的交集、并集，方法级别的交集、并集，切点级别的交集并集。

#### 三、切点实现案例

```
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor mybatisLogAdvisor(MybatisProperties properties) {
        //限定类级别的切点
        Pointcut cpc = new AnnotationMatchingPointcut(Mapper.class, properties.isCheckClassInherited());
        //限定方法级别的切点
        Pointcut mpc = new AnnotationMatchingPointcut(null, Mapper.class, properties.isCheckMethodInherited());
        //组合切面(并集)，一、ClassFilter只要有一个符合条件就返回true，二、
        Pointcut pointcut = new ComposablePointcut(cpc).union(mpc);
        //mybatis日志拦截切面
        MethodInterceptor interceptor = new MybatisMethodInterceptor();
        //切面增强类
        AnnotationPointcutAdvisor advisor = new AnnotationPointcutAdvisor(interceptor, pointcut);
        //切面优先级顺序
        advisor.setOrder(AopOrderInfo.MYBATIS);
        return advisor;
    }
```

> 此拦截器可以实现对标注了Mapper方法进行日志拦截，具体实现可以参考GitHub源码

四、上述向上查询父类、父接口及其方法的核心是AnnotatedElementUtils工具类，示例参考如下：

```java
 //返回当前类或父类或接口方法上标注的注解对象
targetDataSource = AnnotatedElementUtils.findMergedAnnotation(method, TargetDataSource.class);
//返回当前类或父类或接口上标注的注解对象
targetDataSource = AnnotatedElementUtils.findMergedAnnotation(method.getDeclaringClass(), TargetDataSource.class);
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)