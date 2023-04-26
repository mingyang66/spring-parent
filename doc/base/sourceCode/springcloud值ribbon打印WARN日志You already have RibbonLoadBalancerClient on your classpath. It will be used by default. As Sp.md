### springcloud值ribbon打印WARN日志You already have RibbonLoadBalancerClient on your classpath. It will be used by default. As Spring Cloud Ribbon is in maintenance mode. We recommend switching to BlockingLoadBalancerClient instead. In order to use it, set the value of `spring.cloud.loadbalancer.ribbon.enabled` to `false` or remove spring-cloud-starter-netflix-ribbon from your project.

在使用springcloud ribbon做客户端负载均衡的时候控制台打印如下日志：

```
You already have RibbonLoadBalancerClient on your classpath. It will be used by default. As Spring Cloud Ribbon is in maintenance mode. We recommend switching to BlockingLoadBalancerClient instead. In order to use it, set the value of `spring.cloud.loadbalancer.ribbon.enabled` to `false` or remove spring-cloud-starter-netflix-ribbon from your project.
```

看着很疑惑就去查询了代码，此行日志是在BlockingLoadBalancerClientAutoConfiguration类中打印出来的，源码如下：

```java
@Configuration(proxyBeanMethods = false)
@LoadBalancerClients
@AutoConfigureAfter(LoadBalancerAutoConfiguration.class)
@AutoConfigureBefore({
		org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration.class,
		AsyncLoadBalancerAutoConfiguration.class })
public class BlockingLoadBalancerClientAutoConfiguration {
	//由于默认是开启ribbon客户端的，所以就默认实例化BlockingLoadBalancerClientRibbonWarnLogger对象
	@Bean
	@ConditionalOnClass(
			name = "org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient")
	@ConditionalOnProperty(value = "spring.cloud.loadbalancer.ribbon.enabled",
			matchIfMissing = true)
	public BlockingLoadBalancerClientRibbonWarnLogger blockingLoadBalancerClientRibbonWarnLogger() {
		return new BlockingLoadBalancerClientRibbonWarnLogger();
	}
	//在没有使用Ribbon客户端的时候使用BlockingLoadBalancerClient客户端
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(RestTemplate.class)
	@Conditional(OnNoRibbonDefaultCondition.class)
	protected static class BlockingLoadbalancerClientConfig {

		@Bean
		@ConditionalOnBean(LoadBalancerClientFactory.class)
		@Primary
		public BlockingLoadBalancerClient blockingLoadBalancerClient(
				LoadBalancerClientFactory loadBalancerClientFactory) {
			return new BlockingLoadBalancerClient(loadBalancerClientFactory);
		}

	}

	static class BlockingLoadBalancerClientRibbonWarnLogger {

		private static final Log LOG = LogFactory
				.getLog(BlockingLoadBalancerClientRibbonWarnLogger.class);
		//当前类被初始化之后就会默认调用
		@PostConstruct
		void logWarning() {
			if (LOG.isWarnEnabled()) {
				LOG.warn(
						"You already have RibbonLoadBalancerClient on your classpath. It will be used by default. "
								+ "As Spring Cloud Ribbon is in maintenance mode. We recommend switching to "
								+ BlockingLoadBalancerClient.class.getSimpleName()
								+ " instead. In order to use it, set the value of `spring.cloud.loadbalancer.ribbon.enabled` to `false` or "
								+ "remove spring-cloud-starter-netflix-ribbon from your project.");
			}
		}

	}

}

```

经上述源码分析我们应该很清楚为什么会打印这么一条warn警告日志了，这是官方想推荐自己的负载均衡客户端BlockingLoadBalancerClient，但是BlockingLoadBalancerClient还有很多不完善的地方，还是老老实实的用Ribbon吧；警告对我们使用ribbon没有任何影响，如果看着不舒服的话可以关掉，通过配置当前类日志级别的方式可以做到：

```properties
logging.level.org.springframework.cloud.loadbalancer.config.BlockingLoadBalancerClientAutoConfiguration=warn
```

GitHub地址:[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)