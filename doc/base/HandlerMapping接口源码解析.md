### HandlerMapping接口源码解析

> 实现HandlerMapping接口的类用来定义request请求和handler object之间的映射关系；request请求可以理解为路由url、RequestMappingInfo，handler object理解为控制器类；RequestMappingHandlerMapping类就是实现此接口并将容器中所有的控制器的RequestMappingInfo请求和HandlerMethod注册到内存之中，方便真实的请求发送过来调用具体的控制器方法；本文将以RequestMappingHandlerMapping为主线来讲解。

接口源码如下：

```java
public interface HandlerMapping {
	/**
	 * 返回一个包含handler Object和所有拦截器的HandlerExecutionChain
	 */
	@Nullable
	HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;
}
```

##### 1.HandlerMapping的初始化

容器加载时DispatcherServlet中的初始化方法initStrategies会被调用，里面的initHandlerMappings(context)方法会被执行，源码如下：

```java
  //容器中HandlerMapping接口实现类集合	
  @Nullable
	private List<HandlerMapping> handlerMappings;

	private void initHandlerMappings(ApplicationContext context) {
		this.handlerMappings = null;
		//判定是否检测所有的HandlerMapping
		if (this.detectAllHandlerMappings) {
			//查看容器中所有的实现了HandlerMapping的bean
			Map<String, HandlerMapping> matchingBeans =
					BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
			if (!matchingBeans.isEmpty()) {
				this.handlerMappings = new ArrayList<>(matchingBeans.values());
				// 使用实现类的order进行优先级排序，升序
				AnnotationAwareOrderComparator.sort(this.handlerMappings);
			}
		}
		else {
			try {
        //加载缺省的HandlerMapping
				HandlerMapping hm = context.getBean(HANDLER_MAPPING_BEAN_NAME, HandlerMapping.class);
				this.handlerMappings = Collections.singletonList(hm);
			}
			catch (NoSuchBeanDefinitionException ex) {
				// Ignore, we'll add a default HandlerMapping later.
			}
		}

		// Ensure we have at least one HandlerMapping, by registering
		// a default HandlerMapping if no other mappings are found.
		if (this.handlerMappings == null) {
			this.handlerMappings = getDefaultStrategies(context, HandlerMapping.class);
			if (logger.isTraceEnabled()) {
				logger.trace("No HandlerMappings declared for servlet '" + getServletName() +
						"': using default strategies from DispatcherServlet.properties");
			}
		}
	}
```

> 上面的源码是从容器中获取HandlerMapping接口的实现类，其中包括RequestMappingHandlerMapping类实例，那这个类实例实在哪里加载的呢？请查看另一篇文章RequestMappingHandlerMapping源码分析；

##### 2.RequestMappingHandlerMapping处理reques请求

前端发送过来一个request请求首先进入DispatcherServlet的doService方法，再进入doDispatch方法，那我们看下doDispatch方法的源码：

```java
	-
```

本文的重点是通过request请求如何获取HandlerExecutionChain处理程序执行器链;HandlerExecutionChain是一个很重要的类，所有的HandlerMethod最终都要包装成HandlerExecutionChain后才可以使用；

------

跳出正文主题分析下HandlerExecutionChain类：

> Handler execution chain, consisting of handler object and any handler interceptors.
> Returned by HandlerMapping's {@link HandlerMapping#getHandler} method.

上面官方说明已经说的很清楚了，该类包含handler object(HandlerMethod)和所有的handler interceptors；而且该类的生成只能通过HandlerMapping接口的getHandler方法，具体由AbstractHandlerMapping抽象类实现；AbstractHandlerMapping类实现源码：

```java
	/**
	 * 根据request请求获取HandlerExecutionChain处理程序执行器链
	 * Look up a handler for the given request, falling back to the default
	 * handler if no specific one is found.
	 * @param request current HTTP request
	 * @return the corresponding handler instance, or the default handler
	 * @see #getHandlerInternal
	 */
	@Override
	@Nullable
	public final HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
    //获取Handler object即HandlerMethod对象
		Object handler = getHandlerInternal(request);
		if (handler == null) {
      //如果没有找到匹配的HandlerMethod对象，那么就获取默认的handler
			handler = getDefaultHandler();
		}
		if (handler == null) {
			return null;
		}
		// Bean name or resolved handler?
		if (handler instanceof String) {
			String handlerName = (String) handler;
			handler = obtainApplicationContext().getBean(handlerName);
		}
		//根据上面获取的handler object获取处理程序执行器链类HandlerExecutionChain
		HandlerExecutionChain executionChain = getHandlerExecutionChain(handler, request);

		if (logger.isTraceEnabled()) {
			logger.trace("Mapped to " + handler);
		}
		else if (logger.isDebugEnabled() && !request.getDispatcherType().equals(DispatcherType.ASYNC)) {
			logger.debug("Mapped to " + executionChain.getHandler());
		}
		//有跨域相关配置时执行如下代码
		if (hasCorsConfigurationSource(handler) || CorsUtils.isPreFlightRequest(request)) {
			CorsConfiguration config = (this.corsConfigurationSource != null ? this.corsConfigurationSource.getCorsConfiguration(request) : null);
			CorsConfiguration handlerConfig = getCorsConfiguration(handler, request);
			config = (config != null ? config.combine(handlerConfig) : handlerConfig);
			executionChain = getCorsHandlerExecutionChain(request, executionChain, config);
		}

		return executionChain;
	}
```

看下getHandlerInternal方法如何获取HandlerMethod对象：

```java
	/**
	 * 通过request请求获取对应的HandlerMethod类
	 */
	@Override
	protected HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
		//获取请求URL
    String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
		request.setAttribute(LOOKUP_PATH, lookupPath);
		this.mappingRegistry.acquireReadLock();
		try {
      //获取URL对应的HandlerMethod,重点是我们如何获取HandlerMethod对象？
			HandlerMethod handlerMethod = lookupHandlerMethod(lookupPath, request);
			return (handlerMethod != null ? handlerMethod.createWithResolvedBean() : null);
		}
		finally {
			this.mappingRegistry.releaseReadLock();
		}
	}
```

看lookupHandlerMethod源码：

```java
	/**
	 * 通过URL获取HandlerMethod对象
	 */
	@Nullable
	protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
		List<Match> matches = new ArrayList<>();
    //通过URL获取RequestMappingInfo集合
		List<T> directPathMatches = this.mappingRegistry.getMappingsByUrl(lookupPath);
		if (directPathMatches != null) {
      //将RequestMappingInfo对象和HandlerMethod对象封装到Match对象中存到matches集合
			addMatchingMappings(directPathMatches, matches, request);
		}
		if (matches.isEmpty()) {
			// No choice but to go through all mappings...
			addMatchingMappings(this.mappingRegistry.getMappings().keySet(), matches, request);
		}

		if (!matches.isEmpty()) {
      //获取集合中第一个Match对象
			Match bestMatch = matches.get(0);
			if (matches.size() > 1) {
				Comparator<Match> comparator = new MatchComparator(getMappingComparator(request));
				matches.sort(comparator);
				bestMatch = matches.get(0);
				if (logger.isTraceEnabled()) {
					logger.trace(matches.size() + " matching mappings: " + matches);
				}
				if (CorsUtils.isPreFlightRequest(request)) {
					return PREFLIGHT_AMBIGUOUS_MATCH;
				}
				Match secondBestMatch = matches.get(1);
				if (comparator.compare(bestMatch, secondBestMatch) == 0) {
					Method m1 = bestMatch.handlerMethod.getMethod();
					Method m2 = secondBestMatch.handlerMethod.getMethod();
					String uri = request.getRequestURI();
					throw new IllegalStateException(
							"Ambiguous handler methods mapped for '" + uri + "': {" + m1 + ", " + m2 + "}");
				}
			}
			request.setAttribute(BEST_MATCHING_HANDLER_ATTRIBUTE, bestMatch.handlerMethod);
			handleMatch(bestMatch.mapping, lookupPath, request);
      //返回HandlerMethod对象
			return bestMatch.handlerMethod;
		}
		else {
			return handleNoMatch(this.mappingRegistry.getMappings().keySet(), lookupPath, request);
		}
	}
```

上面的代码有一个this.mappingRegistry.getMappingsByUrl(lookupPath)方法获取url对应的RequestMappingInfo对象，源码如下：

```java
	 //URL和RequestMappingInfo集合	
	 private final MultiValueMap<String, T> urlLookup = new LinkedMultiValueMap<>();		
		/**
		 * 返回与URL匹配的RequestMappingInfo集合
		 */
		@Nullable
		public List<T> getMappingsByUrl(String urlPath) {
			return this.urlLookup.get(urlPath);
		}
```

> 上面的源码显示RequestMappingInfo对象是从urlLookup集合中获取的，那urlLookup集合中的数据又是从哪里来的呢？请看我的另外一篇文章RequestMappingHandlerMapping源码分析。

上面代码有一个addMatchingMappings方法，是通过这个方法获取到的HandlerMethod对象，看下源码：

```java
	private void addMatchingMappings(Collection<T> mappings, List<Match> matches, HttpServletRequest request) {
    //循环遍历RequestMappingInfo对象
		for (T mapping : mappings) {
      //
			T match = getMatchingMapping(mapping, request);
			if (match != null) {
        //通过this.mappingRegistry.getMappings()获取HandlerMethod对象
				matches.add(new Match(match, this.mappingRegistry.getMappings().get(mapping)));
			}
		}
	}
	//RequestMappingInfo和HandlerMethod集合
	private final Map<T, HandlerMethod> mappingLookup = new LinkedHashMap<>();
	/**
		 * Return all mappings and handler methods. Not thread-safe.
		 * @see #acquireReadLock()
		 */
		public Map<T, HandlerMethod> getMappings() {
			return this.mappingLookup;
		}
```

上面源码获取HandlerMethod对象实际上是从mappingLookUp集合之中获取的，那你可能会问mappingLookup集合中的数据又是从哪里获取到的呢？请看我的另外一篇文章RequestMappingHandlerMapping源码分析；


------

回到正文，我们看下mappedHandler = getHandler(processedRequest);方法，源码如下：

```java
	/**
	 * Return the HandlerExecutionChain for this request.
	 * <p>Tries all handler mappings in order.
	 * @param request current HTTP request
	 * @return the HandlerExecutionChain, or {@code null} if no handler could be found
	 */
	@Nullable
	protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		if (this.handlerMappings != null) {
      //循环HandlerMapping实现类集合，其中包括RequestMappingHandlerMapping
			for (HandlerMapping mapping : this.handlerMappings) {
        //调用HandlerMapping实现类的getHandler方法，并获取程序执行器链类对象
				HandlerExecutionChain handler = mapping.getHandler(request);
				if (handler != null) {
          //如果匹配直接返回，否则返回null
					return handler;
				}
			}
		}
		return null;
	}
```

通过上面我们一步步的分析源码，知道了HandlerExecutionChain处理程序执行器链是如何获取的，HandlerExecutionChain里面包含了请求对应的HandlerMethod对象和所有有关的连接器；接下来的一篇文章对HandlerAdapter接口进行分析，理解拿到HandlerExecutionChain后如何通过适配器类调用控制器方法；

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/doc/base](https://github.com/mingyang66/spring-parent/tree/master/doc/base)