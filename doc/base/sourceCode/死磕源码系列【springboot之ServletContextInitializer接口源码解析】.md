### 死磕源码系列【springboot之ServletContextInitializer接口源码解析】

> springboot提供在Servlet 3.0+环境中用于编码方式配置ServletContext的接口，此接口（ServletContextInitializer）主要被RegistrationBean抽象类实现用于往ServletContext容器中注册Servlet、Filter或者Listener。这些实现了此接口的Bean的生命周期将会交给Spring容器管理，而不会交给Servlet容器。

##### 1.ServletContextInitializer接口源码：

```java
@FunctionalInterface
public interface ServletContextInitializer {

	/**
	 * 使用初始化所需要的任何servlet、Filter、Listener上下文参数及所需要的参数进行初始化ServletContext
	 * @param servletContext 将要初始化的上下文
	 * @throws ServletException 抛出发生的异常信息
	 */
	void onStartup(ServletContext servletContext) throws ServletException;

}
```

##### 2.RegistrationBean是基于Servlet3.0+的注册bean的基类，此类是一个抽象类，里面的很多方法都是抽象方法，具体由其子类来实现

```java
public abstract class RegistrationBean implements ServletContextInitializer, Ordered {

	private static final Log logger = LogFactory.getLog(RegistrationBean.class);
	//注册bean的优先级
	private int order = Ordered.LOWEST_PRECEDENCE;
	//指示注册是否已经启用的标记
	private boolean enabled = true;

	@Override
	public final void onStartup(ServletContext servletContext) throws ServletException {
    //获取注册bean的描述信息
		String description = getDescription();
    //判定是否开启注册功能，否则打印info日志并且直接返回
		if (!isEnabled()) {
			logger.info(StringUtils.capitalize(description) + " was not registered (disabled)");
			return;
		}
    //调用抽象的注册方法
		register(description, servletContext);
	}

	/**
	 * 返回注册的描述说明
	 */
	protected abstract String getDescription();

	/**
	 * 在servlet上下文中注册这个bean.
	 * @param description 正在注册项的描述
	 * @param servletContext the servlet context
	 */
	protected abstract void register(String description, ServletContext servletContext);

	/**
	 * 指示注册是否已经启用的标记
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * 返回注册是否一起用的标记boolean值
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * 设置注册bean的优先级顺序
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * 获取注册bean的优先级顺序
	 */
	@Override
	public int getOrder() {
		return this.order;
	}

}
```

##### 3.DynamicRegistrationBean是一个抽象类，继承了RegistrationBean抽象类，是基于Servlet3.0+的注册bean的基类

```java
public abstract class DynamicRegistrationBean<D extends Registration.Dynamic> extends RegistrationBean {

	private static final Log logger = LogFactory.getLog(RegistrationBean.class);
	//注册的名称，如果没有指定，将使用bean的名称
	private String name;
  //是否支持异步注册
	private boolean asyncSupported = true;

	private Map<String, String> initParameters = new LinkedHashMap<>();

	/**
	 * 设置注册的名称，如果没有指定，将使用bean的名称
	 */
	public void setName(String name) {
		Assert.hasLength(name, "Name must not be empty");
		this.name = name;
	}

	/**
	 * 如果此操作支持异步注册，则支持异步集，如果未指定，则默认为true
	 */
	public void setAsyncSupported(boolean asyncSupported) {
		this.asyncSupported = asyncSupported;
	}

	/**
	 * 判定当前注册是否支持异步注册
	 */
	public boolean isAsyncSupported() {
		return this.asyncSupported;
	}

	/**
	 * 为此注册设置init参数，调用此方法将替换任何现有的init参数
	 */
	public void setInitParameters(Map<String, String> initParameters) {
		Assert.notNull(initParameters, "InitParameters must not be null");
		this.initParameters = new LinkedHashMap<>(initParameters);
	}

	/**
	 * 返回注册的初始化参数
	 */
	public Map<String, String> getInitParameters() {
		return this.initParameters;
	}

	/**
	 * 添加一个init参数，用相同的名称替换任何现有的参数
	 * @param name 初始化参数名
	 * @param value 初始化参数值
	 */
	public void addInitParameter(String name, String value) {
		Assert.notNull(name, "Name must not be null");
		this.initParameters.put(name, value);
	}
	//注册方法的具体实现
	@Override
	protected final void register(String description, ServletContext servletContext) {
    //调用具体的注册方法
		D registration = addRegistration(description, servletContext);
		if (registration == null) {
			logger.info(StringUtils.capitalize(description) + " was not registered (possibly already registered?)");
			return;
		}
    //配置注册结果
		configure(registration);
	}
	//注册具体动作抽象方法
	protected abstract D addRegistration(String description, ServletContext servletContext);
  //注册结果及设置参数
	protected void configure(D registration) {
		registration.setAsyncSupported(this.asyncSupported);
		if (!this.initParameters.isEmpty()) {
			registration.setInitParameters(this.initParameters);
		}
	}

	/**
	 * 推断此注册的名称，将返回用户指定的名称或回退到基于约定的命名
	 */
	protected final String getOrDeduceName(Object value) {
		return (this.name != null) ? this.name : Conventions.getVariableName(value);
	}

}
```

##### 4.ServletRegistrationBean是基于servlet3.0+容器注册Servlet，类似于ServletContext#addServlet(String, Servlet)注册方法，但是提供了注册为spring bean的友好设计

```java
public class ServletRegistrationBean<T extends Servlet> extends DynamicRegistrationBean<ServletRegistration.Dynamic> {
	//默认路径匹配
	private static final String[] DEFAULT_MAPPINGS = { "/*" };
  //将要注册的servlet
	private T servlet;
	//映射的URL路由模式集合
	private Set<String> urlMappings = new LinkedHashSet<>();
	//如果省略了URL映射，则应将其替换为"/*"
	private boolean alwaysMapUrl = true;
	//启动优先级
	private int loadOnStartup = -1;
	//要设置的配置
	private MultipartConfigElement multipartConfig;

	/**
	 * 创建一个ServletRegistrationBean实例对象
	 */
	public ServletRegistrationBean() {
	}

	/**
	 * 创建一个ServletRegistrationBean实例对象，并且制定servlet和URL映射参数
	 */
	public ServletRegistrationBean(T servlet, String... urlMappings) {
		this(servlet, true, urlMappings);
	}

	/**
	 * 创建一个ServletRegistrationBean实例对象，并且制定servlet、如果省略URL则使用"/*"替换,和URL映射参数
	 * @param servlet the servlet being mapped
	 * @param alwaysMapUrl if omitted URL mappings should be replaced with '/*'
	 * @param urlMappings the URLs being mapped
	 */
	public ServletRegistrationBean(T servlet, boolean alwaysMapUrl, String... urlMappings) {
		Assert.notNull(servlet, "Servlet must not be null");
		Assert.notNull(urlMappings, "UrlMappings must not be null");
		this.servlet = servlet;
		this.alwaysMapUrl = alwaysMapUrl;
		this.urlMappings.addAll(Arrays.asList(urlMappings));
	}

	/**
	 * Sets the servlet to be registered.
	 * @param servlet the servlet
	 */
	public void setServlet(T servlet) {
		Assert.notNull(servlet, "Servlet must not be null");
		this.servlet = servlet;
	}

	/**
	 * 返回正在注册的servlet
	 */
	public T getServlet() {
		return this.servlet;
	}

	/**
	 * 设置servlet的URL映射，如果未指定，映射将默认为"/",这将替换以前指定的任何映射
	 */
	public void setUrlMappings(Collection<String> urlMappings) {
		Assert.notNull(urlMappings, "UrlMappings must not be null");
		this.urlMappings = new LinkedHashSet<>(urlMappings);
	}

	/**
	 * 返回注册的servlet的映射集合
	 * @return the urlMappings
	 */
	public Collection<String> getUrlMappings() {
		return this.urlMappings;
	}

	/**
	 * 添加注册的servlet的url映射
	 * @param urlMappings the mappings to add
	 * @see #setUrlMappings(Collection)
	 */
	public void addUrlMappings(String... urlMappings) {
		Assert.notNull(urlMappings, "UrlMappings must not be null");
		this.urlMappings.addAll(Arrays.asList(urlMappings));
	}

	/**
	 * 设置loadOnStartup方法的优先级
	 */
	public void setLoadOnStartup(int loadOnStartup) {
		this.loadOnStartup = loadOnStartup;
	}

	/**
	 * 设置注册servlet的配置
	 */
	public void setMultipartConfig(MultipartConfigElement multipartConfig) {
		this.multipartConfig = multipartConfig;
	}

	/**
	 * 获取servlet的配置
	 */
	public MultipartConfigElement getMultipartConfig() {
		return this.multipartConfig;
	}
	//获取注册servlet的描述
	@Override
	protected String getDescription() {
		Assert.notNull(this.servlet, "Servlet must not be null");
		return "servlet " + getServletName();
	}
	//核心，向ServletContext注册servlet对象
	@Override
	protected ServletRegistration.Dynamic addRegistration(String description, ServletContext servletContext) {
		String name = getServletName();
		return servletContext.addServlet(name, this.servlet);
	}

	/**
	 * 配置注册配置
	 */
	@Override
	protected void configure(ServletRegistration.Dynamic registration) {
		super.configure(registration);
		String[] urlMapping = StringUtils.toStringArray(this.urlMappings);
		if (urlMapping.length == 0 && this.alwaysMapUrl) {
			urlMapping = DEFAULT_MAPPINGS;
		}
		if (!ObjectUtils.isEmpty(urlMapping)) {
			registration.addMapping(urlMapping);
		}
		registration.setLoadOnStartup(this.loadOnStartup);
		if (this.multipartConfig != null) {
			registration.setMultipartConfig(this.multipartConfig);
		}
	}

	/**
	 * 获取将被注册的servlet的名字
	 * @return the servlet name
	 */
	public String getServletName() {
		return getOrDeduceName(this.servlet);
	}

	@Override
	public String toString() {
		return getServletName() + " urls=" + getUrlMappings();
	}

}

```

##### 5.AbstractFilterRegistrationBean是基于Servlet3.0+容器注册Filter的抽象基类

```java
public abstract class AbstractFilterRegistrationBean<T extends Filter> extends DynamicRegistrationBean<Dynamic> {
	//默认的URL映射路径
	private static final String[] DEFAULT_URL_MAPPINGS = { "/*" };
	
	private Set<ServletRegistrationBean<?>> servletRegistrationBeans = new LinkedHashSet<>();

	private Set<String> servletNames = new LinkedHashSet<>();

	private Set<String> urlPatterns = new LinkedHashSet<>();

	private EnumSet<DispatcherType> dispatcherTypes;

	private boolean matchAfter = false;

	/**
	 * 创建AbstractFilterRegistrationBean实例对象，参数为ServletRegistrationBean集合
	 */
	AbstractFilterRegistrationBean(ServletRegistrationBean<?>... servletRegistrationBeans) {
		Assert.notNull(servletRegistrationBeans, "ServletRegistrationBeans must not be null");
		Collections.addAll(this.servletRegistrationBeans, servletRegistrationBeans);
	}

	/**
	 * 设置ServletRegistrationBean，过滤器将会针对其进行注册
	 */
	public void setServletRegistrationBeans(Collection<? extends ServletRegistrationBean<?>> servletRegistrationBeans) {
		Assert.notNull(servletRegistrationBeans, "ServletRegistrationBeans must not be null");
		this.servletRegistrationBeans = new LinkedHashSet<>(servletRegistrationBeans);
	}

	/**
	 * 返回Filter简要根据ServletRegistrationBean集合对象进行注册的ServletRegistrationBean集合
	 */
	public Collection<ServletRegistrationBean<?>> getServletRegistrationBeans() {
		return this.servletRegistrationBeans;
	}

	/**
	 * 添加ServletRegistrationBean对象
	 */
	public void addServletRegistrationBeans(ServletRegistrationBean<?>... servletRegistrationBeans) {
		Assert.notNull(servletRegistrationBeans, "ServletRegistrationBeans must not be null");
		Collections.addAll(this.servletRegistrationBeans, servletRegistrationBeans);
	}

	/**
	 * 设置过滤器将注册的servlet名称，这将替换以前指定的任何servlet名称
	 */
	public void setServletNames(Collection<String> servletNames) {
		Assert.notNull(servletNames, "ServletNames must not be null");
		this.servletNames = new LinkedHashSet<>(servletNames);
	}

	/**
	 * 返回用于注册过滤器的servlet名称的可变集合
	 */
	public Collection<String> getServletNames() {
		return this.servletNames;
	}

	/**
	 * 为Filter添加servlet名称
	 */
	public void addServletNames(String... servletNames) {
		Assert.notNull(servletNames, "ServletNames must not be null");
		this.servletNames.addAll(Arrays.asList(servletNames));
	}

	/**
	 * 设置将根据其注册Filter的URL模式，这将替换以前指定的任何URL模式
	 */
	public void setUrlPatterns(Collection<String> urlPatterns) {
		Assert.notNull(urlPatterns, "UrlPatterns must not be null");
		this.urlPatterns = new LinkedHashSet<>(urlPatterns);
	}

	/**
	 * 返回一个URL模式的可变集合，如Servlet规范中定义的那样，过滤器针对这些模式进行注册
	 */
	public Collection<String> getUrlPatterns() {
		return this.urlPatterns;
	}

	/**
	 * 添加URL模式，如Servlet规范中所定义的，过滤器将针对这些模式进行注册
	 */
	public void addUrlPatterns(String... urlPatterns) {
		Assert.notNull(urlPatterns, "UrlPatterns must not be null");
		Collections.addAll(this.urlPatterns, urlPatterns);
	}

	/**
	 * 
	 * @param first the first dispatcher type
	 * @param rest additional dispatcher types
	 */
	public void setDispatcherTypes(DispatcherType first, DispatcherType... rest) {
		this.dispatcherTypes = EnumSet.of(first, rest);
	}

	/**
	 * Sets the dispatcher types that should be used with the registration. If not
	 * specified the types will be deduced based on the value of
	 * {@link #isAsyncSupported()}.
	 * @param dispatcherTypes the dispatcher types
	 */
	public void setDispatcherTypes(EnumSet<DispatcherType> dispatcherTypes) {
		this.dispatcherTypes = dispatcherTypes;
	}

	/**
	 * Set if the filter mappings should be matched after any declared filter mappings of
	 * the ServletContext. Defaults to {@code false} indicating the filters are supposed
	 * to be matched before any declared filter mappings of the ServletContext.
	 * @param matchAfter if filter mappings are matched after
	 */
	public void setMatchAfter(boolean matchAfter) {
		this.matchAfter = matchAfter;
	}

	/**
	 * Return if filter mappings should be matched after any declared Filter mappings of
	 * the ServletContext.
	 * @return if filter mappings are matched after
	 */
	public boolean isMatchAfter() {
		return this.matchAfter;
	}

	@Override
	protected String getDescription() {
		Filter filter = getFilter();
		Assert.notNull(filter, "Filter must not be null");
		return "filter " + getOrDeduceName(filter);
	}
	//注册过滤器
	@Override
	protected Dynamic addRegistration(String description, ServletContext servletContext) {
		Filter filter = getFilter();
		return servletContext.addFilter(getOrDeduceName(filter), filter);
	}

	/**
	 * 配置过滤器的配置
	 */
	@Override
	protected void configure(FilterRegistration.Dynamic registration) {
		super.configure(registration);
		EnumSet<DispatcherType> dispatcherTypes = this.dispatcherTypes;
		if (dispatcherTypes == null) {
			T filter = getFilter();
			if (ClassUtils.isPresent("org.springframework.web.filter.OncePerRequestFilter",
					filter.getClass().getClassLoader()) && filter instanceof OncePerRequestFilter) {
				dispatcherTypes = EnumSet.allOf(DispatcherType.class);
			}
			else {
				dispatcherTypes = EnumSet.of(DispatcherType.REQUEST);
			}
		}
		Set<String> servletNames = new LinkedHashSet<>();
		for (ServletRegistrationBean<?> servletRegistrationBean : this.servletRegistrationBeans) {
			servletNames.add(servletRegistrationBean.getServletName());
		}
		servletNames.addAll(this.servletNames);
		if (servletNames.isEmpty() && this.urlPatterns.isEmpty()) {
			registration.addMappingForUrlPatterns(dispatcherTypes, this.matchAfter, DEFAULT_URL_MAPPINGS);
		}
		else {
			if (!servletNames.isEmpty()) {
				registration.addMappingForServletNames(dispatcherTypes, this.matchAfter,
						StringUtils.toStringArray(servletNames));
			}
			if (!this.urlPatterns.isEmpty()) {
				registration.addMappingForUrlPatterns(dispatcherTypes, this.matchAfter,
						StringUtils.toStringArray(this.urlPatterns));
			}
		}
	}

	/**
	 * Return the {@link Filter} to be registered.
	 * @return the filter
	 */
	public abstract T getFilter();

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(getOrDeduceName(this));
		if (this.servletNames.isEmpty() && this.urlPatterns.isEmpty()) {
			builder.append(" urls=").append(Arrays.toString(DEFAULT_URL_MAPPINGS));
		}
		else {
			if (!this.servletNames.isEmpty()) {
				builder.append(" servlets=").append(this.servletNames);
			}
			if (!this.urlPatterns.isEmpty()) {
				builder.append(" urls=").append(this.urlPatterns);
			}
		}
		builder.append(" order=").append(getOrder());
		return builder.toString();
	}

}

```



##### 6.FilterRegistrationBean是基于Servlet3.0+容器注册Filter，类似于ServletContext#addFilter(String, Filter)注册过滤器的特性，但是提供了注册为spring bean的友好特性

```java
public class FilterRegistrationBean<T extends Filter> extends AbstractFilterRegistrationBean<T> {
	//将要注册的Filter对象
	private T filter;

	/**
	 * 创建一个FilterRegistrationBean实例对象
	 */
	public FilterRegistrationBean() {
	}

	/**
	 * 创建一个新的FilterRegistrationBean实例，使用指定的Filter和ServletRegistrationBean作为参数
	 * @param filter the filter to register
	 * @param servletRegistrationBeans associate {@link ServletRegistrationBean}s
	 */
	public FilterRegistrationBean(T filter, ServletRegistrationBean<?>... servletRegistrationBeans) {
		super(servletRegistrationBeans);
		Assert.notNull(filter, "Filter must not be null");
		this.filter = filter;
	}
	//获取将要注册的获取器
	@Override
	public T getFilter() {
		return this.filter;
	}

	/**
	 * 设置将要被注册的过滤器
	 */
	public void setFilter(T filter) {
		Assert.notNull(filter, "Filter must not be null");
		this.filter = filter;
	}

}

```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)