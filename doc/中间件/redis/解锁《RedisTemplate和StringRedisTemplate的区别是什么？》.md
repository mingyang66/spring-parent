### 解锁《RedisTemplate和StringRedisTemplate的区别是什么？》

- StringRedisTemplate是RedisTemplate的子类，RedisTemplate是一个泛型类型，而StringRedisTemplate则不是。
- 两者的序列化策略不同，StringRedisTemplate采用String的序列化策略，key和value都是采用此种策略；RedisTemplate则采用JDK的序列化策略；
- 两者的数据不共通，原因就是上述所说的序列化策略不同的原因；

##### StringRedisTemplate默认序列化策略：

```java
	public StringRedisTemplate() {
		setKeySerializer(RedisSerializer.string());
		setValueSerializer(RedisSerializer.string());
		setHashKeySerializer(RedisSerializer.string());
		setHashValueSerializer(RedisSerializer.string());
	}
```

##### RedisTemplate默认序列化策略：

```java
	@Override
	public void afterPropertiesSet() {

		super.afterPropertiesSet();

		boolean defaultUsed = false;

		if (defaultSerializer == null) {

			defaultSerializer = new JdkSerializationRedisSerializer(
					classLoader != null ? classLoader : this.getClass().getClassLoader());
		}

		if (enableDefaultSerializer) {

			if (keySerializer == null) {
				keySerializer = defaultSerializer;
				defaultUsed = true;
			}
			if (valueSerializer == null) {
				valueSerializer = defaultSerializer;
				defaultUsed = true;
			}
			if (hashKeySerializer == null) {
				hashKeySerializer = defaultSerializer;
				defaultUsed = true;
			}
			if (hashValueSerializer == null) {
				hashValueSerializer = defaultSerializer;
				defaultUsed = true;
			}
		}

		if (enableDefaultSerializer && defaultUsed) {
			Assert.notNull(defaultSerializer, "default serializer null and not all serializers initialized");
		}

		if (scriptExecutor == null) {
			this.scriptExecutor = new DefaultScriptExecutor<>(this);
		}

		initialized = true;
	}
```

> 默认采用JdkSerializationRedisSerializer序列化方式；

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)