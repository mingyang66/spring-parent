### RabbitMQ学习笔记：消费者预取消息（basic.qos）

> 消费者预取是信道预取机制的扩展

AMQP 0-9-1协议指定basic.qos方法，以便在使用时限制channel（connection）上未确认的消息数（也称之为“prefetch count”）。



不幸的是，信道不是这方面的理想范围，因为一个信道可能消费来自多个队列的信息，信道和队列需要为发送的每个消息互相协助，以确保他们不会超过限制。这在单台机器上会很慢，在集群中使用会更慢。



此外，对于许多用途，更自然的做法是指定适用于每个使用者的预取计数。



因此RabbitMQ重新定义了basic.qos全局标识的含义，默认是false。

| global | prefetch_count在协议AMQP 0-9-1中的意义 | prefetch_count在RabbitMQ中的意义 |
| ------ | -------------------------------------- | -------------------------------- |
| false  | 在channel上所有消费者共享              | 分别应用于channel上的每个新用户  |
| true   | 在connection上所有的消费者共享         | 在channel上的所有消费者中共享    |



##### 单一消费者

Java中的以下基本示例将同时接收最多10个未确认消息：

```java
Channel channel = ...;
Consumer consumer = ...;
channel.basicQos(10); // Per consumer limit
channel.basicConsume("my-queue", false, consumer);
```

##### 独立消费者

此示例启动同一信道上的两个消费者，每个消费者将一次独立接收最多10个未确认消息：

```java
Channel channel = ...;
Consumer consumer1 = ...;
Consumer consumer2 = ...;
channel.basicQos(10); // Per consumer limit
channel.basicConsume("my-queue1", false, consumer1);
channel.basicConsume("my-queue2", false, consumer2);
```

##### 多个消费者共享限制

AMQP 0-9-1规范没有解释如果使用不同的全局值多次调用basic.qos会发生什么情况，RabbitMQ将其解释为这两个预取限制应相互独立地执行；只有在未达到对未确认消息的限制时，消费者才会收到新消息。

示例：

```java
Channel channel = ...;
Consumer consumer1 = ...;
Consumer consumer2 = ...;
channel.basicQos(10, false); // Per consumer limit
channel.basicQos(15, true);  // Per channel limit
channel.basicConsume("my-queue1", false, consumer1);
channel.basicConsume("my-queue2", false, consumer2);
```

这两个消费者之间只有15个未确认的消息，每个用户最多有10条消息，这将比上面的示例慢，因为在信道和队列之间进行协调以实施全局限制会增加额外的开销。