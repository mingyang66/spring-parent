### 死磕源码系列【ConfigurationClass类源码分析】

> ConfigurationClass类代表一个用户定义的配置(@Configuration)类，包括@Bean注解标注的方法，包括所有在类的父类中定义的方法，用一种扁平化的方式来管理；

##### 1.先看下类的源码定义的属性

```java
final class ConfigurationClass {
	//配置类的注解元数据
	private final AnnotationMetadata metadata;
	//配置类的资源对象
	private final Resource resource;
	//bean名称
	@Nullable
	private String beanName;
	//存放通过@Import注解引入的候选类是通过那个配置类引入的
  //包括ImportSelector选择器类中引入的候选类
  //包括DeferredImportSelector选择器中引入的候选类
  //包括@Import引入的普通类
	private final Set<ConfigurationClass> importedBy = new LinkedHashSet<>(1);
	//配置类的Bean方法对象
	private final Set<BeanMethod> beanMethods = new LinkedHashSet<>();
	//存放通过@ImportResource注解引入资源类及BeanDefinitionReader读取器对应关系
	private final Map<String, Class<? extends BeanDefinitionReader>> importedResources =
			new LinkedHashMap<>();
	//存放通过@Import注解引入的ImportBeanDefinitionRegistrar实现类，用于注册自定义bean到IOC容器
  //value值是引入当前类的类的注解元数据
	private final Map<ImportBeanDefinitionRegistrar, AnnotationMetadata> importBeanDefinitionRegistrars =
			new LinkedHashMap<>();
	//存放将BeanMethod标记为按其条件跳过
	final Set<String> skippedBeanMethods = new HashSet<>();
	}
```

类提供了通过importedBy属性判定是否是通过@Import注解被引入，还是被嵌套在其它配置类中被自动注入的：

```java
	public boolean isImported() {
		return !this.importedBy.isEmpty();
	}
```

配置类校验方法validate:

```java
	public void validate(ProblemReporter problemReporter) {
    //除非配置类声明为proxyBeanMethods=false不适用CGLIB代理模式，否则的话不可能为final类
		Map<String, Object> attributes = this.metadata.getAnnotationAttributes(Configuration.class.getName());
		if (attributes != null && (Boolean) attributes.get("proxyBeanMethods")) {
      //如果配置类是final类型，则抛出BeanDefinitionParsingException异常信息
			if (this.metadata.isFinal()) {
				problemReporter.error(new FinalConfigurationProblem());
			}
      //校验配置类中@Bean定义的方法
			for (BeanMethod beanMethod : this.beanMethods) {
				beanMethod.validate(problemReporter);
			}
		}
	}
```

org.springframework.context.annotation.BeanMethod#validate

```java
	@Override
	public void validate(ProblemReporter problemReporter) {
		if (getMetadata().isStatic()) {
			// 静态@Bean方法没有约束校验，立即返回约束验证
			return;
		}

		if (this.configurationClass.getMetadata().isAnnotated(Configuration.class.getName())) {
			//判定配置类方法是否可以重写（非静态方法、非final方法、非private方法）
      if (!getMetadata().isOverridable()) {
        //@Configuration标注的配置类的@Bean方法要想使用CGLIB代理必须是可重写的
        //将会抛出BeanDefinitionParsingException异常
				problemReporter.error(new NonOverridableMethodError());
			}
		}
	}


	private class NonOverridableMethodError extends Problem {
		
		public NonOverridableMethodError() {
			super(String.format("@Bean method '%s' must not be private or final; change the method's modifiers to continue",
					getMetadata().getMethodName()), getResourceLocation());
		}
	}
}

```



GitHub代码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)