### RestTemplate处理返回状态码非200及设置超时时间

默认情况下使用RestTemplate如果返回结果的状态码是200的话就正常处理，否则都会抛出异常；

#### 1.调试postForEntity请求的方法找到判断响应结果状态码的方法是org.springframework.web.client.DefaultResponseErrorHandler类中的hasError方法

```
	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		int rawStatusCode = response.getRawStatusCode();
		HttpStatus statusCode = HttpStatus.resolve(rawStatusCode);
		return (statusCode != null ? hasError(statusCode) : hasError(rawStatusCode));
	}
```

代码再往上跟踪一级，如下：

```
	protected void handleResponse(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
		ResponseErrorHandler errorHandler = getErrorHandler();
		boolean hasError = errorHandler.hasError(response);
		if (logger.isDebugEnabled()) {
			try {
				int code = response.getRawStatusCode();
				HttpStatus status = HttpStatus.resolve(code);
				logger.debug("Response " + (status != null ? status : code));
			}
			catch (IOException ex) {
				// ignore
			}
		}
		if (hasError) {
			errorHandler.handleError(url, method, response);
		}
	}
```

从上面的代码可以看到是使用了RestTemplate的错误处理器，所以我们就可以想办法自定义错误处理器;

```
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
        RestTemplate restTemplate = new RestTemplate(factory);
        ResponseErrorHandler responseErrorHandler = new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return true;
            }
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
            }
        };
        restTemplate.setErrorHandler(responseErrorHandler);
        return restTemplate;
    }zhi
```

只需要将hasError方法的返回值更改为true就可以了，以后不管状态码是200还是其它的都会返回结果；

#### 2.设置超时时间

RestTemplate默认使用的是SimpleClientHttpRequestFactory工厂方法，看下它的超时时间是：

```
	private int connectTimeout = -1;

	private int readTimeout = -1;
```

默认值都是-1，也就是没有超时时间；

其底层是使用URLConnection,而URLConnection实际上时封装了Socket，Socket我们知道是没有超时时间限制的，所以我们必须设置超时时间，否则如果请求的URL一直卡死程序将会不可以运行下去；

```
    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        //读取超时5秒,默认无限限制,单位：毫秒
        factory.setReadTimeout(5000);
        //连接超时10秒，默认无限制，单位：毫秒
        factory.setConnectTimeout(10000);
        return factory;
    }
```

GitHub源码：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rest-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rest-service)
