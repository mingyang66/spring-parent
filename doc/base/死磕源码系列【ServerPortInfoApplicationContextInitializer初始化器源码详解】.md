### 死磕源码系列【ServerPortInfoApplicationContextInitializer初始化器源码详解】

> ServerPortInfoApplicationContextInitializer是ApplicationContextInitializer接口的实现类，会通过SPI方式在应用程序启动时初始化，其主要作用是在环境Environment中添加一个属性源，将应用的本地端口号添加进去，方便通过@Value或environment获取本地端口号；ServerPortInfoApplicationContextInitializer初始化器还实现了另外一个ApplicationListener监听器，监听器实现方法会在服务器server启动后调用。

##### 1.初始化方法initialize

```java
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		applicationContext.addApplicationListener(this);
	}
```

> initialize方法是ApplicationContextInitializer接口的回调方法，会在springboot应用启动时在org.springframework.boot.SpringApplication#prepareContext方法中调用初始化，在方法中会将当前类作为监听器添加到应用程序上下文ConfigurableApplicationContext（实际是AnnotationConfigServletWebServerApplicationContext）中，会在后面web server启动之后触发。



##### 2.onApplicationEvent监听器回调方法

```java
	@Override
	public void onApplicationEvent(WebServerInitializedEvent event) {
    //local.server.port
		String propertyName = "local." + getName(event.getApplicationContext()) + ".port";
		//设置environment环境中的端口属性，从事件对象WebServerInitializedEvent中获取本地的端口号
    setPortProperty(event.getApplicationContext(), propertyName, event.getWebServer().getPort());
	}
	private String getName(WebServerApplicationContext context) {
    //获取应用程序上下文的命名空间，可能为null
		String name = context.getServerNamespace();
    //实际返回是server
		return StringUtils.hasText(name) ? name : "server";
	}
	private void setPortProperty(ApplicationContext context, String propertyName, int port) {
		if (context instanceof ConfigurableApplicationContext) {
			setPortProperty(((ConfigurableApplicationContext) context).getEnvironment(), propertyName, port);
		}
		if (context.getParent() != null) {
			setPortProperty(context.getParent(), propertyName, port);
		}
	}
	@SuppressWarnings("unchecked")
	private void setPortProperty(ConfigurableEnvironment environment, String propertyName, int port) {
    //获取environment环境中的属性源配置集合
		MutablePropertySources sources = environment.getPropertySources();
    //获取集合中name为server.ports的PropertySource属性源配置
		PropertySource<?> source = sources.get("server.ports");
		if (source == null) {
      //创建一个name是server.ports的字典类型属性源配置
			source = new MapPropertySource("server.ports", new HashMap<>());
      //将属性源配置放在最高优先级位置
			sources.addFirst(source);
		}
    //将端口属性名local.server.port及本地端口号添加到environment中
		((Map<String, Object>) source.getSource()).put(propertyName, port);
	}
```

方法中参数WebServerInitializedEvent事件是当WebServer准备好之后触发，事件对象中持有本地的端口号，可以从中获取。

##### 3.WebServerInitializedEvent事件在如何触发的？是在WebServerStartStopLifecycle类中开启的。

WebServer在ServletWebServerApplicationContext中启动和关闭都在此类中执行

```java
class WebServerStartStopLifecycle implements SmartLifecycle {

	private final ServletWebServerApplicationContext applicationContext;

	private final WebServer webServer;

	private volatile boolean running;

	WebServerStartStopLifecycle(ServletWebServerApplicationContext applicationContext, WebServer webServer) {
		this.applicationContext = applicationContext;
		this.webServer = webServer;
	}

	@Override
	public void start() {
		this.webServer.start();
		this.running = true;
    //WebServer就绪之后发送事件ServletWebServerInitializedEvent（WebServerInitializedEvent），包含服务器的本地端口号
		this.applicationContext
				.publishEvent(new ServletWebServerInitializedEvent(this.webServer, this.applicationContext));
	}

	@Override
	public void stop() {
		this.webServer.stop();
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}

	@Override
	public int getPhase() {
		return Integer.MAX_VALUE - 1;
	}

}
```

> 到这里我们知道了初始化器是在WebServerStartStopLifecycle生命周期类的start方法中发布事件触发的，那么WebServerStartStopLifecycle生命周期类又是在哪里触发调用的start方法呢？是在org.springframework.context.support.AbstractApplicationContext#refresh方法中的最后一步调用finishRefresh方法，finishRefresh方法的getLifecycleProcessor().onRefresh()方法，此处就不在一步步的跟踪了，有兴趣的同学可以自己调试源码看看。

------

GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

