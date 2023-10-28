#### Redis学习笔记3：基于springboot的lettuce redis客户端validateConnection连接有效性检查

> LettuceConnectionFactory连接工厂默认对redis操作时不会对本地共享连接进行有效性检测，不进行有效性检测可以 提升应用程序的性能，但是也会带来一定的连接无效性的风险，LettuceConnectionFactory提供了一个validateConnection属性，默认值是false,可以在我们对性能要求不是很高的场景下对redis操作之前进行有效性检查，如果无效则重新建立连接。

一个对springboot redis框架进行重写，支持lettuce、jedis、连接池、同时连接多个集群、多个redis数据库、开发自定义属性配置的开源SDK

```xml
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-redis</artifactId>
    <version>4.3.9</version>
</dependency>
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

##### 一、LettuceConnectionFactory连接工厂属性

```java
//是否进行连接有效性检查，默认：false
private boolean validateConnection = false;
```

##### 二、LettuceConnectionFactory.SharedConnection#getConnection获取连接并进行有效性检查

```java
		@Nullable
		StatefulConnection<E, E> getConnection() {

			synchronized (this.connectionMonitor) {

				if (this.connection == null) {
          //获取共享本地连接
					this.connection = getNativeConnection();
				}
        //判定是否进行连接有效性检查
				if (getValidateConnection()) {
					validateConnection();
				}

				return this.connection;
			}
		}
```

##### 三、LettuceConnectionFactory.SharedConnection#validateConnection对连接进行检查或重建

```java
		void validateConnection() {

			synchronized (this.connectionMonitor) {
				//连接是否有效，默认：无效
				boolean valid = false;
				//如果连接不为空，并且是打开状态，则对连接进行ping操作
				if (connection != null && connection.isOpen()) {
					try {

						if (connection instanceof StatefulRedisConnection) {
							((StatefulRedisConnection) connection).sync().ping();
						}

						if (connection instanceof StatefulRedisClusterConnection) {
							((StatefulRedisClusterConnection) connection).sync().ping();
						}
            //如果可以ping通，则说明是有效连接
						valid = true;
					} catch (Exception e) {
						log.debug("Validation failed", e);
					}
				}

				if (!valid) {

					log.info("Validation of shared connection failed; Creating a new connection.");
          //如果是无效连接，则重置连接
					resetConnection();
          //重新建立本地连接
					this.connection = getNativeConnection();
				}
			}
		}
```

