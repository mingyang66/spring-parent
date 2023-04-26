### RabbitMQ学习笔记：高级特性TTL（过期时间）

> TTL，Time To Live的简称，即消息过期时间，可以对消息和队列设置TTL。

目前有两种方式可以设置消息的TTL。第一种是通过队列的属性设置，队列中的所有消息都有相同的过期时间。第二种方法是对消息本身进行单独设置，每条消息的TTL可以不同。如果两种方法同时设置，则TTL以两者之间较小的那个数值为准。消息在队列中的生存时间一旦超过设置的TTL值时，就会变成“死信”（Dead
Message）,消费者将无法再收到该消息（不是绝对的）。

##### 通过队列属性设置消息过期时间

```java
            Map<String, Object> arguments = Maps.newHashMap();
            /**
             * 设置消息发送到队列中在被丢弃之前可以存活的时间，单位：毫秒
             */
            arguments.put("x-message-ttl", 5000);
            /**
             * 声明队列
             * durable: true 如果我们声明一个持久化队列（队列将会在服务重启后任然存在）
             * exclusive: true 如果我们声明一个独占队列（仅限于此链接）
             * autoDelete: true 声明一个自动删除队列（服务器将在不使用它时删除，即队列的连接数为0）
             * arguments: 队列的其它属性（构造参数）
             */
            channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);
```

##### 对消息本身设定过期时间

```java
AMQP.BasicProperties.Builder properties = MessageProperties.PERSISTENT_TEXT_PLAIN.builder();
properties.expiration("5000");
/**
* 发布消息
* 发布到不存在的交换器将导致信道级协议异常，该协议关闭信道，
* exchange: 要将消息发送到的交换器
* routingKey: 路由KEY
* props: 消息的其它属性，如：路由头等
* body: 消息体
*/
channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, true, properties.build(), message.getBytes());
```

##### 设置队列过期时间

```java
            Map<String, Object> arguments = Maps.newHashMap();
            /**
             * 设置一个队列多长时间未被使用将会被删除，单位：毫秒
             */
            arguments.put("x-expires", 5000);
            /**
             * 声明队列
             * durable: true 如果我们声明一个持久化队列（队列将会在服务重启后任然存在）
             * exclusive: true 如果我们声明一个独占队列（仅限于此链接）
             * autoDelete: true 声明一个自动删除队列（服务器将在不使用它时删除，即队列的连接数为0）
             * arguments: 队列的其它属性（构造参数）
             */
            channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);
```

GitHub地址：[https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%9A%E9%AB%98%E7%BA%A7%E7%89%B9%E6%80%A7TTL%EF%BC%88%E8%BF%87%E6%9C%9F%E6%97%B6%E9%97%B4%EF%BC%89.md](https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%9A%E9%AB%98%E7%BA%A7%E7%89%B9%E6%80%A7TTL%EF%BC%88%E8%BF%87%E6%9C%9F%E6%97%B6%E9%97%B4%EF%BC%89.md)

