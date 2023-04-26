### 死磕源码系列【ApplicationEventMulticaster接口及实现类源码解析】

>
ApplicationEventMulticaster接口的实现类可以管理多个ApplicationListener监听器对象，并且发布事件到监听器；ApplicationEventMulticaster其实是ApplicationEventPublisher发布事件的代理类，通常作为SpringApplicationRunListener接口实现类EventPublishingRunListener的一个属性来使用；

类的结构图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020100916102123.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhb21pbmd5YW5n,size_16,color_FFFFFF,t_70#pic_center)

ApplicationEventMulticaster接口只有一个直接实现抽象类AbstractApplicationEventMulticaster及SimpleApplicationEventMulticaster实现类；

ApplicationEventMulticaster接口源码如下：

```java
public interface ApplicationEventMulticaster {

	/**
	 * 添加监听器以接收所有的事件通知
	 */
	void addApplicationListener(ApplicationListener<?> listener);

	/**
	 * 添加监听器以接收所有的事件通知，参数是监听器bean名称
	 */
	void addApplicationListenerBean(String listenerBeanName);

	/**
	 * 从通知列表中删除监听器
	 * @param listener 将被删除的监听器
	 */
	void removeApplicationListener(ApplicationListener<?> listener);

	/**
	 * 从通知列表中删除监听器
	 * @param listenerBeanName 将被删除的监听器
	 */
	void removeApplicationListenerBean(String listenerBeanName);

	/**
	 * 删除注册到代理上的所有监听器
	 * 在remove调用之后，代理不会对事件通知执行任何操作直到有新的监听器注册
	 */
	void removeAllListeners();

	/**
	 * 将给定的事件广播到对应的监听器上
	 * @param 需要广播的事件
	 */
	void multicastEvent(ApplicationEvent event);

	/**
	 * 将应用程序事件广播到对应的监听器上
	 * @param 需要广播的事件
	 * @param 事件类型
	 */
	void multicastEvent(ApplicationEvent event, @Nullable ResolvableType eventType);

}
```

AbstractApplicationEventMulticaster类是ApplicationEventMulticaster接口的抽象实现，提供基本的监听器注册工具方法（注册和移除监听器）；默认情况下不允许同一个监听器有多个实例，因为该类会将监听器保存在ListenerRetriever集合类的Set集合中。

AbstractApplicationEventMulticaster类属性源码如下：

```java
public abstract class AbstractApplicationEventMulticaster
      implements ApplicationEventMulticaster, BeanClassLoaderAware, BeanFactoryAware {
	//创建监听器助手类，用于存放应用程序的监听器集合，参数是否是预过滤监听器为false
   private final ListenerRetriever defaultRetriever = new ListenerRetriever(false);
	//ListenerCacheKey是基于事件类型和源类型的类作为key用来存储监听器助手ListenerRetriever
   final Map<ListenerCacheKey, ListenerRetriever> retrieverCache = new ConcurrentHashMap<>(64);
	//类加载器
   @Nullable
   private ClassLoader beanClassLoader;
	//IOC容器工厂类
   @Nullable
   private ConfigurableBeanFactory beanFactory;
  //互斥的监听器助手类
   private Object retrievalMutex = this.defaultRetriever;
}
```

ListenerRetriever监听器助手类(
封装一组特定目标监听器的帮助类，允许有效地检索预过滤的监听器，此助手的实例按照时间类型和源类型缓存)：

```java
	private class ListenerRetriever {
		//存放应用程序事件监听器，有序、不可重复
		public final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<>();
		//存放应用程序事件监听器bean名称，有序，不可重复
		public final Set<String> applicationListenerBeans = new LinkedHashSet<>();
		//是否预过滤监听器
		private final boolean preFiltered;

		public ListenerRetriever(boolean preFiltered) {
			this.preFiltered = preFiltered;
		}
		//获取应用程序的时间监听器
		public Collection<ApplicationListener<?>> getApplicationListeners() {
      //创建一个指定大小的ApplicationListener监听器List集合
			List<ApplicationListener<?>> allListeners = new ArrayList<>(
					this.applicationListeners.size() + this.applicationListenerBeans.size());
			allListeners.addAll(this.applicationListeners);
      //如果存放监听器bean name的集合不为空
			if (!this.applicationListenerBeans.isEmpty()) {
        //获取IOC容器工厂类
				BeanFactory beanFactory = getBeanFactory();
				for (String listenerBeanName : this.applicationListenerBeans) {
					try {
            //获取指定bean name的监听器实例
						ApplicationListener<?> listener = beanFactory.getBean(listenerBeanName, ApplicationListener.class);
            //判定如果是预过滤的监听器或者集合中不包含监听器实例则添加到集合中
						if (this.preFiltered || !allListeners.contains(listener)) {
							allListeners.add(listener);
						}
					}
					catch (NoSuchBeanDefinitionException ex) {
						// Singleton listener instance (without backing bean definition) disappeared -
						// probably in the middle of the destruction phase
					}
				}
			}
			if (!this.preFiltered || !this.applicationListenerBeans.isEmpty()) {
				AnnotationAwareOrderComparator.sort(allListeners);
			}
			return allListeners;
		}
	}
```

AbstractApplicationEventMulticaster#addApplicationListener方法添加应用程序监听器类：

```java
	@Override
	public void addApplicationListener(ApplicationListener<?> listener) {
    //锁定监听器助手对象
		synchronized (this.retrievalMutex) {
			// 如果已经注册，则显式删除已经注册的监听器对象
			// 为了避免调用重复的监听器对象
			Object singletonTarget = AopProxyUtils.getSingletonTarget(listener);
			if (singletonTarget instanceof ApplicationListener) {
        //删除监听器对象
				this.defaultRetriever.applicationListeners.remove(singletonTarget);
			}
      //新增监听器对象
			this.defaultRetriever.applicationListeners.add(listener);
      //清空监听器助手缓存Map
			this.retrieverCache.clear();
		}
	}

	@Override
	public void addApplicationListenerBean(String listenerBeanName) {
    //锁定监听器助手对象
		synchronized (this.retrievalMutex) {
      //新增bean name为listenerBeanName的监听器对象到集合之中
			this.defaultRetriever.applicationListenerBeans.add(listenerBeanName);
      //清空监听器助手缓存Map
			this.retrieverCache.clear();
		}
	}
```

删除监听器对象：

```java
	@Override
	public void removeApplicationListener(ApplicationListener<?> listener) {
		synchronized (this.retrievalMutex) {
			this.defaultRetriever.applicationListeners.remove(listener);
			this.retrieverCache.clear();
		}
	}

	@Override
	public void removeApplicationListenerBean(String listenerBeanName) {
		synchronized (this.retrievalMutex) {
			this.defaultRetriever.applicationListenerBeans.remove(listenerBeanName);
			this.retrieverCache.clear();
		}
	}

	@Override
	public void removeAllListeners() {
		synchronized (this.retrievalMutex) {
			this.defaultRetriever.applicationListeners.clear();
			this.defaultRetriever.applicationListenerBeans.clear();
			this.retrieverCache.clear();
		}
	}
```

AbstractApplicationEventMulticaster#getApplicationListeners方法获取监听器对象（匹配指定的时间类型，不匹配很早就被排除在外的监听器）

```java
	protected Collection<ApplicationListener<?>> getApplicationListeners(
			ApplicationEvent event, ResolvableType eventType) {
		//事件源，事件最初发生在其上的对象
		Object source = event.getSource();
    //事件源class对象
		Class<?> sourceType = (source != null ? source.getClass() : null);
    //创建基于事件源，和源类型的监听器助手cacheKey
		ListenerCacheKey cacheKey = new ListenerCacheKey(eventType, sourceType);

		//快速检测监听器助手缓存ConcurrentHashMap中是否存在指定的cacheKey
		ListenerRetriever retriever = this.retrieverCache.get(cacheKey);
		if (retriever != null) {
      //如果存在指定的key，则返回应用程序的监听器对象
			return retriever.getApplicationListeners();
		}
		//如果类加载器为null,或者事件源在给定的类加载器上下文是安全的并且源类型为null或者源类型在指定上下文是安全的
		if (this.beanClassLoader == null ||
				(ClassUtils.isCacheSafe(event.getClass(), this.beanClassLoader) &&
						(sourceType == null || ClassUtils.isCacheSafe(sourceType, this.beanClassLoader)))) {
			// 同步从ListenerRetriever监听器助手中获取指定的监听器
			synchronized (this.retrievalMutex) {
				retriever = this.retrieverCache.get(cacheKey);
				if (retriever != null) {
          //返回监听器助手中存储的监听器对象
					return retriever.getApplicationListeners();
				}
				retriever = new ListenerRetriever(true);
        //实际检索给定事件和源类型的应用程序监听器
				Collection<ApplicationListener<?>> listeners =
						retrieveApplicationListeners(eventType, sourceType, retriever);
				this.retrieverCache.put(cacheKey, retriever);
				return listeners;
			}
		}
		else {
			// 无ListenerRetriever监听器助手 -> 无需同步缓存
			return retrieveApplicationListeners(eventType, sourceType, null);
		}
	}

```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)