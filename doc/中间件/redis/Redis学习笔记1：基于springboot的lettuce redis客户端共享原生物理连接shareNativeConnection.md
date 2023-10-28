#### Redis学习笔记1：基于springboot的lettuce redis客户端共享原生物理连接shareNativeConnection

> springboot默认是使用基于lettuce的redis客户端，默认情况下LettuceConnectionFactory的shareNativeConnection属性值为true；在springboot的IOC容器之中一个LettuceConnectionFactory最多提供两个共享本地连接，一个给RedisTemplate和StringRedisTemplate的所有操作共用，另外一个给ReactiveRedisTemplate和ReactiveStringRedisTemplate的所有操作共用。

一个对springboot redis框架进行重写，支持lettuce、jedis、连接池、同时连接多个集群、多个redis数据库、开发自定义属性配置的开源SDK：

```xml
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-redis</artifactId>
    <version>4.3.9</version>
</dependency>
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

##### 一、LettuceConnectionFactory连接工厂类默认属性

```java
	//建立Redis连接的client对象
  private @Nullable AbstractRedisClient client;
	private @Nullable LettuceConnectionProvider connectionProvider;
	private @Nullable LettuceConnectionProvider reactiveConnectionProvider;
  //是否校验连接，默认：false
	private boolean validateConnection = false;
  //是否共享本地连接，默认：true
	private boolean shareNativeConnection = true;
  //是否提前初始化共享本地连接，默认：false
	private boolean eagerInitialization = false;
  //通用共享本地连接，默认：null
	private @Nullable SharedConnection<byte[]> connection;
  //基于reactive的通用共享本地连接，默认：null
	private @Nullable SharedConnection<ByteBuffer> reactiveConnection;
```



##### 二、Redis连接获取统一入口RedisConnectionUtils#doGetConnection

```java
public static RedisConnection doGetConnection(RedisConnectionFactory factory, boolean allowCreate, boolean bind,
			boolean transactionSupport) {
		...
      //入参factory是LettuceConnectionFactory对象，
		RedisConnection connection = fetchConnection(factory);
		...
		return connection;
	}
	private static RedisConnection fetchConnection(RedisConnectionFactory factory) {
    //通过LettuceConnectionFactory对象的getConnection方法获取连接对象
		return factory.getConnection();
	}
```

##### 三、LettuceConnectionFactory#getConnection连接工厂类方法获取连接对象

```java
	public RedisConnection getConnection() {

		assertInitialized();

		if (isClusterAware()) {
			return getClusterConnection();
		}
		//此处会获取基于lettuce的连接对象
		LettuceConnection connection = doCreateLettuceConnection(getSharedConnection(), connectionProvider, getTimeout(),
				getDatabase());
		connection.setConvertPipelineAndTxResults(convertPipelineAndTxResults);
		return connection;
	}
 // 当前shareNativeConnection为true,则回去获取或者创建本地原生共享连接
	@Nullable
	protected StatefulRedisConnection<byte[], byte[]> getSharedConnection() {
		return shareNativeConnection && !isClusterAware()
				? (StatefulRedisConnection) getOrCreateSharedConnection().getConnection()
				: null;
	}
	// 首先创建一个共享连接对象，此处包含一个真实创建连接对象的connectionProvider对象，实际是StandaloneConnectionProvider
  // 实际的创建原理，我们后面的文章分析
	private SharedConnection<byte[]> getOrCreateSharedConnection() {

		synchronized (this.connectionMonitor) {

			if (this.connection == null) {
				this.connection = new SharedConnection<>(connectionProvider);
			}

			return this.connection;
		}
	}
```

##### 四、LettuceConnectionFactory.SharedConnection#getConnection创建原生共享本地连接

```java
		// 此方法是所有创建真正的物理连接都要调用的方法入口
    @Nullable
		StatefulConnection<E, E> getConnection() {

			synchronized (this.connectionMonitor) {
        //如果连接不存在，则会创建本地连接
				if (this.connection == null) {
					this.connection = getNativeConnection();
				}
				// 此处是根据属性配置是否需要校验连接有效性，如果需要则会校验，本文不做重点分析
				if (getValidateConnection()) {
					validateConnection();
				}

				return this.connection;
			}
		}
		private StatefulConnection<E, E> getNativeConnection() {
      //此处是创建本地连接的入口，具体是调用LettuceConnectionProvider的子类StandaloneConnectionProvider
			return connectionProvider.getConnection(StatefulConnection.class);
		}
```

##### 五、StandaloneConnectionProvider#getConnection方法创建本地连接

```java
	@Override
	public <T extends StatefulConnection<?, ?>> T getConnection(Class<T> connectionType) {

		if (connectionType.equals(StatefulRedisSentinelConnection.class)) {
			return connectionType.cast(client.connectSentinel());
		}

		if (connectionType.equals(StatefulRedisPubSubConnection.class)) {
			return connectionType.cast(client.connectPubSub(codec));
		}
		//本地连接是通过此处方法创建，具体是通过client.connect创建
    //此处的client是通过LettuceConnectionFactory的afterPropertiesSet方法初始化的client对象
    //再具体的创建连接过程要进入RedisClient对象中，本文不做进一步分析。
		if (StatefulConnection.class.isAssignableFrom(connectionType)) {

			return connectionType.cast(readFrom.map(it -> this.masterReplicaConnection(redisURISupplier.get(), it))
					.orElseGet(() -> client.connect(codec)));
		}

		throw new UnsupportedOperationException("Connection type " + connectionType + " not supported");
	}
```

##### 六、共享本地连接是如何实现的

​		经过上面的源码分析其实结果已经很清楚了，redis的共享本地连接就保存在ioc容器中的LettuceConnectionFactory连接工厂类对象中，有两个属性connection、reactiveConnection，其为通用编程模式和响应式编程分别提供了一个共享本地对象，开发者可以根据个人编程需求通过RedisTemplate等模板对象去LettuceConnectionFactory对象上去取符合要求的共享本地连接。