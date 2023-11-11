#### Redis学习笔记2：基于springboot的lettuce redis客户端eagerInitialization提前初始化连接

> LettuceConnectionFactory连接工厂类默认是不会提前初始化本地物理连接的，也就是懒加载模式，只有等到客户端的RedisTemplate等具体要操作Redis时才会去建立连接。

一个对springboot redis框架进行重写，支持lettuce、jedis、连接池、同时连接多个集群、多个redis数据库、开发自定义属性配置的开源SDK

```xml
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-redis</artifactId>
    <version>4.4.0</version>
</dependency>
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

##### 一、LettuceConnectionFactory连接工厂类属性

```java
//是否提前初始化共享本地连接，默认：false
private boolean eagerInitialization = false;
private @Nullable SharedConnection<byte[]> connection;
private @Nullable SharedConnection<ByteBuffer> reactiveConnection;
```

##### 二、LettuceConnectionFactory#afterPropertiesSet方法提前初始化本地连接

```java
	public void afterPropertiesSet() {
    //创建Redis连接对象
		this.client = createClient();

		this.connectionProvider = new ExceptionTranslatingConnectionProvider(createConnectionProvider(client, CODEC));
		this.reactiveConnectionProvider = new ExceptionTranslatingConnectionProvider(
				createConnectionProvider(client, LettuceReactiveRedisConnection.CODEC));

		if (isClusterAware()) {

			this.clusterCommandExecutor = new ClusterCommandExecutor(
					new LettuceClusterTopologyProvider((RedisClusterClient) client),
					new LettuceClusterConnection.LettuceClusterNodeResourceProvider(this.connectionProvider),
					EXCEPTION_TRANSLATION);
		}

		this.initialized = true;
   	//只有在共享本地连接对象shareNativeConnection为true,并且eagerInitialization为true的时候才会进行本地连接初始化
		if (getEagerInitialization() && getShareNativeConnection()) {
			initConnection();
		}
	}

 public boolean getEagerInitialization() {
		return eagerInitialization;
	}
public boolean getShareNativeConnection() {
		return shareNativeConnection;
	}
```

> LettuceConnectionFactory连接工厂类实现了InitializingBean接口，会在类初始化完成后调用afterPropertiesSet方法

##### 三、LettuceConnectionFactory#initConnection初始化本地连接对象

```java
	public void initConnection() {
		//重置调当前连接工厂类的连接
		resetConnection();
    //获取并初始化通用共享本地连接，初始化属性connection
		if (isClusterAware()) {
			getSharedClusterConnection();
		} else {
			getSharedConnection();
		}
   //获取并初始化响应式共享本地连接，初始化属性reactiveConnection
		getSharedReactiveConnection();
	}
	public void resetConnection() {

		assertInitialized();

		Optionals.toStream(Optional.ofNullable(connection), Optional.ofNullable(reactiveConnection))
				.forEach(SharedConnection::resetConnection);

		synchronized (this.connectionMonitor) {
      //将连接工厂类的两个属性置为null
			this.connection = null;
			this.reactiveConnection = null;
		}
	}
```

