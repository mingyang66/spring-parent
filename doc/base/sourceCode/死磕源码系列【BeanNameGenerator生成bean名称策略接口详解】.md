### 死磕源码系列【BeanNameGenerator生成bean名称策略接口详解】

> 本文包括bean name实现类源码详解，及自定义bean name生成策略源码原理详解

#### bean name生成策略三个实现类源码详解

> BeanNameGenerator接口是BeanDefinition定义生成bean名称的策略接口；

```java
public interface BeanNameGenerator {

	/**
	 * 为给定的BeanDefinition生成一个bean名称
	 */
	String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry);

}
```

看下BeanNameGenerator接口实现类UML关系图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201016191953510.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhb21pbmd5YW5n,size_16,color_FFFFFF,t_70#pic_center)

DefaultBeanNameGenerator是BeanNameGenerator接口的默认实现类：

```java
public class DefaultBeanNameGenerator implements BeanNameGenerator {

	/**
	 * DefaultBeanNameGenerator实例常量，用于在AbstractBeanDefinitionReader类中属性beanNameGenerator的设置
	 */
	public static final DefaultBeanNameGenerator INSTANCE = new DefaultBeanNameGenerator();

	//通过BeanDefinitionReaderUtils工具类的generateBeanName方法生成指定BeanDefinition的bean名称
	@Override
	public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
		return BeanDefinitionReaderUtils.generateBeanName(definition, registry);
	}

}
```

BeanDefinitionReaderUtils#generateBeanName代理方法源码：

```java
	/**
	 * 为给定的顶级BeanDefinition定义生成一个bean名称，该名称在给定的bean工厂中是唯一的
	 */
	public static String generateBeanName(BeanDefinition beanDefinition, BeanDefinitionRegistry registry)
			throws BeanDefinitionStoreException {

		return generateBeanName(beanDefinition, registry, false);
	}

	/**
	 * 为给定的顶级BeanDefinition定义生成一个bean名称，该名称在给定的bean工厂中是唯一的
	 * @param BeanDefiniton定义
	 * @param 注册bean的工厂类
	 * @param isInnerBean 给定的BeanDefinition是注册为内部bean还是顶级bean(允许内部bean和顶级bean生成特殊名称)
	 */
	public static String generateBeanName(
			BeanDefinition definition, BeanDefinitionRegistry registry, boolean isInnerBean)
			throws BeanDefinitionStoreException {
		//获取bean定义的类名
		String generatedBeanName = definition.getBeanClassName();
		if (generatedBeanName == null) {
			if (definition.getParentName() != null) {
        //当bean定义名称不存在并且存在父类时命名方式
				generatedBeanName = definition.getParentName() + "$child";
			}
			else if (definition.getFactoryBeanName() != null) {
        //读取生成该bean的factoryBean名称做前缀
				generatedBeanName = definition.getFactoryBeanName() + "$created";
			}
		}
		if (!StringUtils.hasText(generatedBeanName)) {
			throw new BeanDefinitionStoreException("Unnamed bean definition specifies neither " +
					"'class' nor 'parent' nor 'factory-bean' - can't generate bean name");
		}

		if (isInnerBean) {
			// 当为内部类时使用#好分割和系统的唯一hash码作为后缀
			return generatedBeanName + GENERATED_BEAN_NAME_SEPARATOR + ObjectUtils.getIdentityHexString(definition);
		}

		// 顶级bean,使用普通类名加唯一后缀
		return uniqueBeanName(generatedBeanName, registry);
	}

	/**
	 * 将给定的bean名称转换为给定bean工厂的唯一bean名称，如果有必要，附加一个唯一的计数器做后缀
	 */
	public static String uniqueBeanName(String beanName, BeanDefinitionRegistry registry) {
		String id = beanName;
		int counter = -1;

		// 增加计数器，直到id唯一
		String prefix = beanName + GENERATED_BEAN_NAME_SEPARATOR;
		while (counter == -1 || registry.containsBeanDefinition(id)) {
			counter++;
			id = prefix + counter;
		}
		return id;
	}
```

------

AnnotationBeanNameGenerator是BeanNameGenerator接口的实现类，用来生成被@Component注解或者其它@Component派生注解标注的bean；示例：被@Component注解标注的@Repository注解。

如果注释的值没有指示bean名称，那么将基于类的短名称（第一个字母消息）构建一个适当的名称。例如：com.xyz.FooServiceImpl类的bean名称是fooServiceImpl。

AnnotationBeanNameGenerator类源码：

```java
public class AnnotationBeanNameGenerator implements BeanNameGenerator {

	/**
	 * 默认AnnotationBeanNameGenerator实例的方便常量，用于组件扫描目的（即@Componment注解及@ComponmentScan注解扫描包）bean名称生成
	 * @since 5.2
	 */
	public static final AnnotationBeanNameGenerator INSTANCE = new AnnotationBeanNameGenerator();
	//@Component注解类常量
	private static final String COMPONENT_ANNOTATION_CLASSNAME = "org.springframework.stereotype.Component";

	private final Map<String, Set<String>> metaAnnotationTypesCache = new ConcurrentHashMap<>();


	@Override
	public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
		if (definition instanceof AnnotatedBeanDefinition) {
      //通过注解的属性值获取bean名称
			String beanName = determineBeanNameFromAnnotation((AnnotatedBeanDefinition) definition);
			if (StringUtils.hasText(beanName)) {
				//显式的找到bean名称
				return beanName;
			}
		}
		//如果注解未指定bean名称，则生成一个唯一的默认bean名称
		return buildDefaultBeanName(definition, registry);
	}

	/**
	 * 从类上的一个注释派生bean名称（即通过注解的属性value值来获取唯一的bean名称）
	 * @param annotatedDef the annotation-aware bean definition
	 * @return the bean name, or {@code null} if none is found
	 */
	@Nullable
	protected String determineBeanNameFromAnnotation(AnnotatedBeanDefinition annotatedDef) {
		AnnotationMetadata amd = annotatedDef.getMetadata();
		Set<String> types = amd.getAnnotationTypes();
		String beanName = null;
		for (String type : types) {
			AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor(amd, type);
			if (attributes != null) {
				Set<String> metaTypes = this.metaAnnotationTypesCache.computeIfAbsent(type, key -> {
					Set<String> result = amd.getMetaAnnotationTypes(key);
					return (result.isEmpty() ? Collections.emptySet() : result);
				});
        //检查给定的注释是否允许通过其注释的value属性构造bean名称
				if (isStereotypeWithNameValue(type, metaTypes, attributes)) {
					Object value = attributes.get("value");
					if (value instanceof String) {
						String strVal = (String) value;
						if (StringUtils.hasLength(strVal)) {
							if (beanName != null && !strVal.equals(beanName)) {
								throw new IllegalStateException("Stereotype annotations suggest inconsistent " +
										"component names: '" + beanName + "' versus '" + strVal + "'");
							}
							beanName = strVal;
						}
					}
				}
			}
		}
		return beanName;
	}

	/**
	 * 检查给定的注释是否允许通过其注释的value属性构造bean名称
	 */
	protected boolean isStereotypeWithNameValue(String annotationType,
			Set<String> metaAnnotationTypes, @Nullable Map<String, Object> attributes) {

		boolean isStereotype = annotationType.equals(COMPONENT_ANNOTATION_CLASSNAME) ||
				metaAnnotationTypes.contains(COMPONENT_ANNOTATION_CLASSNAME) ||
				annotationType.equals("javax.annotation.ManagedBean") ||
				annotationType.equals("javax.inject.Named");

		return (isStereotype && attributes != null && attributes.containsKey("value"));
	}

	/**
	 * 从给定的bean定义派生一个默认的bean名称，即类名首字母小写
	 */
	protected String buildDefaultBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
		return buildDefaultBeanName(definition);
	}

	/**
	 * 从给定的bean定义派生一个默认的bean名称，即类名首字母小写
	 */
	protected String buildDefaultBeanName(BeanDefinition definition) {
		String beanClassName = definition.getBeanClassName();
		Assert.state(beanClassName != null, "No bean class name set");
		String shortClassName = ClassUtils.getShortName(beanClassName);
		return Introspector.decapitalize(shortClassName);
	}

}

```

FullyQualifiedAnnotationBeanNameGenerator类源码：

FullyQualifiedAnnotationBeanNameGenerator类是AnnotationBeanNameGenerator类的一个扩展用于生成类的全限定类名作为bean名称，

```java
public class FullyQualifiedAnnotationBeanNameGenerator extends AnnotationBeanNameGenerator {

	@Override
	protected String buildDefaultBeanName(BeanDefinition definition) {
		String beanClassName = definition.getBeanClassName();
		Assert.state(beanClassName != null, "No bean class name set");
		return beanClassName;
	}

}
```

#### bean name自定义生成策略源码原理解析

在后置beanfactory容器回调接口实现类ConfigurationClassPostProcessor中定义有三个变量：

```
//使用类的全限定名作为bean的默认生成策略
public static final AnnotationBeanNameGenerator IMPORT_BEAN_NAME_GENERATOR =
			new FullyQualifiedAnnotationBeanNameGenerator();
			
	/* 使用短类名作为默认bean名称生成策略 */
	private BeanNameGenerator componentScanBeanNameGenerator = AnnotationBeanNameGenerator.INSTANCE;

	/* 使用类的全新定名作为bean默认生成策略 */
	private BeanNameGenerator importBeanNameGenerator = IMPORT_BEAN_NAME_GENERATOR;	
  //是否是本地xml配置的BeanNameGenerator生成器
	private boolean localBeanNameGeneratorSet = false;
```

ConfigurationClassPostProcessor#setBeanNameGenerator方法设置自定义bean name生成器，这个setter通常只适用于将ConfigurationClassPostProcessor后处理器配置为XML中的独立bean定义，例如：不使用专用的AnnotatiionConfig*应用程序上下文或者<context:annotation-config>标签，针对应用程序上下文指定的任何bean名称生成器都将优先于此处的设置（此方法只适合Spring中通过xml配置的形式，springboot可以忽略）。

```java
	public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
		Assert.notNull(beanNameGenerator, "BeanNameGenerator must not be null");
		this.localBeanNameGeneratorSet = true;
		this.componentScanBeanNameGenerator = beanNameGenerator;
		this.importBeanNameGenerator = beanNameGenerator;
	}
```

ConfigurationClassPostProcessor#processConfigBeanDefinitions方法（只展示bean name名称生成策略部分代码）：

```java
public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
		...

		//检测通过封闭的应用程序上下文提供的任何自定义bean名称生成策略
		SingletonBeanRegistry sbr = null;
		if (registry instanceof SingletonBeanRegistry) {
			sbr = (SingletonBeanRegistry) registry;
      //springboot 中localBeanNameGeneratorSet默认是false
			if (!this.localBeanNameGeneratorSet) {
        //获取自定义的bean name生成策略
				BeanNameGenerator generator = (BeanNameGenerator) sbr.getSingleton(
						AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR);
				if (generator != null) {
          //设置组件扫描bean name生成策略
					this.componentScanBeanNameGenerator = generator;
          //设置import bean name生成策略
					this.importBeanNameGenerator = generator;
				}
			}
		}
		...
		
	}

```

到这里会有一个疑问内部自定义bean name生成策略bean org.springframework.context.annotation.internalConfigurationBeanNameGenerator（AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR）是如何注入到IOC容器之中的？那我们看下启动类SpringApplication#run方法，方法中内嵌prepareContext方法，prepareContext方法内嵌postProcessApplicationContext方法：

```java
	protected void postProcessApplicationContext(ConfigurableApplicationContext context) {
    //此处会判定bean name生成策略是否为空
		if (this.beanNameGenerator != null) {
    //此处 将上一步中的疑问，自定义bean name生成策略注入到IOC容器		
      context.getBeanFactory().registerSingleton(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR,this.beanNameGenerator);
		}
		。。。
	}
```

此段代码我们看到了如何将bean name自定义生成策略注入到IOC容器之中，那么beanNameGenerator生成策略是如何获取呢？beanNameGenerator是SpringApplication类的一个属性，提供了setter方法：

```
	public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
		this.beanNameGenerator = beanNameGenerator;
	}
```

此处的setter方法就是我们设置自定义bean name生成器的入口，示例如下：

```java
@SpringBootApplication
public class QuartzBootStrap {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(QuartzBootStrap.class);
      		//MySelfBeanNameGenerator是自定义bean name生成策略
        application.setBeanNameGenerator(new MySelfBeanNameGenerator());
        application.run(args);
    }
}
```

至此，springboot如何自定义bean name生成策略，此处的设置是全局的，不包括指定配置类的bean name生成策略。

自定义生成bean name策略实现类：

```java
public class MySelfBeanNameGenerator implements BeanNameGenerator {
    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        System.out.println("--------"+definition.getBeanClassName());
        return definition.getBeanClassName();
    }
}
```



GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)