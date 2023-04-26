### 解锁新技能RestTemplate设置全局、单个请求超时时间及支持https请求

>
springboot请求第三方接口时会用到RestTemplate，其底层实现逻辑默认是通过SimpleClientHttpRequestFactory来实现，具体由socket连接来实现；可以替换其默认实现为HttpComponentsClientHttpRequestFactory。

##### 一、自定义RestTemplate实例对象

```java
    @Primary
    @Bean
    public RestTemplate restTemplate(ObjectProvider<HttpClientCustomizer> httpClientCustomizers, ClientHttpRequestFactory clientHttpRequestFactory, HttpClientProperties httpClientProperties) {
        RestTemplate restTemplate = new RestTemplate();
        //设置BufferingClientHttpRequestFactory将输入流和输出流保存到内存中，允许多次读取
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(clientHttpRequestFactory));
        //设置自定义异常处理
        restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        if (httpClientProperties.isInterceptor()) {
            //添加拦截器
          restTemplate.setInterceptors(Collections.singletonList(httpClientCustomizers.orderedStream().findFirst().get()));
        }

        return restTemplate;
    }
```

##### 二、RestTemplate自定义全局超时时间

```
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(HttpClientProperties properties) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        //SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        //读取超时5秒,默认无限限制,单位：毫秒
        factory.setReadTimeout(properties.getReadTimeOut());
        //连接超时10秒，默认无限制，单位：毫秒
        factory.setConnectTimeout(properties.getConnectTimeOut());
        return factory;
    }
```

##### 三、RestTemplate设置单个请求的超时时间

首先看下HttpComponentsClientHttpRequestFactory类的createRequest方法源码：

```java
	@Override
	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
		HttpClient client = getHttpClient();

		HttpUriRequest httpRequest = createHttpUriRequest(httpMethod, uri);
		postProcessHttpRequest(httpRequest);
		HttpContext context = createHttpContext(httpMethod, uri);
		if (context == null) {
			context = HttpClientContext.create();
		}

		// Request configuration not set in the context
		if (context.getAttribute(HttpClientContext.REQUEST_CONFIG) == null) {
			// Use request configuration given by the user, when available
			RequestConfig config = null;
			if (httpRequest instanceof Configurable) {
				config = ((Configurable) httpRequest).getConfig();
			}
			if (config == null) {
				config = createRequestConfig(client);
			}
			if (config != null) {
				context.setAttribute(HttpClientContext.REQUEST_CONFIG, config);
			}
		}

		if (this.bufferRequestBody) {
			return new HttpComponentsClientHttpRequest(client, httpRequest, context);
		}
		else {
			return new HttpComponentsStreamingClientHttpRequest(client, httpRequest, context);
		}
	}
```

>
其中createHttpContext方法默认返回的是null，因此HttpContext中的RequestConfig配置值为null，所以需要按照接下来的代码生成并设置；RequestConfig配置类中的socketTimeout是设置读取超时时间，connectTimeout是设置连接超时时间的两个属性，明白了这些就应该知道怎样设置单个请求超时时间了；

定义一个HttpContextFactory类，即HttpContext类的一个实现：

```java
public class HttpContextFactory implements BiFunction<HttpMethod, URI, HttpContext> {
    @Override
    public HttpContext apply(HttpMethod httpMethod, URI uri) {
        RequestConfig requestConfig = HttpContextHolder.peek();
        if (Objects.nonNull(requestConfig)) {
            HttpContext context = HttpClientContext.create();
            context.setAttribute(HttpClientContext.REQUEST_CONFIG, requestConfig);
            return context;
        }
        return null;
    }
}
```

定义一个持有RequestConfig线程上下文对象类：

```java
public class HttpContextHolder {

    private static final ThreadLocal<RequestConfig> threadLocal = new NamedThreadLocal<>("HTTP进程执行状态上下文");

    public static void bind(RequestConfig requestConfig) {
        threadLocal.set(requestConfig);
    }

    public static RequestConfig peek() {
        return threadLocal.get();
    }

    public static void unbind() {
        threadLocal.remove();
    }
}
```

设置将HttpContextFactory类实例对象：

```java
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(HttpClientProperties properties) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        //SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        //读取超时5秒,默认无限限制,单位：毫秒
        factory.setReadTimeout(properties.getReadTimeOut());
        //连接超时10秒，默认无限制，单位：毫秒
        factory.setConnectTimeout(properties.getConnectTimeOut());
        //设置HTTP进程执行状态工厂类
        factory.setHttpContextFactory(new HttpContextFactory());
        return factory;
    }
```

调用示例：

```java
@RequestMapping("api/http")
@RestController
public class HttpClientController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("get1")
    public BaseResponse get1(HttpServletRequest request) {
        String timeout = request.getParameter("timeout");
        BaseResponse<String> result;
        try {
            HttpContextHolder.bind(RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(-1).build());
            result = restTemplate.getForObject("https://127.0.0.1:8080/api/http/testResponse?timeout=" + timeout, BaseResponse.class);
        } finally {
            HttpContextHolder.unbind();
        }
        return result;
    }
    }
```

这样设置有点麻烦，可以定义一个注解，以AOP切面的方式来使用：

定义注解@TargetHttpTimeout：

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TargetHttpTimeout {
    /**
     * 读取超时时间，默认：-1
     */
    int readTimeout() default -1;

    /**
     * 连接超时时间，默认：-1
     */
    int connectTimeout() default -1;
}
```

定义拦截器HttpTimeoutMethodInterceptor：

```java
public class HttpTimeoutMethodInterceptor implements MethodInterceptor {
    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        try {
            Method method = invocation.getMethod();
            if (method.isAnnotationPresent(TargetHttpTimeout.class)) {
                TargetHttpTimeout targetHttpTimeout = method.getAnnotation(TargetHttpTimeout.class);
                RequestConfig requestConfig = RequestConfig.custom()
                        .setSocketTimeout(targetHttpTimeout.readTimeout())
                        .setConnectTimeout(targetHttpTimeout.connectTimeout())
                        .build();
                HttpContextHolder.bind(requestConfig);
            }
            return invocation.proceed();
        } finally {
            HttpContextHolder.unbind();
        }
    }
}
```

将AOP切面及切点关联起来并注入容器：

```java
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor httpTimeoutPointCutAdvice() {
        //限定方法级别的切点
        Pointcut mpc = new AnnotationMatchingPointcut(null, TargetHttpTimeout.class, false);
        //组合切面(并集)，即只要有一个切点的条件符合，则就拦截
        Pointcut pointcut = new ComposablePointcut(mpc);
        //切面增强类
        AnnotationPointcutAdvisor advisor = new AnnotationPointcutAdvisor(new HttpTimeoutMethodInterceptor(), pointcut);
        //切面优先级顺序
        advisor.setOrder(AopOrderInfo.HTTP_CLIENT_INTERCEPTOR);
        return advisor;
    }
```

通过上述几步的优化就可以优雅的按照注解的方式设置单个请求的超时时间：

```java
    @GetMapping("get2")
    @TargetHttpTimeout(readTimeout = 2000, connectTimeout = -1)
    public BaseResponse get2(HttpServletRequest request) {
        String timeout = request.getParameter("timeout");
        BaseResponse<String> result = restTemplate.getForObject("https://127.0.0.1:8080/api/http/testResponse?timeout=1000", BaseResponse.class);

        return result;
    }
```

##### 四、RestTemplate支持https请求

```java
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(HttpClientProperties properties) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        //SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        //读取超时5秒,默认无限限制,单位：毫秒
        factory.setReadTimeout(properties.getReadTimeOut());
        //连接超时10秒，默认无限制，单位：毫秒
        factory.setConnectTimeout(properties.getConnectTimeOut());
        //设置HTTP进程执行状态工厂类
        factory.setHttpContextFactory(new HttpContextFactory());
        //开启HTTPS请求支持
        if (properties.isSsl()) {
            TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(connectionSocketFactory).build();
            factory.setHttpClient(httpClient);
        }
        return factory;
    }
```

> 通过上述的步骤完整的实现了http全局超时时间的设置，单个请求超时时间设置，https请求支持；

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)