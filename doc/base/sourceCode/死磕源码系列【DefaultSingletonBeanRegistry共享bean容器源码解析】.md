死磕源码系列【DefaultSingletonBeanRegistry共享bean实例注册表源码解析】

> DefaultSingletonBeanRegistry类继承SimpleAliasRegistry以及SingletonBeanRegistry接口，处理公用bean的注册、销毁以及依赖关系的注册和销毁。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201030112017610.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhb21pbmd5YW5n,size_16,color_FFFFFF,t_70#pic_center)

SimpleAliasRegistry类提供了bean别名注册功能，之前已经讲解过，请参考[死磕源码系列【JavaConfig配置bean别名AliasRegistry详解】](https://mingyang.blog.csdn.net/article/details/109228656)
；SingletonBeanRegistry接口为共享bean实例定义注册表的接口，可以通过IOC容器来实现，以便以统一公开的方式公开单实例管理工具。

##### SingletonBeanRegistry单实例注册表接口：

```java
public interface SingletonBeanRegistry {

	/**
	 * 将给定的现有对象注册为bean,注册表中给定bean名称及实例对象；
	 * 给定的实例应该是完全初始化的，注册表将不执行任何初始化回调
	 *（特别是，它将不执行InitializingBean的		afterPropertiesSet方法）
	 * 给定的实例也不会执行任何销毁回调（比如：DisposableBean的destory方法）
	 * 在一个完整的BeanFactory中运行时：如果您的bean应该接收初始化，并且或者销毁的回调，
	 * 注册一个bean定义，而不是一个现有的实例；
	 */
	void registerSingleton(String beanName, Object singletonObject);

	/**
	 * 返回以给定名称已注册的（原始）单例对象
	 * 只检查已经实例化的的单例，不返回尚未实例化的单例bean定义的对象
	 * 此方法的主要目的是访问手动注册的单例，也可以用于一个已经被实例化的bean定义(BeanDefinition)单例bean
	 * 此查找方法不知道FactoryBean前缀或别名，在获取单实例之前，需要首先解析规范的bean名称
	 */
	@Nullable
	Object getSingleton(String beanName);

	/**
	 * 检查注册表中是否包含具有给定名称的单实例；
	 * 只检查已经实例化的单例，对于尚未实例化的单例bean定义，不返回true
	 * 此方法的主要目的是检查手动注册的单例，也可以用来检查是否已经创建了由bean定义创建的单例
	 */
	boolean containsSingleton(String beanName);

	/**
	 * 返回在此注册表中注册的单例bean的名称，只检查已经实例化的单例，不返回未实例化的bean定义的名称；
	 * 此方法的主要目的是用来检查手动注册的单例，也可以用来检查已经创建了由bean定义的单实例
	 */
	String[] getSingletonNames();

	/**
	 * 返回在此注册表中注册的单例bean数量，只检查已经实例化的单例，不计算还没有初始化的单例bean定义
	 * 此方法的主要目的是用来检查手动注册的单例，也可以用来检查已经实例化过的bean定义；
	 */
	int getSingletonCount();

	/**
	 * 返回此注册表使用的单例互斥体（对于外部协作着）
	 */
	Object getSingletonMutex();

}

```

##### 共享bean实例的通用注册表具体实现类DefaultSingletonBeanRegistry，先看下属性：

```java
	/** Maximum number of suppressed exceptions to preserve. */
	private static final int SUPPRESSED_EXCEPTIONS_LIMIT = 100;


	/** 缓存单实例对象，bean名称及bean实例对象 */
	private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

	/** 缓存单实例工厂，bean名称及ObjectFactory */
	private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

	/** 缓存早期的单实例对象，bean名称及bean实例对象 */
	private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);

	/** 缓存一组已注册的单例实例，按照注册顺序包含bean名称 */
	private final Set<String> registeredSingletons = new LinkedHashSet<>(256);

	/** 当前正在创建的bean名称集合 */
	private final Set<String> singletonsCurrentlyInCreation =
			Collections.newSetFromMap(new ConcurrentHashMap<>(16));

	/** 当前从创建检查中排除的beanName */
	private final Set<String> inCreationCheckExclusions =
			Collections.newSetFromMap(new ConcurrentHashMap<>(16));

	/** 抑制的异常集合，可用于关联相关原因 */
	@Nullable
	private Set<Exception> suppressedExceptions;

	/** 标识当前是否正在销毁单实例对象的标记 */
	private boolean singletonsCurrentlyInDestruction = false;

	/** 销毁bean实例，beanName及bean实例对象 */
	private final Map<String, Object> disposableBeans = new LinkedHashMap<>();

	/** bean依赖之间的映射关系，key对应的bean依赖于value对应的bean */
	private final Map<String, Set<String>> containedBeanMap = new ConcurrentHashMap<>(16);

	/** bean依赖之间的映射关系，key对应的bean依赖value对应的bean（key依赖value） */
	private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<>(64);

	/** bean依赖之间的映射关系，key对应的bean被value对应的bean集合中的bean依赖（value依赖key） */
	private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<>(64);

```

##### getSingletonCount方法获取注册表中的单实例数量：

```java
	@Override
	public int getSingletonCount() {
		synchronized (this.singletonObjects) {
			return this.registeredSingletons.size();
		}
	}
```

##### getSingletonMutex单实例互斥体集合，向子类和外部协作着公开单例互斥体，如果子类执行任何类型的扩展单例创建阶段，那么它们应该在给定对象上加同步锁。特别是，子类不应该在单例创建中有自己的互斥锁，以避免在lazy-init情况下出现死锁：

```java
	@Override
	public final Object getSingletonMutex() {
		return this.singletonObjects;
	}
```

##### registerSingleton方法注册单实例对象到注册表：

```java
	//注册单实例bean到注册表
	//bean名称不可以为null,单实例对象不可以为null
	@Override
	public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
		Assert.notNull(beanName, "Bean name must not be null");
		Assert.notNull(singletonObject, "Singleton object must not be null");
		synchronized (this.singletonObjects) {
      //从单实例互斥体中通过给定的beanName获取单实例对象
			Object oldObject = this.singletonObjects.get(beanName);
      //如果单实例对象已经存在，则抛出异常
			if (oldObject != null) {
				throw new IllegalStateException("Could not register object [" + singletonObject +
						"] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
			}
      //将bean实例对象注册到注册标志中
			addSingleton(beanName, singletonObject);
		}
	}

	/**
	 * 将给定的单例对象添加到此注册表工厂的单实例缓存中
	 */
	protected void addSingleton(String beanName, Object singletonObject) {
		synchronized (this.singletonObjects) {
      //将单实例对象添加到单实例字典集合
			this.singletonObjects.put(beanName, singletonObject);
      //beanName已被注册存放在了单实例缓存对象singletonObjects中，那么此缓存对象就不需要持有beanName
			this.singletonFactories.remove(beanName);
      //beanName已被注册存放在了单实例缓存对象singletonObjects中，那么此缓存对象就不需要持有beanName
			this.earlySingletonObjects.remove(beanName);
      //将单实例对象beanName添加到单实例集合
			this.registeredSingletons.add(beanName);
		}
	}
```

##### addSingletonFactory方法，如果有必要，添加给定的单例工厂以生成指定的单例对象。

```java
	protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
		Assert.notNull(singletonFactory, "Singleton factory must not be null");
		synchronized (this.singletonObjects) {
      //如果单实例缓存集合不包含指定的beanName
			if (!this.singletonObjects.containsKey(beanName)) {
        //将单实例工厂对象添加到singletonFactories缓存
				this.singletonFactories.put(beanName, singletonFactory);
        //删除earlySingletonObjects集合中给定名称的单实例（如果存在）
				this.earlySingletonObjects.remove(beanName);
        //将指定的beanName添加到单实例beanName集合
				this.registeredSingletons.add(beanName);
			}
		}
	}
```

##### getSingleton方法返回给定beanName在注册表中已经注册的单实例对象：

```java
	@Override
	@Nullable
	public Object getSingleton(String beanName) {
		return getSingleton(beanName, true);
	}

	/**
	 * 返回以给定名称注册的单例对象，
	 * 检查已经实例化的单例，并允许早期引用当前创建的单例（解析循环引用）
	 * @param beanName 要查早的bean的名称
	 * @param allowEarlyReference 是否应创建早期引用
	 * @return 返回已经注册过的单例对象，如果不存在，返回null
	 */
	@Nullable
	protected Object getSingleton(String beanName, boolean allowEarlyReference) {
		// 快速检查没有使用单例锁的现有实例
		Object singletonObject = this.singletonObjects.get(beanName);
    //判定当前bean还未注册或者是正在创建的bean
		if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
      //从earlySingletonObjects早期单实例集合中获取实例
			singletonObject = this.earlySingletonObjects.get(beanName);
      //如果bean还未注册并且允许早期引用
			if (singletonObject == null && allowEarlyReference) {
				synchronized (this.singletonObjects) {
					// 在完整的单例锁中一致地创建早期引用
					singletonObject = this.singletonObjects.get(beanName);
					if (singletonObject == null) {
						singletonObject = this.earlySingletonObjects.get(beanName);
						if (singletonObject == null) {
							ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
							if (singletonFactory != null) {
								singletonObject = singletonFactory.getObject();
                //注册早期单例对象引用
								this.earlySingletonObjects.put(beanName, singletonObject);
                //删除singletonFactorie中的bean
								this.singletonFactories.remove(beanName);
							}
						}
					}
				}
			}
		}
		return singletonObject;
	}

```

##### isSingletonCurrentlyInCreation方法指定指定的单例bean当前是否正在创建：

```java
	public boolean isSingletonCurrentlyInCreation(String beanName) {
		return this.singletonsCurrentlyInCreation.contains(beanName);
	}
```

##### getSingleton方法返回以给定名称注册的单例对象，如果还没有注册则创建并注册一个新对象：

```java
	public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
		Assert.notNull(beanName, "Bean name must not be null");
		synchronized (this.singletonObjects) {
      //获取注册表中指定beanName的单实例对象
			Object singletonObject = this.singletonObjects.get(beanName);
			if (singletonObject == null) {
        //如果正在销毁bean，则直接抛出异常
				if (this.singletonsCurrentlyInDestruction) {
					throw new BeanCreationNotAllowedException(beanName,
							"Singleton bean creation not allowed while singletons of this factory are in destruction " +
							"(Do not request a bean from a BeanFactory in a destroy method implementation!)");
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
				}
        //bean创建之前的回调方法
				beforeSingletonCreation(beanName);
        //是否已经创建单实例集合
				boolean newSingleton = false;
        //如果抑制异常的集合为null,则初始化集合
				boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
				if (recordSuppressedExceptions) {
					this.suppressedExceptions = new LinkedHashSet<>();
				}
				try {
          //获取单实例对象
					singletonObject = singletonFactory.getObject();
          //设置已经创建单实例集合结果为true
					newSingleton = true;
				}
				catch (IllegalStateException ex) {
					// 如果单实例对象存在，则继续执行
					singletonObject = this.singletonObjects.get(beanName);
					if (singletonObject == null) {
						throw ex;
					}
				}
				catch (BeanCreationException ex) {
					if (recordSuppressedExceptions) {
						for (Exception suppressedException : this.suppressedExceptions) {
							ex.addRelatedCause(suppressedException);
						}
					}
					throw ex;
				}
				finally {
					if (recordSuppressedExceptions) {
						this.suppressedExceptions = null;
					}
          //单实例对象创建之后执行回调
					afterSingletonCreation(beanName);
				}
        //如果是新创建的单实例，则注册到注册表中
				if (newSingleton) {
					addSingleton(beanName, singletonObject);
				}
			}
			return singletonObject;
		}
	}
```

##### beforeSingletonCreation方法在单实例对象创建之前调用：

```java
	protected void beforeSingletonCreation(String beanName) {
    //首先判定是否包含在被排除的bean名称集合中，false 不包含
    //首先不包含，然后是添加成功到当前正在创建bean集合
		if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)) {
			throw new BeanCurrentlyInCreationException(beanName);
		}
	}
```

##### afterSingletonCreation方法在单实例对象创建完成后调用：

```java
protected void afterSingletonCreation(String beanName) {
  //首先判定是否包含在被排除的bean名称集合中，false 不包含
  //然后从正在创建单实例集合中删除beanName
   if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.remove(beanName)) {
      throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
   }
}
```

##### getSingletonNames方法获取注册表中单实例对象的beanNames：

```java
	@Override
	public String[] getSingletonNames() {
		synchronized (this.singletonObjects) {
			return StringUtils.toStringArray(this.registeredSingletons);
		}
	}
```

##### registerDisposableBean方法将给定的bean添加到待销毁bean注册表中：

```java
public void registerDisposableBean(String beanName, DisposableBean bean) {
   synchronized (this.disposableBeans) {
      this.disposableBeans.put(beanName, bean);
   }
}
```

registerContainedBean方法注册两个bean之间的包含关系

```java
public void registerContainedBean(String containedBeanName, String containingBeanName) {
		synchronized (this.containedBeanMap) {
			Set<String> containedBeans =
					this.containedBeanMap.computeIfAbsent(containingBeanName, k -> new LinkedHashSet<>(8));
      //注册给定的bean
			if (!containedBeans.add(containedBeanName)) {
				return;
			}
		}
  	//注册给定bean的依赖bean
		registerDependentBean(containedBeanName, containingBeanName);
	}

	/**
	 * 为给定的bean注册一个依赖bean，在销毁给定bean之前销毁它
	 */
	public void registerDependentBean(String beanName, String dependentBeanName) {
		String canonicalName = canonicalName(beanName);

		synchronized (this.dependentBeanMap) {
			Set<String> dependentBeans =
					this.dependentBeanMap.computeIfAbsent(canonicalName, k -> new LinkedHashSet<>(8));
			//注册beanName依赖的dependentBeanName
      if (!dependentBeans.add(dependentBeanName)) {
				return;
			}
		}

		synchronized (this.dependenciesForBeanMap) {
			Set<String> dependenciesForBean =
					this.dependenciesForBeanMap.computeIfAbsent(dependentBeanName, k -> new LinkedHashSet<>(8));
      //注册被beanName依赖的bean
			dependenciesForBean.add(canonicalName);
		}
	}

```

##### getDependenciesForBean方法返回给定beanName被依赖的集合（如果存在）：

```java
public String[] getDependenciesForBean(String beanName) {
   Set<String> dependenciesForBean = this.dependenciesForBeanMap.get(beanName);
   if (dependenciesForBean == null) {
      return new String[0];
   }
   synchronized (this.dependenciesForBeanMap) {
      return StringUtils.toStringArray(dependenciesForBean);
   }
}
```

##### getDependentBeans方法返回给定beanName依赖的bean集合（如果有的话）：

```java
	public String[] getDependentBeans(String beanName) {
		Set<String> dependentBeans = this.dependentBeanMap.get(beanName);
		if (dependentBeans == null) {
			return new String[0];
		}
		synchronized (this.dependentBeanMap) {
			return StringUtils.toStringArray(dependentBeans);
		}
	}
```

##### isDependent方法判定给的beanName是否依赖dependentBeanName（传递依赖）：

```java
	protected boolean isDependent(String beanName, String dependentBeanName) {
    //锁定当前bean依赖的bean名称集合，beanName当前bean,dependentBeanName指依赖的bean
		synchronized (this.dependentBeanMap) {
			return isDependent(beanName, dependentBeanName, null);
		}
	}

	private boolean isDependent(String beanName, String dependentBeanName, @Nullable Set<String> alreadySeen) {
		if (alreadySeen != null && alreadySeen.contains(beanName)) {
			return false;
		}
    //将bean的别名解析为规范名称
		String canonicalName = canonicalName(beanName);
    //获取bean依赖的beanName集合
		Set<String> dependentBeans = this.dependentBeanMap.get(canonicalName);
    //如果给定的bean没有依赖的bean,则返回false
		if (dependentBeans == null) 
			return false;
		}
    //如果指定的bean所依赖的bean集合包含指定的依赖bean，则返回true
		if (dependentBeans.contains(dependentBeanName)) {
			return true;
		}
		//循环递归判定给定bean所依赖的bean集合中的所有bean中是否有依赖指定dependentBeanName名称的bean
		for (String transitiveDependency : dependentBeans) {
			if (alreadySeen == null) {
				alreadySeen = new HashSet<>();
			}
			alreadySeen.add(beanName);
      //递归判定指定bean所依赖的bean的集合是否依赖指定dependentBeanName名称的bean(即依赖传递性)
			if (isDependent(transitiveDependency, dependentBeanName, alreadySeen)) {
				return true;
			}
		}
		return false;
	}
```

##### destroySingletons方法销毁当前实例对象：

```java
public void destroySingletons() {
		if (logger.isTraceEnabled()) {
			logger.trace("Destroying singletons in " + this);
		}
		synchronized (this.singletonObjects) {
      //标记当前正在销毁实例
			this.singletonsCurrentlyInDestruction = true;
		}

		String[] disposableBeanNames;
		synchronized (this.disposableBeans) {
      //将需要销毁的实例对象封装为数组
			disposableBeanNames = StringUtils.toStringArray(this.disposableBeans.keySet());
		}
  //循环销毁实例对象
		for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
			destroySingleton(disposableBeanNames[i]);
		}
		this.containedBeanMap.clear();
		this.dependentBeanMap.clear();
		this.dependenciesForBeanMap.clear();

		clearSingletonCache();
	}
	//销毁指定的实例对象
	public void destroySingleton(String beanName) {
		//删除注册表中指定beanName的单实例对象
		removeSingleton(beanName);

		// Destroy the corresponding DisposableBean instance.
		DisposableBean disposableBean;
		synchronized (this.disposableBeans) {
      //删除注册表中销毁单实例对象
			disposableBean = (DisposableBean) this.disposableBeans.remove(beanName);
		}
		destroyBean(beanName, disposableBean);
	}
//销毁给定beanName的单实例对象，并且要先销毁依赖它的bean
protected void destroyBean(String beanName, @Nullable DisposableBean bean) {
		// Trigger destruction of dependent beans first...
		Set<String> dependencies;
		synchronized (this.dependentBeanMap) {
			// 在同步环境中删除依赖的bean
			dependencies = this.dependentBeanMap.remove(beanName);
		}
		if (dependencies != null) {
			if (logger.isTraceEnabled()) {
				logger.trace("Retrieved dependent beans for bean '" + beanName + "': " + dependencies);
			}
			for (String dependentBeanName : dependencies) {
        //销毁单实例bean
				destroySingleton(dependentBeanName);
			}
		}

		// 真正的销毁bean
		if (bean != null) {
			try {
				bean.destroy();
			}
			catch (Throwable ex) {
				if (logger.isWarnEnabled()) {
					logger.warn("Destruction of bean with name '" + beanName + "' threw an exception", ex);
				}
			}
		}
	...
	}

```

------

GitHub地址:[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)