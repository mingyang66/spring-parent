### FeignClient如何确定通过ApacheHttpClient、FeignBlockingLoadBalancerClient、RetryableFeignBlockingLoadBalancerClient发送请求

> springcloud使用feign时对feignclient的初始化核心在FeignClientsRegistrar类中，FeignClientsRegistrar类中会对FeignClientFactoryBean类进行初始化；

##### 一、先看下FeignClientsRegistrar#registerFeignClient方法

```java
	private void registerFeignClient(BeanDefinitionRegistry registry, AnnotationMetadata annotationMetadata,
			Map<String, Object> attributes) {
		String className = annotationMetadata.getClassName();
		Class clazz = ClassUtils.resolveClassName(className, null);
		ConfigurableBeanFactory beanFactory = registry instanceof ConfigurableBeanFactory
				? (ConfigurableBeanFactory) registry : null;
		String contextId = getContextId(beanFactory, attributes);
		String name = getName(attributes);
    //实例化FeignClientFactoryBean工厂
		FeignClientFactoryBean factoryBean = new FeignClientFactoryBean();
		factoryBean.setBeanFactory(beanFactory);
		factoryBean.setName(name);
		factoryBean.setContextId(contextId);
		factoryBean.setType(clazz);
    //实例化BeanDefinition构建器，在注入容器时会回调lambda表达式
		BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(clazz, () -> {
      //设置@FeignClient注解设置的url
			factoryBean.setUrl(getUrl(beanFactory, attributes));
      //设置@FeignClient注解上path属性
			factoryBean.setPath(getPath(beanFactory, attributes));
			factoryBean.setDecode404(Boolean.parseBoolean(String.valueOf(attributes.get("decode404"))));
			Object fallback = attributes.get("fallback");
			if (fallback != null) {
				factoryBean.setFallback(fallback instanceof Class ? (Class<?>) fallback
						: ClassUtils.resolveClassName(fallback.toString(), null));
			}
			Object fallbackFactory = attributes.get("fallbackFactory");
			if (fallbackFactory != null) {
				factoryBean.setFallbackFactory(fallbackFactory instanceof Class ? (Class<?>) fallbackFactory
						: ClassUtils.resolveClassName(fallbackFactory.toString(), null));
			}
      //调用工厂的getObject方法
			return factoryBean.getObject();
		});
		definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
		definition.setLazyInit(true);
		validate(attributes);

		AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
		beanDefinition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, className);
		beanDefinition.setAttribute("feignClientsRegistrarFactoryBean", factoryBean);

		// has a default, won't be null
		boolean primary = (Boolean) attributes.get("primary");

		beanDefinition.setPrimary(primary);

		String[] qualifiers = getQualifiers(attributes);
		if (ObjectUtils.isEmpty(qualifiers)) {
			qualifiers = new String[] { contextId + "FeignClient" };
		}

		BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, qualifiers);
    //将FeignClientFactoryBean实例注入容器之中
		BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
	}
```

上述代码在将FeignClientFactoryBean实例注入容器时后会调用FeignClientFactoryBean类的getObject方法；

##### 二、org.springframework.cloud.openfeign.FeignClientFactoryBean#getObject方法

```java
	@Override
	public Object getObject() {
		return getTarget();
	}
```

##### 三、org.springframework.cloud.openfeign.FeignClientFactoryBean#getTarget方法是确定请求调用哪个client的核心

```java
	<T> T getTarget() {
		FeignContext context = beanFactory != null ? beanFactory.getBean(FeignContext.class)
				: applicationContext.getBean(FeignContext.class);
		Feign.Builder builder = feign(context);
		//微服务@FeignClient的url为null,则会执行如下代码
		if (!StringUtils.hasText(url)) {
			if (!name.startsWith("http")) {
				url = "http://" + name;
			}
			else {
				url = name;
			}
			url += cleanPath();
      // 此处调用feign.Feign.Builder#target(feign.Target<T>)
      //构建ReflectiveFeign对象
      //构建AOP切面请求代理
			return (T) loadBalance(builder, context, new HardCodedTarget<>(type, name, url));
		}
    //非微服务@FeignClient的url为非空，则执行如下代码
		if (StringUtils.hasText(url) && !url.startsWith("http")) {
			url = "http://" + url;
		}
		String url = this.url + cleanPath();
    //获取上下文中的Client对象
		Client client = getOptional(context, Client.class);
		if (client != null) {
			if (client instanceof FeignBlockingLoadBalancerClient) {
				// 非load balancing，因为存在url
				// 获取FeignBlockingLoadBalancerClient的代理，即AppacheHttpClient
				client = ((FeignBlockingLoadBalancerClient) client).getDelegate();
			}
			if (client instanceof RetryableFeignBlockingLoadBalancerClient) {
				// 非load balancing，因为存在url
				// 获取FeignBlockingLoadBalancerClient的代理，即AppacheHttpClient
				client = ((RetryableFeignBlockingLoadBalancerClient) client).getDelegate();
			}
      //初始化构造器为AppacheHttpClient
			builder.client(client);
		}
		Targeter targeter = get(context, Targeter.class);
      // 此处调用feign.Feign.Builder#target(feign.Target<T>)
      //构建ReflectiveFeign对象
      //构建AOP切面请求代理
		return (T) targeter.target(this, builder, context, new HardCodedTarget<>(type, name, url));
	}
```

org.springframework.cloud.openfeign.FeignClientFactoryBean#loadBalance方法获取loadBalance客户端

```java
	protected <T> T loadBalance(Feign.Builder builder, FeignContext context, HardCodedTarget<T> target) {
    //获取微服务请求的FeignBlockingLoadBalancerClient或RetryableFeignBlockingLoadBalancerClient
		Client client = getOptional(context, Client.class);
		if (client != null) {
      //初始化构造器的Client对象
			builder.client(client);
			Targeter targeter = get(context, Targeter.class);
      // 此处调用feign.Feign.Builder#target(feign.Target<T>)
      //构建ReflectiveFeign对象
      //构建AOP切面请求代理
			return targeter.target(this, builder, context, target);
		}

		throw new IllegalStateException(
				"No Feign Client for loadBalancing defined. Did you forget to include spring-cloud-starter-loadbalancer?");
	}
```

>
经上述代码分析可知@FeignClient注解的url为空时，client为loadbalancer微服务对象，其Client对象是FeignBlockingLoadBalancerClient、RetryableFeignBlockingLoadBalancerClient；而url为非空时为普通请求，则Client对象是ApacheHttpClient实例对象，其获取ApacheHttpClient对象是通过getDelegate方法获取，这又是为何呢？

```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ApacheHttpClient.class)
@ConditionalOnBean({ LoadBalancerClient.class, LoadBalancerClientFactory.class })
@ConditionalOnProperty(value = "feign.httpclient.enabled", matchIfMissing = true)
@Conditional(HttpClient5DisabledConditions.class)
@Import(HttpClientFeignConfiguration.class)
@EnableConfigurationProperties(LoadBalancerProperties.class)
class HttpClientFeignLoadBalancerConfiguration {

	@Bean
	@ConditionalOnMissingBean
	@Conditional(OnRetryNotEnabledCondition.class)
	public Client feignClient(LoadBalancerClient loadBalancerClient, HttpClient httpClient,
			LoadBalancerProperties properties, LoadBalancerClientFactory loadBalancerClientFactory) {
		ApacheHttpClient delegate = new ApacheHttpClient(httpClient);
    //未开启重试实例对象，代理是ApacheHttpClient
		return new FeignBlockingLoadBalancerClient(delegate, loadBalancerClient, properties, loadBalancerClientFactory);
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(name = "org.springframework.retry.support.RetryTemplate")
	@ConditionalOnBean(LoadBalancedRetryFactory.class)
	@ConditionalOnProperty(value = "spring.cloud.loadbalancer.retry.enabled", havingValue = "true",
			matchIfMissing = true)
	public Client feignRetryClient(LoadBalancerClient loadBalancerClient, HttpClient httpClient,
			LoadBalancedRetryFactory loadBalancedRetryFactory, LoadBalancerProperties properties,
			LoadBalancerClientFactory loadBalancerClientFactory) {
		ApacheHttpClient delegate = new ApacheHttpClient(httpClient);
    //开启重试实例化对象，代理为ApacheHttpClient
		return new RetryableFeignBlockingLoadBalancerClient(delegate, loadBalancerClient, loadBalancedRetryFactory,
				properties, loadBalancerClientFactory);
	}

}
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)