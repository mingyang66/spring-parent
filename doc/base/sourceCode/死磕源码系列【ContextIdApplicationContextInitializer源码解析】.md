死磕源码系列【ContextIdApplicationContextInitializer源码解析】

> ContextIdApplicationContextInitializer类是ApplicationContextInitializer初始化器接口实现类，会在应用程序启动的时候初始化应用程序的唯一ID。

ContextIdApplicationContextInitializer设置Spring
ApplicationContext上下文ID,如果spring.application.name属性存在，则将其作为ID，否则使用application默认值作为ID，源码如下：

```java
public class ContextIdApplicationContextInitializer
		implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

	private int order = Ordered.LOWEST_PRECEDENCE - 10;

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}
	//初始化器对象回调方法
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
    //获取上下文ID实例
		ContextId contextId = getContextId(applicationContext);
    //将获取到的ID设置到ApplicationContext上下文中
		applicationContext.setId(contextId.getId());
    //将上下文ID注入到IOC容器之中
		applicationContext.getBeanFactory().registerSingleton(ContextId.class.getName(), contextId);
	}
	//获取上下文 ID实例对象
	private ContextId getContextId(ConfigurableApplicationContext applicationContext) {
		ApplicationContext parent = applicationContext.getParent();
		if (parent != null && parent.containsBean(ContextId.class.getName())) {
			return parent.getBean(ContextId.class).createChildId();
		}
		return new ContextId(getApplicationId(applicationContext.getEnvironment()));
	}
	//获取应用程序上下文ID,如果spring.application.name存在，则使用此值，否则使用application
	private String getApplicationId(ConfigurableEnvironment environment) {
		String name = environment.getProperty("spring.application.name");
		return StringUtils.hasText(name) ? name : "application";
	}

	/**
	 * 上下文ID
	 */
	static class ContextId {

		private final AtomicLong children = new AtomicLong(0);

		private final String id;

		ContextId(String id) {
			this.id = id;
		}

		ContextId createChildId() {
			return new ContextId(this.id + "-" + this.children.incrementAndGet());
		}

		String getId() {
			return this.id;
		}

	}

}
```

------

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

