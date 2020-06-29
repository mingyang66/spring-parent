### HandlerAdapter接口源码解析

> HandlerAdapter是一个适配器接口类，适配器模式是指两个不兼容接口之间的桥梁，要想让一个接口使用另外一个接口的实现中间可以加一层适配器类；举个例子：笔记本没有网线接口，那我想连接网线接通网络如何实现呢？我可以买一个以太网转换器，网线插入转换器上，转换器插入笔记本上就实现了连接网络的功能，那这个转换器起到的就是一个适配器的作用；在接口访问控制器方法的时候是通过HandlerAdapter接口的实现类来进行的，本文以最常用的RequestMappingHandlerAdapter适配器类为主线索来讲解。

接口的源码如下：

```java
public interface HandlerAdapter {

	/**
	 * 判定是否支持传入的handler
	 */
	boolean supports(Object handler);

	/**
	 * 使用给定的handler来处理当前request请求
	 */
	@Nullable
	ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;

	/**
	 * 返回handler最后修改的时间
	 */
	long getLastModified(HttpServletRequest request, Object handler);

}
```

HandlerAdapter的实现类有多个，我们以RequestMappingHandlerAdapter适配器类为主线索，而此类又是AbstractHandlerMethodAdapter抽象类的子类；

如下是RequestMappingHandlerAdapter适配器类的关系图

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200605101006749.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhb21pbmd5YW5n,size_16,color_FFFFFF,t_70)



##### 1.HandlerAdapter接口的初始化

容器加载时DispatcherServlet中的初始化方法initStrategies方法会被调用，里面的

```java
  /**
  * 容器中HandlerAdapter接口实现类集合
  **/
  @Nullable
	private List<HandlerAdapter> handlerAdapters;	
	/**
	 * 初始化handlerAdapters集合，如果在BeanFactory中没有HandlerAdapter，那么将使用默认
	 * 的SimpleControllerHandlerAdapter
	 */
	private void initHandlerAdapters(ApplicationContext context) {
		this.handlerAdapters = null;
		//判定是否检测所有的HandlerAdapter
		if (this.detectAllHandlerAdapters) {
			//查看容器中所有实现了HandlerAdapter的bean 
			Map<String, HandlerAdapter> matchingBeans =
					BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerAdapter.class, true, false);
			if (!matchingBeans.isEmpty()) {
				this.handlerAdapters = new ArrayList<>(matchingBeans.values());
				// We keep HandlerAdapters in sorted order.
				AnnotationAwareOrderComparator.sort(this.handlerAdapters);
			}
		}
		else {
			try {
				HandlerAdapter ha = context.getBean(HANDLER_ADAPTER_BEAN_NAME, HandlerAdapter.class);
				this.handlerAdapters = Collections.singletonList(ha);
			}
			catch (NoSuchBeanDefinitionException ex) {
				// Ignore, we'll add a default HandlerAdapter later.
			}
		}

		// Ensure we have at least some HandlerAdapters, by registering
		// default HandlerAdapters if no other adapters are found.
		if (this.handlerAdapters == null) {
			this.handlerAdapters = getDefaultStrategies(context, HandlerAdapter.class);
			if (logger.isTraceEnabled()) {
				logger.trace("No HandlerAdapters declared for servlet '" + getServletName() +
						"': using default strategies from DispatcherServlet.properties");
			}
		}
	}
```

> 上面的代码是从容器中获取HandlerAdapter接口实现类的bean集合

##### 2.RequestMappingHandlerAdapter处理request请求

前端发送过来一个request请求首先进入DispatcherServlet的doService方法，再进入doDispatch方法，那我们看下doDispatch方法的源码：

```java
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpServletRequest processedRequest = request;
		HandlerExecutionChain mappedHandler = null;
		boolean multipartRequestParsed = false;

		WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

		try {
			ModelAndView mv = null;
			Exception dispatchException = null;

			try {
				processedRequest = checkMultipart(request);
				multipartRequestParsed = (processedRequest != request);
				//通过request请求获取HandlerExecutionChain处理器执行程序链
        //也即通过request获取匹配的HandlerMapping实现类，通过实现类的getHandler
        //方法获取HandlerExecutionChain处理程序执行器链类，里面包含了请求对应的HandlerMethod
        //及对应的所有拦截器
				// Determine handler for the current request.
				mappedHandler = getHandler(processedRequest);
				if (mappedHandler == null) {
					noHandlerFound(processedRequest, response);
					return;
				}
				//根据上面获取到的HandlerMethod获取匹配到的适配器类
				// Determine handler adapter for the current request.
				HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

				// Process last-modified header, if supported by the handler.
				String method = request.getMethod();
				boolean isGet = "GET".equals(method);
				if (isGet || "HEAD".equals(method)) {
					long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
					if (new ServletWebRequest(request, response).checkNotModified(lastModified) && isGet) {
						return;
					}
				}

				if (!mappedHandler.applyPreHandle(processedRequest, response)) {
					return;
				}
				//根据适配器对象调用具体的方法（如：RequestMappingHandlerAdapter）
				// Actually invoke the handler.
				mv = ha.handle(processedRequest, response, mappedHandler.getHandler());

				if (asyncManager.isConcurrentHandlingStarted()) {
					return;
				}

				applyDefaultViewName(processedRequest, mv);
				mappedHandler.applyPostHandle(processedRequest, response, mv);
			}
			catch (Exception ex) {
				dispatchException = ex;
			}
			catch (Throwable err) {
				// As of 4.3, we're processing Errors thrown from handler methods as well,
				// making them available for @ExceptionHandler methods and other scenarios.
				dispatchException = new NestedServletException("Handler dispatch failed", err);
			}
			processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
		}
		catch (Exception ex) {
			triggerAfterCompletion(processedRequest, response, mappedHandler, ex);
		}
		catch (Throwable err) {
			triggerAfterCompletion(processedRequest, response, mappedHandler,
					new NestedServletException("Handler processing failed", err));
		}
		finally {
			if (asyncManager.isConcurrentHandlingStarted()) {
				// Instead of postHandle and afterCompletion
				if (mappedHandler != null) {
					mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
				}
			}
			else {
				// Clean up any resources used by a multipart request.
				if (multipartRequestParsed) {
					cleanupMultipart(processedRequest);
				}
			}
		}
	}
```

------

跳出本文分析的正文流程，来分析下AbstractHandlerMethodAdapter适配器类；

> ```
> Abstract base class for {@link HandlerAdapter} implementations that support
> handlers of type {@link HandlerMethod}.
> ```

源码注解说明类抽象类的实现support方法判定是否支持HandlerMethod;

```java
	/**
	* 判定适配器类是否支持HandlerMethod
	**/
	@Override
	public final boolean supports(Object handler) {
		return (handler instanceof HandlerMethod && supportsInternal((HandlerMethod) handler));
	}
```



------

getHandlerAdapter方法实现源码：

```java
	/**
	 * 获取可以处理handler的HandlerAdapter适配器类
	 */
	protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
		if (this.handlerAdapters != null) {
			for (HandlerAdapter adapter : this.handlerAdapters) {
        //判定适配器类是否支持HandlerMethod
				if (adapter.supports(handler)) {
					return adapter;
				}
			}
		}
		throw new ServletException("No adapter for handler [" + handler +
				"]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
	}
```

ha.handle(processedRequest, response, mappedHandler.getHandler());方法会调用AbstractHandlerMethodAdapter适配器类的handle方法：

```java
	@Override
	@Nullable
	public final ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		return handleInternal(request, response, (HandlerMethod) handler);
	}
```

而handleInternal方法最终会调用RequestMappingHandlerAdapter类的handlerInternal方法：

```java
@Override
	protected ModelAndView handleInternal(HttpServletRequest request,
			HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {

		ModelAndView mav;
		checkRequest(request);

		// Execute invokeHandlerMethod in synchronized block if required.
		if (this.synchronizeOnSession) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				Object mutex = WebUtils.getSessionMutex(session);
				synchronized (mutex) {
					mav = invokeHandlerMethod(request, response, handlerMethod);
				}
			}
			else {
				// No HttpSession available -> no mutex necessary
				mav = invokeHandlerMethod(request, response, handlerMethod);
			}
		}
		else {
			// No synchronization on session demanded at all...
			mav = invokeHandlerMethod(request, response, handlerMethod);
		}

		if (!response.containsHeader(HEADER_CACHE_CONTROL)) {
			if (getSessionAttributesHandler(handlerMethod).hasSessionAttributes()) {
				applyCacheSeconds(response, this.cacheSecondsForSessionAttributeHandlers);
			}
			else {
				prepareResponse(response);
			}
		}

		return mav;
	}
```

handleInternal方法又会调用invokeHandlerMethod方法：

```java
	@Nullable
	protected ModelAndView invokeHandlerMethod(HttpServletRequest request,
			HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {

		ServletWebRequest webRequest = new ServletWebRequest(request, response);
		try {
			WebDataBinderFactory binderFactory = getDataBinderFactory(handlerMethod);
			ModelFactory modelFactory = getModelFactory(handlerMethod, binderFactory);
			//创建HandlerMethod的子类ServletInvocableHandlerMethod的实例对象
			ServletInvocableHandlerMethod invocableMethod = createInvocableHandlerMethod(handlerMethod);
			if (this.argumentResolvers != null) {
        //设置参数解析器
				invocableMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
			}
			if (this.returnValueHandlers != null) {
        //设置返回值处理程序
				invocableMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);
			}
			invocableMethod.setDataBinderFactory(binderFactory);
			invocableMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);

			ModelAndViewContainer mavContainer = new ModelAndViewContainer();
			mavContainer.addAllAttributes(RequestContextUtils.getInputFlashMap(request));
			modelFactory.initModel(webRequest, mavContainer, invocableMethod);
			mavContainer.setIgnoreDefaultModelOnRedirect(this.ignoreDefaultModelOnRedirect);

			AsyncWebRequest asyncWebRequest = WebAsyncUtils.createAsyncWebRequest(request, response);
			asyncWebRequest.setTimeout(this.asyncRequestTimeout);

			WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
			asyncManager.setTaskExecutor(this.taskExecutor);
			asyncManager.setAsyncWebRequest(asyncWebRequest);
			asyncManager.registerCallableInterceptors(this.callableInterceptors);
			asyncManager.registerDeferredResultInterceptors(this.deferredResultInterceptors);

			if (asyncManager.hasConcurrentResult()) {
				Object result = asyncManager.getConcurrentResult();
				mavContainer = (ModelAndViewContainer) asyncManager.getConcurrentResultContext()[0];
				asyncManager.clearConcurrentResult();
				LogFormatUtils.traceDebug(logger, traceOn -> {
					String formatted = LogFormatUtils.formatValue(result, !traceOn);
					return "Resume with async result [" + formatted + "]";
				});
				invocableMethod = invocableMethod.wrapConcurrentResult(result);
			}
			//调用ServletInvocableHandlerMethod类的invokeAndHandle方法
			invocableMethod.invokeAndHandle(webRequest, mavContainer);
			if (asyncManager.isConcurrentHandlingStarted()) {
				return null;
			}

			return getModelAndView(mavContainer, modelFactory, webRequest);
		}
		finally {
			webRequest.requestCompleted();
		}
	}
```

> 上面的方法会涉及到参数解析器的初始化、返回值处理程序的初始化，那这些返回值处理程序和参数解析程序如何初始化的呢？RequestMappingHandlerAdapter类实现了InitializingBean接口，该类加载后会调用afterPropertiesSet方法，会对这些处理程序进行初始化；
>
> ```java
> 	@Override
> 	public void afterPropertiesSet() {
> 		// Do this first, it may add ResponseBody advice beans
> 		initControllerAdviceCache();
> 		//参数解析器初始化
> 		if (this.argumentResolvers == null) {
> 			List<HandlerMethodArgumentResolver> resolvers = getDefaultArgumentResolvers();
> 			this.argumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
> 		}
>     //InitBinder的参数解析器初始化
> 		if (this.initBinderArgumentResolvers == null) {
> 			List<HandlerMethodArgumentResolver> resolvers = getDefaultInitBinderArgumentResolvers();
> 			this.initBinderArgumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
> 		}
>     //返回值处理程序初始化
> 		if (this.returnValueHandlers == null) {
> 			List<HandlerMethodReturnValueHandler> handlers = getDefaultReturnValueHandlers();
> 			this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite().addHandlers(handlers);
> 		}
> 	}
> ```
>
> 

调用ServletInvocableHandlerMethod.invokeAndHandle方法源码如下：

```java
	public void invokeAndHandle(ServletWebRequest webRequest, ModelAndViewContainer mavContainer,
			Object... providedArgs) throws Exception {
		//调用控制器方法，获取返回值
		Object returnValue = invokeForRequest(webRequest, mavContainer, providedArgs);
		setResponseStatus(webRequest);

		if (returnValue == null) {
			if (isRequestNotModified(webRequest) || getResponseStatus() != null || mavContainer.isRequestHandled()) {
				disableContentCachingIfNecessary(webRequest);
				mavContainer.setRequestHandled(true);
				return;
			}
		}
		else if (StringUtils.hasText(getResponseStatusReason())) {
			mavContainer.setRequestHandled(true);
			return;
		}

		mavContainer.setRequestHandled(false);
		Assert.state(this.returnValueHandlers != null, "No return value handlers");
		try {
      //将返回值交给HandlerMethodReturnValueHandlerComposite返回值代理类
      //选择合适的HandlerMethodReturnValueHandler实现类处理返回值
			this.returnValueHandlers.handleReturnValue(
					returnValue, getReturnValueType(returnValue), mavContainer, webRequest);
		}
		catch (Exception ex) {
			if (logger.isTraceEnabled()) {
				logger.trace(formatErrorForReturnValue(returnValue), ex);
			}
			throw ex;
		}
	}
```

调用InvocableHandlerMethod.invokeForRequest方法源码：

```java
	/**
	* 获取request请求参数，调用控制器方法
	**/
  @Nullable
	public Object invokeForRequest(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer,
			Object... providedArgs) throws Exception {
		//获取request请求方法的参数
		Object[] args = getMethodArgumentValues(request, mavContainer, providedArgs);
		if (logger.isTraceEnabled()) {
			logger.trace("Arguments: " + Arrays.toString(args));
		}
		return doInvoke(args);
	}
```

调用InvocableHandlerMethod.doInvoke(Object... args)方法源码：

```java
	/**
	* 使用给定的参数调用控制器方法
	**/
	@Nullable
	protected Object doInvoke(Object... args) throws Exception {
		ReflectionUtils.makeAccessible(getBridgedMethod());
		try {
      //调用真实最终的控制器方法，并返回执行后的结果
			return getBridgedMethod().invoke(getBean(), args);
		}
		catch (IllegalArgumentException ex) {
			assertTargetBean(getBridgedMethod(), getBean(), args);
			String text = (ex.getMessage() != null ? ex.getMessage() : "Illegal argument");
			throw new IllegalStateException(formatInvokeError(text, args), ex);
		}
		catch (InvocationTargetException ex) {
			// Unwrap for HandlerExceptionResolvers ...
			Throwable targetException = ex.getTargetException();
			if (targetException instanceof RuntimeException) {
				throw (RuntimeException) targetException;
			}
			else if (targetException instanceof Error) {
				throw (Error) targetException;
			}
			else if (targetException instanceof Exception) {
				throw (Exception) targetException;
			}
			else {
				throw new IllegalStateException(formatInvokeError("Invocation failure", args), targetException);
			}
		}
	}
```


GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/doc/base](https://github.com/mingyang66/spring-parent/tree/master/doc/base)
