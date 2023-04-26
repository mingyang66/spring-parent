### 死磕源码系列【WebServer接口源码解析】

> WebServer可用于控制Tomcat web服务器。通常，这个类应该使用TomcatReactiveWebServerFactory或TomcatServletWebServerFactory创建，但不能直接创建。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201030092823382.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhb21pbmd5YW5n,size_16,color_FFFFFF,t_70#pic_center)

由类的结构图可知，TomcatWebServer是WebServer的子类，先看下WebServer接口：

```java
//表示完全配置的web服务器的简单接口（例如：Tomcat、Jetty、Netty）,允许服务器start和stop
public interface WebServer {

	/**
	 * 启动web server,在一个已经启动的服务器上调用此方法没有任何效果
	 */
	void start() throws WebServerException;

	/**
	 * 停止web server,在一个已经停止的服务器上调用此方法没有任何效果
	 */
	void stop() throws WebServerException;

	/**
	 * 返回当前服务器正在监听的端口，如果不存在，则返回-1
	 */
	int getPort();

	/**
	 * 优雅的关闭web服务器，停止接收新的请求，并在最后尝试调用callback的回调方法；也可以通过调用stop来停止，
	 * 默认的实现时立马调用callback回调
	 */
	default void shutDownGracefully(GracefulShutdownCallback callback) {
		callback.shutdownComplete(GracefulShutdownResult.IMMEDIATE);
	}

}
```

##### GracefulShutdownCallback优雅的关闭接口的回调：

```
@FunctionalInterface
public interface GracefulShutdownCallback {

	/**
	 * 使用给定的结果优雅的关闭服务器
	 * @param result the result of the shutdown
	 */
	void shutdownComplete(GracefulShutdownResult result);

}
```

GracefulShutdownResult提供了几种优雅关闭服务器的方式（真实的优雅关机模式是如果服务器在宽限期内还有活跃的请求继续处理，使用REQUESTS_ACTIVE模式，否则使用IDLE模式）：

```java
public enum GracefulShutdownResult {

	/**
	 * 宽限期结束前连接保持活动状态，也就是说宽限期结束后无论是否处理完成都会强制关闭
	 */
	REQUESTS_ACTIVE,

	/**
	 * 在宽限期结束时服务器空闲，没有活动请求
	 */
	IDLE,

	/**
	 * 服务器立即关闭，忽略任何活动请求
	 */
	IMMEDIATE;

}

```

------

#####   