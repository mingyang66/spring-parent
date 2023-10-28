##### Redis学习笔记4：基于springboot的lettuce redis客户端LettuceConnectionFactory连接工厂LettuceConnectionProvider实现

> LettuceConnectionFactory连接工厂在建立本地连接的时候会通过LettuceConnectionProvider的具体实现建立真实的本地连接，LettuceConnectionProvider在连接工厂中起到的作用就是连接提供者的角色，具体建立连接是会调用对应的getConnection方法。

一个对springboot redis框架进行重写，支持lettuce、jedis、连接池、同时连接多个集群、多个redis数据库、开发自定义属性配置的开源SDK

```xml
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-redis</artifactId>
    <version>4.3.9</version>
</dependency>
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

##### 一、LettuceConnectionFactory连接工厂提供了两个LettuceConnectionProvider属性

```java
//通用连接提供者对象，对RedisTemplate及StringRedisTemplate使用的本地连接建立连接对象	
private @Nullable LettuceConnectionProvider connectionProvider;
//响应是连接对象，对ReactiveRedisTemplate及ReactiveStringRedisTemplate使用的本地连接建立连接对象
private @Nullable LettuceConnectionProvider reactiveConnectionProvider;
```

##### 二、LettuceConnectionProvider属性对象初始化ettuce.LettuceConnectionFactory#afterPropertiesSet

> ExceptionTranslatingConnectionProvider类是LettuceConnectionProvider接口的一个代理对象，可自行查看源码学习

```java
	public void afterPropertiesSet() {

		this.client = createClient();
		//通用连接提供者属性初始化
		this.connectionProvider = new ExceptionTranslatingConnectionProvider(createConnectionProvider(client, CODEC));
    //响应式连接提供者属性初始化
		this.reactiveConnectionProvider = new ExceptionTranslatingConnectionProvider(
				createConnectionProvider(client, LettuceReactiveRedisConnection.CODEC));
		...
	}
```

##### 三、LettuceConnectionFactory#createConnectionProvider创建连接提供者对象

```java
	private LettuceConnectionProvider createConnectionProvider(AbstractRedisClient client, RedisCodec<?, ?> codec) {
		//创建连接提供者对象
		LettuceConnectionProvider connectionProvider = doCreateConnectionProvider(client, codec);
		//创建基于连接池的连接提供者对象
		if (this.clientConfiguration instanceof LettucePoolingClientConfiguration) {
			return new LettucePoolingConnectionProvider(connectionProvider,
					(LettucePoolingClientConfiguration) this.clientConfiguration);
		}

		return connectionProvider;
	}
	//创建基于不同实现方案的连接提供者对象
	protected LettuceConnectionProvider doCreateConnectionProvider(AbstractRedisClient client, RedisCodec<?, ?> codec) {

		ReadFrom readFrom = getClientConfiguration().getReadFrom().orElse(null);

		if (isStaticMasterReplicaAware()) {

			List<RedisURI> nodes = ((RedisStaticMasterReplicaConfiguration) configuration).getNodes().stream() //
					.map(it -> createRedisURIAndApplySettings(it.getHostName(), it.getPort())) //
					.peek(it -> it.setDatabase(getDatabase())) //
					.collect(Collectors.toList());

			return new StaticMasterReplicaConnectionProvider((RedisClient) client, codec, nodes, readFrom);
		}

		if (isClusterAware()) {
			return new ClusterConnectionProvider((RedisClusterClient) client, codec, readFrom);
		}
		
		return new StandaloneConnectionProvider((RedisClient) client, codec, readFrom);
	}
```

