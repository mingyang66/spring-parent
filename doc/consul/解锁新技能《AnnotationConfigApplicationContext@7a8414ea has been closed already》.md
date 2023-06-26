#### 解锁新技能《AnnotationConfigApplicationContext@7a8414ea has been closed already》

> 最近工作过程中遇到了标题中的错误，导致整个项目都不可以访问；原因是我修改了consul的配置，修改过后由于consul监控配置开关是打开着的，所以监控到配置做了修改后会重新创建ApplicationContext对象，并且会将之前的ApplicationContext对象销毁调；由于我在项目启动的时候初始化了一个ApplicationContext全局变量，会在项目中AOP记录访问日志的时候使用，所以每个接口都会报标题中的错误；

##### 一、原因分析

- Consul配置监控类ConfigWatch监控consul配置中心的配置变动，如果配置有变动会触发RefreshEvent事件

```java
	@Timed("consul.watch-config-keys")
	public void watchConfigKeyValues() {
		if (!this.running.get()) {
			return;
		}
		for (String context : this.consulIndexes.keySet()) {

							//********
							RefreshEventData data = new RefreshEventData(context, currentIndex, newIndex);
      				//配置有变动，触发RefreshEvent事件
							this.publisher.publishEvent(new RefreshEvent(this, data, data.toString()));
						//*****
    }
		this.firstTime = false;
	}
```

- RefreshEventListener监听器监听RefreshEvent事件，并创建新的ApplicationContext对象，销毁原来的ApplicationContext对象

```java
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ApplicationReadyEvent) {
			handle((ApplicationReadyEvent) event);
		}
		else if (event instanceof RefreshEvent) {
      //监听RefreshEvent事件
			handle((RefreshEvent) event);
		}
	}

	public void handle(ApplicationReadyEvent event) {
		this.ready.compareAndSet(false, true);
	}

	public void handle(RefreshEvent event) {
		if (this.ready.get()) { // don't handle events before app is ready
			log.debug("Event received " + event.getEventDesc());
      // 触发ContextRefresher类的refresh方法来创建ApplicationContext对象
			Set<String> keys = this.refresh.refresh();
			log.info("Refresh keys changed: " + keys);
		}
	}
```

- ContextRefresher类新建ApplicationContext并发送EnvironmentChangeEvent事件

```java
	public synchronized Set<String> refresh() {
		Set<String> keys = refreshEnvironment();
		this.scope.refreshAll();
		return keys;
	}

	public synchronized Set<String> refreshEnvironment() {
		Map<String, Object> before = extract(this.context.getEnvironment().getPropertySources());
		updateEnvironment();
		Set<String> keys = changes(before, extract(this.context.getEnvironment().getPropertySources())).keySet();
    //触发EnvironmentChangeEvent事件，可以在此事件的监听器来修改重建后的ApplicationContext
		this.context.publishEvent(new EnvironmentChangeEvent(this.context, keys));
		return keys;
	}
//此方法的实现(LegacyContextRefresher.updateEnvironment)是真正用来新建ApplicationContext和销毁原来ApplicationContext的
protected abstract void updateEnvironment();
```

- LegacyContextRefresher.updateEnvironment实现

```java
public class LegacyContextRefresher extends ContextRefresher {

	@Deprecated
	public LegacyContextRefresher(ConfigurableApplicationContext context, RefreshScope scope) {
		super(context, scope);
	}

	public LegacyContextRefresher(ConfigurableApplicationContext context, RefreshScope scope,
			RefreshAutoConfiguration.RefreshProperties properties) {
		super(context, scope, properties);
	}

	@Override
	protected void updateEnvironment() {
		addConfigFilesToEnvironment();
	}

	/* For testing. */ ConfigurableApplicationContext addConfigFilesToEnvironment() {
		ConfigurableApplicationContext capture = null;
		try {
			//*****************省略*******************
			SpringApplicationBuilder builder = new SpringApplicationBuilder(Empty.class).bannerMode(Banner.Mode.OFF)
					.web(WebApplicationType.NONE).environment(environment);
			// Just the listeners that affect the environment (e.g. excluding logging
			// listener because it has side effects)
			builder.application().setListeners(
					Arrays.asList(new BootstrapApplicationListener(), new BootstrapConfigFileApplicationListener()));
      // 新建ApplicationContext对象，实际会进入org.springframework.boot.builder.SpringApplicationBuilder#run方法创建；
			capture = builder.run();
			//*************省略**********************
		}
		finally {
			ConfigurableApplicationContext closeable = capture;
			while (closeable != null) {
				try {
          //
					closeable.close();
				}
				catch (Exception e) {
					// Ignore;
				}
				if (closeable.getParent() instanceof ConfigurableApplicationContext) {
					closeable = (ConfigurableApplicationContext) closeable.getParent();
				}
				else {
					break;
				}
			}
		}
		return capture;
	}

}
```

上述builder.run方法会进入org.springframework.boot.builder.SpringApplicationBuilder#run最终是在org.springframework.boot.SpringApplication#run(java.lang.String...)方法中创建：



##### 二、解决方案

> 在原因分析中我们知道新建ApplicationContext后会发送EnvironmentChangeEvent事件，而EnvironmentChangeEvent事件会接收一个ApplicationContext作为参数，我们的解决方案就是监听EnvironmentChangeEvent事件，修改定义的全局变量；

###### 方案一：使用@RabbitListener注解监听EnvironmentChangeEvent事件

```java
    @EventListener
    public void handler(EnvironmentChangeEvent event){
        ApplicationContext context = (ApplicationContext) event.getSource();
        //修改定义的全局ApplicationContext对象
        IOCContext.setCONTEXT(context);
        System.out.println(event.getSource());
    }
```

> 此方案适用于具体项目之中，如果想用在springboot脚手架类SDK中，这种方案就行不通了；

###### 方案二：定义实现ApplicationListener监听器接口类

```java
public class EnvironmentChangeApplicationListener implements ApplicationListener<EnvironmentChangeEvent> {
    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        IOCContext.setCONTEXT((ApplicationContext) event.getSource());
    }
}

```



GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)