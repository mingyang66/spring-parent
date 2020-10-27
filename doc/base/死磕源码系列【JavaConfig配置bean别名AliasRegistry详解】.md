### 死磕源码系列【JavaConfig配置bean别名AliasRegistry详解】

> AliasRegistry用于管理别名的公共接口，作为BeanDefinitionRegistry的超级接口（主要用作JavaConfig配置bean别名的注册、删除、判定、获取）

##### AliasRegistry接口源码：

```java
public interface AliasRegistry {

	/**
	 * 给定一个名称，给它注册一个别名
	 * @param 规范的命名
	 * @param 要注册的别名
	 * @throws IllegalStateException if the alias is already in use
	 * and may not be overridden
	 */
	void registerAlias(String name, String alias);

	/**
	 * 从注册表中删除指定的别名
	 * @param 要被删除的别名
	 * @throws IllegalStateException if no such alias was found
	 */
	void removeAlias(String alias);

	/**
	 * 判定给定的名称是否已经注册了别名
	 * @param 要被检测的名称
	 * @return 给定的名称是否别名
	 */
	boolean isAlias(String name);

	/**
	 * 返回给定名称的别名数组
	 * @param name the name to check for aliases
	 * @return the aliases, or an empty array if none
	 */
	String[] getAliases(String name);

}
```

开篇我们已经说了AliasRegistry接口是BeanDefinitionRegistry接口的超级接口，BeanDefinitionRegistry接口用于保存bean定义的注册表，例如RootBeanDefinition和ChildBeanDefinition实例，通常被BeanFactories工程bean实现，在内部使用AbstractBeanDefinition抽象类的继承关系。

BeanDefinitionRegistry是Spring的bean工厂包中唯一封装bean定义的注册接口。标准BeanFactory接口仅覆盖对完全配置的工厂实例的访问注册接口。

Spring的bean定义的读者希望能够处理这个接口的实现。Spring核心中已知的实现着是DefaultListableBeanFactory和GenericApplicationContext。

AliasRegistry接口除了BeanDefinitionRegistry接口这个直接继承者之外还有一个实现类SimpleAliasRegistry，该实现类是IOC容器中别名解析的默认实现，下面先对默认实现类源码进行一个详细的拆解：

```java
/**
* AliasRegistry接口的简单实现，用作基类BeanDefinitionRegistry的实现
**/
public class SimpleAliasRegistry implements AliasRegistry {

	/** 从别名映射到规范名称 */
	private final Map<String, String> aliasMap = new ConcurrentHashMap<>(16);

	/**
	* 注册别名和规范名称的映射关系
	**/
	@Override
	public void registerAlias(String name, String alias) {
		Assert.hasText(name, "'name' must not be empty");
		Assert.hasText(alias, "'alias' must not be empty");
		synchronized (this.aliasMap) {
      //判定别名和规范名称是否相等
			if (alias.equals(name)) {
        //如果相等，则删除字典中的别名映射关系（如果存在）
				this.aliasMap.remove(alias);
				if (logger.isDebugEnabled()) {
					logger.debug("Alias definition '" + alias + "' ignored since it points to same name");
				}
			}
			else {
        //获取字典中指定别名的规范名称
				String registeredName = this.aliasMap.get(alias);
        //如果已经存在
				if (registeredName != null) {
          //如果规范名称已经存在于字典中，则无需重复注册
					if (registeredName.equals(name)) {
						// An existing alias - no need to re-register
						return;
					}
          //是否允许别名重写（即多个别名）
					if (!allowAliasOverriding()) {
						throw new IllegalStateException("Cannot define alias '" + alias + "' for name '" +
								name + "': It is already registered for name '" + registeredName + "'.");
					}
					if (logger.isDebugEnabled()) {
						logger.debug("Overriding alias '" + alias + "' definition for registered name '" +
								registeredName + "' with new target name '" + name + "'");
					}
				}
        //检测给定的名称是否已经作为另一个方向的别名指向给定的别名，并预先捕获循环应用
				checkForAliasCircle(name, alias);
        //将别名和规范名称加入字典中
				this.aliasMap.put(alias, name);
				if (logger.isTraceEnabled()) {
					logger.trace("Alias definition '" + alias + "' registered for name '" + name + "'");
				}
			}
		}
	}

	/**
	 * 确定是否允许别名重写(即多个别名)，默认是true
	 * 在DefaultListableBeanFactory容器类中有实现方法，默认为true，允许修改
	 */
	protected boolean allowAliasOverriding() {
		return true;
	}

	/**
	 * 确定给定名称是否已注册给定别名
	 */
	public boolean hasAlias(String name, String alias) {
    //获取字典中指定别名的规范名称（如果存在）
		String registeredName = this.aliasMap.get(alias);
    //判定给定规范名称name和获取到的规范名称registeredName是否相等，true返回，false则继续判定
    //规范名称registeredName部位null，再判定以registeredName为别名在字典中是否存在规范名称name
		return ObjectUtils.nullSafeEquals(registeredName, name) || (registeredName != null
				&& hasAlias(name, registeredName));
	}

	@Override
	public void removeAlias(String alias) {
		synchronized (this.aliasMap) {
			String name = this.aliasMap.remove(alias);
			if (name == null) {
				throw new IllegalStateException("No alias '" + alias + "' registered");
			}
		}
	}
	//判定指定名称是否是别名
	@Override
	public boolean isAlias(String name) {
		return this.aliasMap.containsKey(name);
	}
	//获取指定名称的别名数组集合
	@Override
	public String[] getAliases(String name) {
		List<String> result = new ArrayList<>();
		synchronized (this.aliasMap) {
			retrieveAliases(name, result);
		}
		return StringUtils.toStringArray(result);
	}

	/**
	 * 检索给定名称的所有别名，如果相同就将别名加入集合
	 */
	private void retrieveAliases(String name, List<String> result) {
		this.aliasMap.forEach((alias, registeredName) -> {
			if (registeredName.equals(name)) {
				result.add(alias);
        //递归检测含间接关系的别名
				retrieveAliases(alias, result);
			}
		});
	}

	/**
	 * 解析此注册表中注册的所有别名和目标名称，并对它们应用给定的StringValueResolver
	 * 例如，值解析器可以解析目标bean名称甚至别名中的占位符。
	 */
	public void resolveAliases(StringValueResolver valueResolver) {
		Assert.notNull(valueResolver, "StringValueResolver must not be null");
		synchronized (this.aliasMap) {
			Map<String, String> aliasCopy = new HashMap<>(this.aliasMap);
			aliasCopy.forEach((alias, registeredName) -> {
				String resolvedAlias = valueResolver.resolveStringValue(alias);
				String resolvedName = valueResolver.resolveStringValue(registeredName);
        //如果别名和规范名称解析后为null或者相等，则从字典中删除
				if (resolvedAlias == null || resolvedName == null || resolvedAlias.equals(resolvedName)) {
					this.aliasMap.remove(alias);
				}
        //如果解析后的别名和原始别名不登
				else if (!resolvedAlias.equals(alias)) {
          //获取字典中的规范名称
					String existingName = this.aliasMap.get(resolvedAlias);
          //如果解析后的规范名臣个解析后的别名在字典中对应的规范名称相等，则删除字典中alias
					if (existingName != null) {
						if (existingName.equals(resolvedName)) {
							// Pointing to existing alias - just remove placeholder
							this.aliasMap.remove(alias);
							return;
						}
						throw new IllegalStateException(
								"Cannot register resolved alias '" + resolvedAlias + "' (original: '" + alias +
								"') for name '" + resolvedName + "': It is already registered for name '" +
								registeredName + "'.");
					}
          //检测给定的名称是否已经作为另一个方向的别名指向给定的别名，并预先捕获循环引用
					checkForAliasCircle(resolvedName, resolvedAlias);
          //删除字典别名
					this.aliasMap.remove(alias);
          //新增字典
					this.aliasMap.put(resolvedAlias, resolvedName);
				}
				else if (!registeredName.equals(resolvedName)) {
					this.aliasMap.put(alias, resolvedName);
				}
			});
		}
	}

	/**
	 * 检测给定的名称是否已经作为另一个方向的别名指向给定的别名，并预先捕获循环引用
	 */
	protected void checkForAliasCircle(String name, String alias) {
		if (hasAlias(alias, name)) {
			throw new IllegalStateException("Cannot register alias '" + alias +
					"' for name '" + name + "': Circular reference - '" +
					name + "' is a direct or indirect alias for '" + alias + "' already");
		}
	}

	/**
	 * 确定原始名称，将别名解析为规范名称
	 */
	public String canonicalName(String name) {
		String canonicalName = name;
		// Handle aliasing...
		String resolvedName;
		do {
      //从字典中获取别名对应的规范名称
			resolvedName = this.aliasMap.get(canonicalName);
			if (resolvedName != null) {
				canonicalName = resolvedName;
			}
		}
		while (resolvedName != null);
		return canonicalName;
	}

}

```

##### bean别名在源码中的使用分析

springboot中JavaConfig配置bean的加载在ConfigurationClassPostProcessor#processConfigBeanDefinitions方法中，源码如下：

```java
public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
		  ...

			//创建ConfigurationClass解析的reader实例
			if (this.reader == null) {
				this.reader = new ConfigurationClassBeanDefinitionReader(
						registry, this.sourceExtractor, this.resourceLoader, this.environment,
						this.importBeanNameGenerator, parser.getImportRegistry());
			}
  	//根据得到的所有ConfigurationClass实例集合获取JavaConfig中@Bean注解标注的类（还包括@Import、@ImportResource、ImportBeanDefinitionRegistrar等）
			this.reader.loadBeanDefinitions(configClasses);
			...

	}
```

```java
private void loadBeanDefinitionsForConfigurationClass(
			ConfigurationClass configClass, TrackedConditionEvaluator trackedConditionEvaluator) {
		...
		for (BeanMethod beanMethod : configClass.getBeanMethods()) {
      //解析@Bean注解实例
			loadBeanDefinitionsForBeanMethod(beanMethod);
		}
		...
	}
```

```java
	private void loadBeanDefinitionsForBeanMethod(BeanMethod beanMethod) {
		...
		//获取@Bean注解name属性值
		List<String> names = new ArrayList<>(Arrays.asList(bean.getStringArray("name")));
    //如果属性name值部位空，则以第一个值作为bean的名称，其它值作为别名
		String beanName = (!names.isEmpty() ? names.remove(0) : methodName);

		//注册bean name的别名
		for (String alias : names) {
      //此处的registry是DefaultListableBeanFactory容器的实例，其实现了AliasRegistry、SimpleAliasRegistry、BeanDefinitionRegistry，具体的别名操作是由SimpleAliasRegistry实现
			this.registry.registerAlias(beanName, alias);
		}
		...
	}
```

通过容器的getBean方法传递别名后，容器会将别名转换为真实name,然后去容器之中查找bean定义：



```java
	protected <T> T doGetBean(
			String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly)
			throws BeansException {
		//此处将name别名转换为给定的bean名称
		String beanName = transformedBeanName(name);
		}
	protected String transformedBeanName(String name) {
		return canonicalName(BeanFactoryUtils.transformedBeanName(name));
	}
```

调用SimpleAliasRegistry中的canonicalName方法获取别名对应的bean名称:

```java
	public String canonicalName(String name) {
		String canonicalName = name;
		// Handle aliasing...
		String resolvedName;
		do {
			resolvedName = this.aliasMap.get(canonicalName);
			if (resolvedName != null) {
				canonicalName = resolvedName;
			}
		}
		while (resolvedName != null);
		return canonicalName;
	}
```



GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)