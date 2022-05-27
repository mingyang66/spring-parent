### RabbitMQ学习笔记：消费者优先级（priority）

##### 概述

消费者优先级允许你确保高优先级消费者活动（非阻塞）的时候接收消息，低优先消费者只有在高优先级消费者阻塞的时候接收消息。



通常，连接到队列上的活动消费者以循环的方式从队列接收消息。当消费者使用优先级，如果多个活动消费者存在，且具有相同的优先级，则消息轮流传递。



##### 活动消费者定义

上述段落指的是消费者处于活动状态或受阻状态。在任何时候，给定的消费者不是活动状态就是阻塞状态，一个活动的消费者是可以不需要任何等待就接收消息；一个消费者如果不可以接收消息就会阻塞，因为它的信道在发送basic.qos之后已经达到了最大数量的未确认消息，或者是因为网络阻塞。



对于队列，以下三种情况肯定有一个为true:

1.  There are no active consumers (队列没有活动状态的消费者)
2.  The queue is empty （队列为空）
3.  The queue is busy delivering messages to consumers （队列正在发送消息给消费者）

消费者每秒钟可以在活动和阻塞状态之间切换多次，我们不会公开消费者是活动或者阻塞通过管理器插件或者rabbitmqctl。

当消费者使用优先级时，你期望最高优先级的消费者接收所有的消息直到变为阻塞，这时候低优先级的消费者开始接收一些消息。如果这里存在一个活动低优先级的消息处于ready状态，RabbitMQ仍将会按优先级传递消息，它不会等待一个高优先级阻塞消费者变为非阻塞消费者。



##### 使用消费者优先级

设置x-priority参数给basic.consume方法，值是一个整数值。未指定值的消费者默认是0，数字越大优先级越高，可以使用正数和负数。

```java
Channel channel = ...;
Consumer consumer = ...;
Map<String, Object> args = new HashMap<String, Object>();
args.put("x-priority", 10);
channel.basicConsume("my-queue", false, args, consumer);
```

GitHub地址：[https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%9A%E6%B6%88%E8%B4%B9%E8%80%85%E4%BC%98%E5%85%88%E7%BA%A7%EF%BC%88priority%EF%BC%89.md](https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%9A%E6%B6%88%E8%B4%B9%E8%80%85%E4%BC%98%E5%85%88%E7%BA%A7%EF%BC%88priority%EF%BC%89.md)
