### 死磕源码系列【springboot之ServletContextInitializerBeans如何将Filter、Servlet、EventListener注册到ServletContext中源码解析】

ServletContextInitializerBeans类表示从ListableBeanFactory容器中获取到的ServletContextInitializer实例集合，包括所有的ServletContextInitializer
beans，并且也包含Servlet、Filter、EventListener beans集合。

这些beans会被排序，按照Servlet->Filter->EventListener->ServletContextInitializer的顺序排列。

springboot创建tomcat容器是在ServletWebServerApplicationContext#createWebServer方法中进行，通过调用getSelfInitializer方法来获取对应的ServletContextInitializer实例对象：

```java
	private org.springframework.boot.web.servlet.ServletContextInitializer getSelfInitializer() {
		return this::selfInitialize;
	}

	private void selfInitialize(ServletContext servletContext) throws ServletException {
    //使用给定的ServletContext准备WebApplicationContext
		prepareWebApplicationContext(servletContext);
		registerApplicationScope(servletContext);
		WebApplicationContextUtils.registerEnvironmentBeans(getBeanFactory(), servletContext);
    //getServletContextInitializerBeans方法获取ServletContextInitializer实例对象、Servlet、Filter、EventListener bean实例对象
		for (ServletContextInitializer beans : getServletContextInitializerBeans()) {
      //调用ServletContextInitializer实例对象的onStartup方法
			beans.onStartup(servletContext);
		}
	}
  //获取ServletContextInitializer实例对象集合类ServletContextInitializerBeans
	protected Collection<ServletContextInitializer> getServletContextInitializerBeans() {
    //参数getBeanFactory()方法获取IOC容器工厂ConfigurableListableBeanFactory的实例对象
		return new ServletContextInitializerBeans(getBeanFactory());
	}
```

ServletContextInitializerBeans是一个从容器ListableBeanFactory中获取的ServletContextInitializer实例对象集合，包含所有的ServletContextInitializer
beans对象，Servlet、Filter、EventListener监听器bean。

```java
public class ServletContextInitializerBeans extends AbstractCollection<ServletContextInitializer> {

	private static final String DISPATCHER_SERVLET_NAME = "dispatcherServlet";

	private static final Log logger = LogFactory.getLog(ServletContextInitializerBeans.class);

	/**
	 * 将发下的bean实例及bean名称集合
	 */
	private final Set<Object> seen = new HashSet<>();
 //存储ServletContextInitializer实例对象
	private final MultiValueMap<Class<?>, ServletContextInitializer> initializers;
 //存储initializers集合中存储的数据类型
	private final List<Class<? extends ServletContextInitializer>> initializerTypes;
  //ServletContextInitializer的已排序不可以修改集合
	private List<ServletContextInitializer> sortedList;

	@SafeVarargs
	public ServletContextInitializerBeans(ListableBeanFactory beanFactory,
			Class<? extends ServletContextInitializer>... initializerTypes) {
    //存储ServletContextInitializer实例对象
		this.initializers = new LinkedMultiValueMap<>();
    //初始哈initializers集合中存储的数据类型，默认为：ServletContextInitializer类型
		this.initializerTypes = (initializerTypes.length != 0) ? Arrays.asList(initializerTypes)
				: Collections.singletonList(ServletContextInitializer.class);
    //将容器中的ServletContextInitializer实例对象按照类别加入到集合
		addServletContextInitializerBeans(beanFactory);
    //将容器中的Servlet、Filter、EventListenr实例对象加入集合
		addAdaptableBeans(beanFactory);
    //将ServletContextInitializer集合进行排序
		List<ServletContextInitializer> sortedInitializers = this.initializers.values().stream()
				.flatMap((value) -> value.stream().sorted(AnnotationAwareOrderComparator.INSTANCE))
				.collect(Collectors.toList());
    //将ServletContextInitializer集合转换为不可修改的已排序集合
		this.sortedList = Collections.unmodifiableList(sortedInitializers);
		logMappings(this.initializers);
	}

	private void addServletContextInitializerBeans(ListableBeanFactory beanFactory) {
		for (Class<? extends ServletContextInitializer> initializerType : this.initializerTypes) {
			//getOrderedBeansOfType方法获取bean实例对象集合
      for (Entry<String, ? extends ServletContextInitializer> initializerBean : getOrderedBeansOfType(beanFactory,
					initializerType)) {
        //将获取到的bean实例对象按照key为ServletContextInitializer.class class实例对象，value为ServletContextInitializer实例对象
				addServletContextInitializerBean(initializerBean.getKey(), initializerBean.getValue(), beanFactory);
			}
		}
	}
	//将ServletContextInitializer、Filter、Servlet、EventListener类的实例对象加入到集合中
	private void addServletContextInitializerBean(String beanName, ServletContextInitializer initializer,
			ListableBeanFactory beanFactory) {
		if (initializer instanceof ServletRegistrationBean) {
			Servlet source = ((ServletRegistrationBean<?>) initializer).getServlet();
      //将Servlet加入集合
			addServletContextInitializerBean(Servlet.class, beanName, initializer, beanFactory, source);
		}
		else if (initializer instanceof FilterRegistrationBean) {
			Filter source = ((FilterRegistrationBean<?>) initializer).getFilter();
      //将Filter加入集合
			addServletContextInitializerBean(Filter.class, beanName, initializer, beanFactory, source);
		}
		else if (initializer instanceof DelegatingFilterProxyRegistrationBean) {
			String source = ((DelegatingFilterProxyRegistrationBean) initializer).getTargetBeanName();
      //将Servlet加入集合
			addServletContextInitializerBean(Filter.class, beanName, initializer, beanFactory, source);
		}
		else if (initializer instanceof ServletListenerRegistrationBean) {
			EventListener source = ((ServletListenerRegistrationBean<?>) initializer).getListener();
      //将EventListener加入集合
			addServletContextInitializerBean(EventListener.class, beanName, initializer, beanFactory, source);
		}
		else {
      //将ServletContextInitializer加入集合
			addServletContextInitializerBean(ServletContextInitializer.class, beanName, initializer, beanFactory,
					initializer);
		}
	}
	/**
	* @param type Filter、Servlet、EventListener类class实例对象
	* @param beanName 是xxRegistrationBean的实例对象bean名称
	* @param initializer ServletContextInitializer实例对象
	**/
	private void addServletContextInitializerBean(Class<?> type, String beanName, ServletContextInitializer initializer,
			ListableBeanFactory beanFactory, Object source) {
    //将ServletContextInitializer实例对象加入集合，key为class类型
		this.initializers.add(type, initializer);
		if (source != null) {
			// 记录被标记为ServletContextInitializer实例对象的Filter、Servlet、EventListener bean实例对象
			this.seen.add(source);
		}
		if (logger.isTraceEnabled()) {
			String resourceDescription = getResourceDescription(beanName, beanFactory);
      //获取initializer实例化对象的order值
			int order = getOrder(initializer);
			logger.trace("Added existing " + type.getSimpleName() + " initializer bean '" + beanName + "'; order="
					+ order + ", resource=" + resourceDescription);
		}
	}

	private String getResourceDescription(String beanName, ListableBeanFactory beanFactory) {
		if (beanFactory instanceof BeanDefinitionRegistry) {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
			return registry.getBeanDefinition(beanName).getResourceDescription();
		}
		return "unknown";
	}

	@SuppressWarnings("unchecked")
	protected void addAdaptableBeans(ListableBeanFactory beanFactory) {
		MultipartConfigElement multipartConfig = getMultipartConfig(beanFactory);
    //将容器中的Servlet实例对象转换为RegistrationBean实例对象加入ServletContextInitializer集合
		addAsRegistrationBean(beanFactory, Servlet.class, new ServletRegistrationBeanAdapter(multipartConfig));
    //将容器中的Filter实例对象转换为RegistrationBean实例对象加入ServletContextInitializer集合
		addAsRegistrationBean(beanFactory, Filter.class, new FilterRegistrationBeanAdapter());
		for (Class<?> listenerType : ServletListenerRegistrationBean.getSupportedTypes()) {
      //将容器中的EventListener实例对象转换为RegistrationBean实例对象加入ServletContextInitializer集合
			addAsRegistrationBean(beanFactory, EventListener.class, (Class<EventListener>) listenerType,
					new ServletListenerRegistrationBeanAdapter());
		}
	}

	private MultipartConfigElement getMultipartConfig(ListableBeanFactory beanFactory) {
		List<Entry<String, MultipartConfigElement>> beans = getOrderedBeansOfType(beanFactory,
				MultipartConfigElement.class);
		return beans.isEmpty() ? null : beans.get(0).getValue();
	}

	protected <T> void addAsRegistrationBean(ListableBeanFactory beanFactory, Class<T> type,
			RegistrationBeanAdapter<T> adapter) {
		addAsRegistrationBean(beanFactory, type, type, adapter);
	}

	private <T, B extends T> void addAsRegistrationBean(ListableBeanFactory beanFactory, Class<T> type,
			Class<B> beanType, RegistrationBeanAdapter<T> adapter) {
    //获取beanName及实例对象属性集合对象
		List<Map.Entry<String, B>> entries = getOrderedBeansOfType(beanFactory, beanType, this.seen);
		for (Entry<String, B> entry : entries) {
      //获取beanName
			String beanName = entry.getKey();
      //获取beanName对应的实例对象属性集合
			B bean = entry.getValue();
      //将对象加入seen集合
			if (this.seen.add(bean)) {
				// 根据适配器将bean转换为RegistrationBean对象
				RegistrationBean registration = adapter.createRegistrationBean(beanName, bean, entries.size());
        //获取排序顺序
				int order = getOrder(bean);
        //设置RegistrationBean的优先级顺序
				registration.setOrder(order);
        //将RegistrationBean加入到集合
				this.initializers.add(type, registration);
				if (logger.isTraceEnabled()) {
					logger.trace("Created " + type.getSimpleName() + " initializer for bean '" + beanName + "'; order="
							+ order + ", resource=" + getResourceDescription(beanName, beanFactory));
				}
			}
		}
	}
	//获取指定实例对象的order顺序值
	private int getOrder(Object value) {
		return new AnnotationAwareOrderComparator() {
			@Override
			public int getOrder(Object obj) {
				return super.getOrder(obj);
			}
		}.getOrder(value);
	}
	//根据class类型获取IOC容器中的bean实例对象，获取后对这些对象进行order排序
	private <T> List<Entry<String, T>> getOrderedBeansOfType(ListableBeanFactory beanFactory, Class<T> type) {
		return getOrderedBeansOfType(beanFactory, type, Collections.emptySet());
	}
	//获取指定IOC容器中指定class类型的bean集合
	private <T> List<Entry<String, T>> getOrderedBeansOfType(ListableBeanFactory beanFactory, Class<T> type,
			Set<?> excludes) {
    //获取指定的class类型的beanName集合
		String[] names = beanFactory.getBeanNamesForType(type, true, false);
		Map<String, T> map = new LinkedHashMap<>();
		for (String name : names) {
			if (!excludes.contains(name) && !ScopedProxyUtils.isScopedTarget(name)) {
        //获取指定beanName及类型的bean实例对象
				T bean = beanFactory.getBean(name, type);
				if (!excludes.contains(bean)) {
					map.put(name, bean);
				}
			}
		}
		List<Entry<String, T>> beans = new ArrayList<>(map.entrySet());
    //bean实例对象排序
		beans.sort((o1, o2) -> AnnotationAwareOrderComparator.INSTANCE.compare(o1.getValue(), o2.getValue()));
		return beans;
	}

	private void logMappings(MultiValueMap<Class<?>, ServletContextInitializer> initializers) {
		if (logger.isDebugEnabled()) {
			logMappings("filters", initializers, Filter.class, FilterRegistrationBean.class);
			logMappings("servlets", initializers, Servlet.class, ServletRegistrationBean.class);
		}
	}

	private void logMappings(String name, MultiValueMap<Class<?>, ServletContextInitializer> initializers,
			Class<?> type, Class<? extends RegistrationBean> registrationType) {
		List<ServletContextInitializer> registrations = new ArrayList<>();
		registrations.addAll(initializers.getOrDefault(registrationType, Collections.emptyList()));
		registrations.addAll(initializers.getOrDefault(type, Collections.emptyList()));
		String info = registrations.stream().map(Object::toString).collect(Collectors.joining(", "));
		logger.debug("Mapping " + name + ": " + info);
	}

	@Override
	public Iterator<ServletContextInitializer> iterator() {
		return this.sortedList.iterator();
	}
	//获取已排序的ServletContextInitializer集合大小
	@Override
	public int size() {
		return this.sortedList.size();
	}

	/**
	 * 讲一个bean转换为RegistrationBean适配器类型接口 (RegistrationBean是ServletContextInitializer的
	 * 的一个增强接口
	 */
	@FunctionalInterface
	protected interface RegistrationBeanAdapter<T> {

		RegistrationBean createRegistrationBean(String name, T source, int totalNumberOfSourceBeans);

	}

	/**
	 * Servlet的RegistrationBeanAdapter适配器接口
	 */
	private static class ServletRegistrationBeanAdapter implements RegistrationBeanAdapter<Servlet> {

		private final MultipartConfigElement multipartConfig;

		ServletRegistrationBeanAdapter(MultipartConfigElement multipartConfig) {
			this.multipartConfig = multipartConfig;
		}

		@Override
		public RegistrationBean createRegistrationBean(String name, Servlet source, int totalNumberOfSourceBeans) {
			String url = (totalNumberOfSourceBeans != 1) ? "/" + name + "/" : "/";
			if (name.equals(DISPATCHER_SERVLET_NAME)) {
				url = "/"; // always map the main dispatcherServlet to "/"
			}
			ServletRegistrationBean<Servlet> bean = new ServletRegistrationBean<>(source, url);
			bean.setName(name);
			bean.setMultipartConfig(this.multipartConfig);
			return bean;
		}

	}

	/**
	 * Filter适配类RegistrationBeanAdapter
	 */
	private static class FilterRegistrationBeanAdapter implements RegistrationBeanAdapter<Filter> {

		@Override
		public RegistrationBean createRegistrationBean(String name, Filter source, int totalNumberOfSourceBeans) {
			FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>(source);
			bean.setName(name);
			return bean;
		}

	}

	/**
	 * EventListener的RegistrationBeanAdapter适配接口
	 */
	private static class ServletListenerRegistrationBeanAdapter implements RegistrationBeanAdapter<EventListener> {

		@Override
		public RegistrationBean createRegistrationBean(String name, EventListener source,
				int totalNumberOfSourceBeans) {
			return new ServletListenerRegistrationBean<>(source);
		}

	}

}

```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)