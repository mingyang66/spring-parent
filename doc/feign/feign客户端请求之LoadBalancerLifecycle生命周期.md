### feign客户端请求之LoadBalancerLifecycle生命周期

> LoadBalancerLifecycle接口允许在load-balancing执行前后应执行的操作，我们可以借此入口获取微服务请求的真实请求URL;

##### 一、LoadBalancerLifecycle接口源码

```java
public interface LoadBalancerLifecycle<RC, RES, T> {

	/**
	 * 判定服务实例对象是否允许执行方法回调
	 */
	default boolean supports(Class requestContextClass, Class responseClass, Class serverTypeClass) {
		return true;
	}

	/**
	 * 在执行load-balancing之前（即获取真实服务实例url之前）的回调方法
	 */
	void onStart(Request<RC> request);

	/**
	 * 已经选择一个真实的服务实例之后，在执行请求之前的回调方法
	 */
	void onStartRequest(Request<RC> request, Response<T> lbResponse);

	/**
	 * 在load-balancing负载均衡真实服务请求之后执行
	 */
	void onComplete(CompletionContext<RES, T, RC> completionContext);

}
```

##### 二、基于loadbalance请求服务有两个FeignBlockingLoadBalancerClient（不支持重试）、RetryableFeignBlockingLoadBalancerClient（支持重试）

我们先看下FeignBlockingLoadBalancerClient的源码：

```java

public class FeignBlockingLoadBalancerClient implements Client {

	...

	@Override
	public Response execute(Request request, Request.Options options) throws IOException {
    //获取请求URL
		final URI originalUri = URI.create(request.url());
    //获取微服务实例ID
		String serviceId = originalUri.getHost();
		Assert.state(serviceId != null, "Request URI does not contain a valid hostname: " + originalUri);
    //获取配置命中值
		String hint = getHint(serviceId);
    //获取请求上线文对象
		DefaultRequest<RequestDataContext> lbRequest = new DefaultRequest<>(
				new RequestDataContext(buildRequestData(request), hint));
    //获取符合当前请求实例的生命周期管理对象
		Set<LoadBalancerLifecycle> supportedLifecycleProcessors = LoadBalancerLifecycleValidator
				.getSupportedLifecycleProcessors(
						loadBalancerClientFactory.getInstances(serviceId, LoadBalancerLifecycle.class),
						RequestDataContext.class, ResponseData.class, ServiceInstance.class);
    //调用生命 周期onStart回调方法
		supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onStart(lbRequest));
    //获取请求实例对应的微服务实例对象
		ServiceInstance instance = loadBalancerClient.choose(serviceId, lbRequest);
    //请求响应对象
		org.springframework.cloud.client.loadbalancer.Response<ServiceInstance> lbResponse = new DefaultResponse(
				instance);
    //如果请求实例不存在，则直接调用生命周期完成方法onComplete
		if (instance == null) {
			String message = "Load balancer does not contain an instance for the service " + serviceId;
			if (LOG.isWarnEnabled()) {
				LOG.warn(message);
			}
			supportedLifecycleProcessors.forEach(lifecycle -> lifecycle
					.onComplete(new CompletionContext<ResponseData, ServiceInstance, RequestDataContext>(
							CompletionContext.Status.DISCARD, lbRequest, lbResponse)));
			return Response.builder().request(request).status(HttpStatus.SERVICE_UNAVAILABLE.value())
					.body(message, StandardCharsets.UTF_8).build();
		}
    //获取实例请求的真实请求对象
		String reconstructedUrl = loadBalancerClient.reconstructURI(instance, originalUri).toString();
    //构建Request请求对象
		Request newRequest = buildRequest(request, reconstructedUrl);
    //执行真实请求
		return executeWithLoadBalancerLifecycleProcessing(delegate, options, newRequest, lbRequest, lbResponse,
				supportedLifecycleProcessors);
	}

	protected Request buildRequest(Request request, String reconstructedUrl) {
		return Request.create(request.httpMethod(), reconstructedUrl, request.headers(), request.body(),
				request.charset(), request.requestTemplate());
	}

	// Visible for Sleuth instrumentation
	public Client getDelegate() {
		return delegate;
	}
	// 获取配置命中值
	private String getHint(String serviceId) {
		String defaultHint = properties.getHint().getOrDefault("default", "default");
		String hintPropertyValue = properties.getHint().get(serviceId);
		return hintPropertyValue != null ? hintPropertyValue : defaultHint;
	}

}

```

executeWithLoadBalancerLifecycleProcessing请求方法：

```java
	static Response executeWithLoadBalancerLifecycleProcessing(Client feignClient, Request.Options options,
			Request feignRequest, org.springframework.cloud.client.loadbalancer.Request lbRequest,
			org.springframework.cloud.client.loadbalancer.Response<ServiceInstance> lbResponse,
			Set<LoadBalancerLifecycle> supportedLifecycleProcessors, boolean loadBalanced) throws IOException {
    //在获取真实请求URL之后，在发送真实请求之前执行onStartRequest回调方法
		supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onStartRequest(lbRequest, lbResponse));
		try {
      //执行真实请求
			Response response = feignClient.execute(feignRequest, options);
			if (loadBalanced) {
        //执行声明周期回调方法onComplete
				supportedLifecycleProcessors.forEach(
						lifecycle -> lifecycle.onComplete(new CompletionContext<>(CompletionContext.Status.SUCCESS,
								lbRequest, lbResponse, buildResponseData(response))));
			}
			return response;
		}
		catch (Exception exception) {
			if (loadBalanced) {
        //执行声明周期回调方法onComplete
				supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onComplete(
						new CompletionContext<>(CompletionContext.Status.FAILED, exception, lbRequest, lbResponse)));
			}
			throw exception;
		}
	}
```

如果开启spring.cloud.loadbalancer.retry.enabled=true重试能力，则执行的是RetryableFeignBlockingLoadBalancerClient，其执行流程跟上述一致，不在做详细分析；

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)