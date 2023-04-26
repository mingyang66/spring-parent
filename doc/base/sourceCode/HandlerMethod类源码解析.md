### HandlerMethod类源码解析

> HandlerMethod类用于封装控制器方法信息，包含类信息、方法Method对象、参数、注解等信息，具体的接口请求是可以根据封装的信息调用具体的方法来执行业务逻辑；

HandlerMethod有三个子类分别是InvocableHandlerMethod、ServletInvocableHandlerMethod、ConcurrentResultHandlerMethod，类的关系图如下：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200605141632789.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhb21pbmd5YW5n,size_16,color_FFFFFF,t_70)

##### 1.HandlerMethod类源码详解

```java
public class HandlerMethod {
	//bean名称，调试的时候看到是字符串控制器名称（首字母小写）
	private final Object bean;
  //bean工厂类，个人调试传入的是DefaultListableBeanFactory
	@Nullable
	private final BeanFactory beanFactory;
	//方法所属类
	private final Class<?> beanType;
	//控制器方法
	private final Method method;
	//桥接方法，如果method是原生的，这个属性就是method
	private final Method bridgedMethod;
	//封装方法参数实例
	private final MethodParameter[] parameters;
	//Http状态码
	@Nullable
	private HttpStatus responseStatus;
	//ResponseStatus注解的reason值
	@Nullable
	private String responseStatusReason;
	//使用createWithResolvedBean方法创建的HttpMethod方法对象
	@Nullable
	private HandlerMethod resolvedFromHandlerMethod;
	//getInterfaceParameterAnnotations获取
	@Nullable
	private volatile List<Annotation[][]> interfaceParameterAnnotations;
	//类描述，使用initDescription方法解析beanType和method获得
	private final String description;
}
```

##### 2.InvocableHandlerMethod类详解

>
InvocableHandlerMethod类是HandlerMethod的直接子类，该类中新增了对请求参数解析的参数解析程序，request请求时的回调方法invokeForRequest和doInvoke(
建议阅读HandlerAdapter源码解析)

request请求会在RequestMappingHandlerAdapter类中的handleInternal方法进行回调，回调方法的源码如下：

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

doInvoke方法源码：

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

##### 3.ServletInvocableHandlerMethod类详解

> ServletInvocableHandlerMethod类继承了InvocableHandlerMethod类，新增了处理返回值HandlerMethodReturnValueHandler的能力，并且新增了调用控制器方法的回调方法；

回调invokeAndHandle方法源码如下：

```java
	/**
	 * Invoke the method and handle the return value through one of the
	 * configured {@link HandlerMethodReturnValueHandler HandlerMethodReturnValueHandlers}.
	 * @param webRequest the current request
	 * @param mavContainer the ModelAndViewContainer for this request
	 * @param providedArgs "given" arguments matched by type (not resolved)
	 */
	public void invokeAndHandle(ServletWebRequest webRequest, ModelAndViewContainer mavContainer,
			Object... providedArgs) throws Exception {
		//调用父类InvocableHandlerMethod的回调方法，并返回调用接口控制器方法的返回结果
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
      //将控制器返回的结果交给HandlerMethodReturnValueHandler来处理
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

##### 4.ConcurrentResultHandlerMethod类详解

> ConcurrentResultHandlerMethod是ServletInvocableHandlerMethod的一个内部类，也是它的子类，支持异常调用结果处理（暂时没有发现使用的场景）

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/doc/base](https://github.com/mingyang66/spring-parent/tree/master/doc/base)