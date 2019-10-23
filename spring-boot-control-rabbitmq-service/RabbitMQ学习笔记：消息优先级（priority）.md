### RabbitMQ学习笔记：消息优先级（priority）

##### 概括

> RabbitMQ在版本3.5.0中有优先级队列的实现，任何队列可以通过设置可选参数x-max-priority转换为优先级队列；这个参数应该是1到255之间的正整数，推荐设置1到10之间的数值，表示队列应该支持的最大优先级；

##### 声明优先级队列

```java
Channel ch = ...;
Map<String, Object> args = new HashMap<String, Object>();
args.put("x-max-priority", 10);
ch.queueDeclare("my-priority-queue", true, false, false, args);
```

声明优先级队列之后发布者可以使用priority属性发布优先级消息，数字越大代表优先级越高

##### 声明优先级消息

> 消息优先级priority字段定义为一个无符号byte,所以实际的优先级应该在0到255之间；消息如果没有设置priority优先级字段，那么priority字段值默认为0；如果优先级队列priority属性被设置为比x-max-priority大，那么priority的值被设置为x-max-priority的值。

```java
AMQP.BasicProperties.Builder properties = MessageProperties.PERSISTENT_TEXT_PLAIN.builder();

int priority = RandomUtils.nextInt(0, 11);
/**
* 设置消息的优先级
*/
properties.priority(priority);
/**
* 发布消息
* 发布到不存在的交换器将导致信道级协议异常，该协议关闭信道，
* exchange: 要将消息发送到的交换器
* routingKey: 路由KEY
* props: 消息的其它属性，如：路由头等
* body: 消息体
*/
channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, true, properties.build(), (priority+":"+message).getBytes());
```

##### 消费者使用方式（Interaction with Consumers）

了解消费者在处理优先级队列时的工作方式很重要。默认情况下，用户在确认任何消息之前可能会收到大量消息，仅受网络压力的限制。



因此如果这样一个饥渴的消费者连接到一个空队列，消息随后将被发布到该队列，则消息可能根本不会在队列中等待任何时间。在这种情况下，优先级队列将没有机会对它们进行优先级排序。



在大多数情况下，你需要在用户的手动确认模式下使用basic.qos方法，以限制可以随时发送的消息数量，从而允许对消息进行优先级排序

##### 优先级队列的其它特性

一般来说，优先级队列具有标准RabbitMQ队列的所有特性，他们支持持久化，分页、镜像等等。



应该过期的消息仍然只会从队列的头过期，这意味着，与普通队列不同，即使每个队列设置TTL也会导致过期的低优先级消息卡在未过期高优先级消息后面。这些消息将永远不会被传递，但他们将出现在队列统计信息中。



设置了最大长度的队列将像其它队列一样，从队列头部丢弃消息以强制执行该限制。这意味着高优先级的消息可能会被丢弃，以便为低优先级的消息让路，这可能不是你所期望的。