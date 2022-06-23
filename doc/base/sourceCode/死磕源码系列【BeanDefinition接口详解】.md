### 死磕源码系列【BeanDefinition相关接口类详解】



> BeanDefinition描述了一个bean实例，它具有属性值、构造函数、参数值和具体实现提供的进一步信息；这只是一个最小的接口：主要目的是允许BeanFactoryPostProcessor来接茬和修改属性值和其他bean元素。

##### 1.接口源码

```java
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {
	/**
	 * 标准单例作用域的作用域标识
	 * @see ConfigurableBeanFactory#SCOPE_SINGLETON
	 */
	String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;

	/**
	 * 标准原型作用域的作用域标识
	 * @see ConfigurableBeanFactory#SCOPE_PROTOTYPE
	 */
	String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;
	/**
	 * 角色描述，指示BeanDefinition是应用程序的主要部分，通常对应于用户定义的bean
	 */
	int ROLE_APPLICATION = 0;
	/**
	 * 角色描述，指示BeanDefinition是应用程序读取xml配置创建的Bean
	 */
	int ROLE_SUPPORT = 1;
	/**
	 * 角色提示，指示BeanDefinition是完全后台的角色，与最终用户无关，也就是系统框架级别的bean
	 */
	int ROLE_INFRASTRUCTURE = 2;

	/**
	 * 设置Bean Definition的父bean definition，如果存在
	 */
	void setParentName(@Nullable String parentName);
	/**
	 * 获取Bean Definition的父bean definition
	 */
	@Nullable
	String getParentName();
	/**
	 * 设置bean definition的类名，类名可以在BeanFactoryPostProcessing工厂钩子方法中修改
	 */
	void setBeanClassName(@Nullable String beanClassName);
	/**
	 * 返回当前bean definition的类名，
	 */
	@Nullable
	String getBeanClassName();
	/**
	 * 重写bean的作用域，制定一个新的作用域名称
	 */
	void setScope(@Nullable String scope);
	/**
	 * 返回bean的作用域，如果还不清楚则返回null
	 */
	@Nullable
	String getScope();
	/**
	 * 设置bean是否应该延迟加载，如果为false,bean将在启动时由执行单例初始化的bean工厂实例化
	 */
	void setLazyInit(boolean lazyInit);
	/**
	 * 返回这个bean是否应该被延迟初始化，也就是说，不应该在启动时立即实例化。只适用于单例bean
	 */
	boolean isLazyInit();
	/**
	 * 设置这个bean所依赖的被初始化的bean的名称。bean工厂将保证这些bean首先被初始化。
	 */
	void setDependsOn(@Nullable String... dependsOn);
	/**
	 * 返回当前bean所依赖的bean的名称
	 */
	@Nullable
	String[] getDependsOn();
	/**
	 * 设置此bean是否使用依赖注入其它的bean属性，注意，此标志仅用于影响基于类型的自动注入，它不影响按照名称
	 * 自动注入，即使指定的bean没有标记自动注入的候选引用，也会解析属性引用，如果名称匹配，则按照名称自动注入；
	 */
	void setAutowireCandidate(boolean autowireCandidate);
	/**
	 * 返回此bean是否是自动连接到其它bean的候选项
	 */
	boolean isAutowireCandidate();
	/**
	 * 设置此bean是否是主自动注入候选项
	 */
	void setPrimary(boolean primary);
	/**
	 * 返回此bean是否主自动注入选项
	 */
	boolean isPrimary();
	/**
	 * 指定要使用的工厂bean名称,如果存在
	 */
	void setFactoryBeanName(@Nullable String factoryBeanName);
	/**
	 * 返回工厂bean名称，如果存在
	 */
	@Nullable
	String getFactoryBeanName();
	/**
	 * 指定工厂方法（如果存在），此方法将使用构造函数参数调用，如果未指定任何参数，则不适用任何参数。该方法将在指定的工厂bean上调用，或者作为本地bean类上的静态方法调用
	 */
	void setFactoryMethodName(@Nullable String factoryMethodName);
	/**
	 * 返回工厂方法（如果存在）
	 */
	@Nullable
	String getFactoryMethodName();
	/**
	 * 返回bean的构造函数参数值
	 */
	ConstructorArgumentValues getConstructorArgumentValues();
	/**
	 * 如果有为此bean定义的构造函数参数值，则返回
	 */
	default boolean hasConstructorArgumentValues() {
		return !getConstructorArgumentValues().isEmpty();
	}
	/**
	 * 返回要应用于bean的新实例的属性值。返回的实例可以在BeanFactoryPostProcessor工厂钩子方法修改，
	 */
	MutablePropertyValues getPropertyValues();
	/**
	 * 如果有为此bean定义的属性值，则返回
	 */
	default boolean hasPropertyValues() {
		return !getPropertyValues().isEmpty();
	}
	/**
	 * 设置初始值设定项方法的名称
	 */
	void setInitMethodName(@Nullable String initMethodName);
	/**
	 * 返回初始值设定项方法的名称
	 */
	@Nullable
	String getInitMethodName();
	/**
	 * 设置destory方法名称
	 */
	void setDestroyMethodName(@Nullable String destroyMethodName);
	/**
	 * 返回destory方法名称
	 */
	@Nullable
	String getDestroyMethodName();
	/**
	 * 设置bean角色提示
	 */
	void setRole(int role);
	/**
	 * 获取角色
	 */
	int getRole();
	/**
	 * 设置bean definition描述
	 */
	void setDescription(@Nullable String description);
	/**
	 * 返回bean definition描述
	 */
	@Nullable
	String getDescription();
	/**
	 * 根据bean类或其它特定元数据，返回bean definition的可解析类型
	 */
	ResolvableType getResolvableType();
	/**
	 * 返回是否是单例模式
	 */
	boolean isSingleton();
	/**
	 * 返回是否是原型模式
	 */
	boolean isPrototype();
	/**
	 * 返回这个bean是否是抽象类，也就是说，不是要实例化的
	 */
	boolean isAbstract();
	/**
	 * 返回这个bean definition的资源描述
	 */
	@Nullable
	String getResourceDescription();
	/**
	 * 返回原始的BeanDefinition，如果没有，则返回null
	 */
	@Nullable
	BeanDefinition getOriginatingBeanDefinition();

}

```

可以看到上面源码中的很多属性和方法都很眼熟，如：类名、属性、构造函数参数列表、是否是单例模式、是否是原型模式、是否懒加载等等，其实就是将Bean的定义信息存储到BeanDefinition之中去了，后面对bean操作就是对BeanDefinition进行，例如拿到这个BeanDefinition后，就可以根据里面的类名、构造函数、构造函数参数使用反射进行对象创建。

BeanDefinition是一个接口，是一个抽象定义，实际使用的是其实现类，如：RootBeanDefinition、ChildBeanDefiniton、GenericBeanDefinition等；

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200923112623830.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhb21pbmd5YW5n,size_16,color_FFFFFF,t_70#pic_center)

BeanDefinition继承AttributeAccessor，说明它具有处理属性的能力；

BeanDefinition继承BeanMetadataElement，说明它可以持有Bean元数据元素;

##### 2.RootBeanDefinition、ChildBeanDefinition、GenericBeanDefinition都是AbstractBeanDefinition的子类

RootBeanDefinition可以单独作为一个BeanDefinition,也可以作为其它BeanDefinition的父类，但是它不能作为其它BeanDefinition的子类，源码中setParentName会抛出一个异常：

```java
	@Override
	public void setParentName(@Nullable String parentName) {
		if (parentName != null) {
			throw new IllegalArgumentException("Root bean cannot be changed into a child bean with parent reference");
		}
	}
```

ChildBeanDefinition相当于一个子类，不可以单独存在，必须要依赖一个父BeanDefinition。该类没有无参构造函数。

RootBeanDefinition可以用于在配置阶段注册单个bean定义。然而从Spring2.5开始，以编程方式注册bean定义的首选方法是GenericBeanDefinition类，GenericBeanDefinition的优点是允许动态定义父依赖项，而不是将角色硬编码为RootBeanDefinition。

##### 3.ConfigurationClassBeanDefinition、AnnotatedGenericBeanDefinition

在@Configuration注解的类中，使用@Bean注解实例化的bean其定义会用ConfigurationClassBeanDefinition存储

AnnotatedGenericBeanDefinition存储@Configuration注解注释的类；

ScannedGenericBeanDefinition存储@Component、@Service、@Controller等注解注释的类；



GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

