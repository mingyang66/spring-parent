### RabbitMQ学习笔记：消息追踪Firehose功能详解

> 在使用任何消息中间件的过程中，难免会出现消息异常丢失的情况。对于RabbitMQ而言，可能是生产者与Broker断开了连接并且没有任何重试机制；也可能是消费者在处理消息时发生了异常，不过却提前进行了ack;甚至是交换器没有与任何队列绑定，生产者感知不到或者没有采取相应的措施；另外RabbitMQ本身的集群策略也可能导致消息的丢失，这个时候就需要有一个良好的机制来跟踪记录消息的投递过程，以此来协助开发或者运维人员快速的定位问题。

##### Firehose

在RabbitMQ中可以使用Firehose功能来实现消息追踪，Firehose可以记录每一次发送或者消费消息的记录，方便RabbitMQ的使用者进行调试、排错等。

Firehose的原理是将生产者投递给RabbitMQ的消息，或者RabbitMQ投递给消费者的消息按照指定的格式发送到默认的交换器上。这个默认交换器的名称是amq.rabbitmq.trace，它是一个topic类型的交换器。发送到这个交换器上的消息路由键是：

- publish.{exchangename},其中exchangename为交换器名称，对应生产者投递到交换器的消息
- deliver.{queuename}，其中queuename为队列名称，对应消费者从队列获取的消息



开启Firehose功能命令：

```
rabbitmqctl trace_on [-p vhost]
```

> [-p vhost]是可选参数，用来指定虚拟主机vhost

关闭Firehose功能命令：

```
rabbitmqctl trace_off [-p vhost]
```

> Firehose默认情况下处于关闭状态，并且Firehose的状态也是非持久化的，会在RabbitMQ服务重启的时候还原成默认状态。Firehose开启之后多少会影响RabbitMQ整体服务性能，因为它会引起额外的消息生成、路由和存储。

##### Firehose使用示例

首先要做一下准备工作：

- 创建队列test_queue、queue1、queue2、queue3、queue4、queue5
- 创建交换器test_exchange
- 通过路由键绑定test_exchange交换器和test_queue队列
- 将amq.rabbitmq.trace和queue1、queue2、queue3、queue4、queue5队列绑定





![在这里插入图片描述](https://img-blog.csdnimg.cn/20191220163128198.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9taW5neWFuZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

RabbitMQ management管理页面示例图：

队列示例图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191220163322953.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9taW5neWFuZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

交换器示例图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191220163414379.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9taW5neWFuZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

使用客户端向交换器exchange中发送消息，然后可以观察到队列test_queue、queue1、queue2、queue3、queue4、queue5中都接收到了一条消息。



解释amq.rabbitmq.trace这个默认追踪交换器绑定队列的路由键：

- "publish.#"匹配发送至所有交换器的消息
- “deliver.#"匹配消费所有队列的消息
- “#”包含“publish.#"和“deliver.#"
- "publish.test_exchange"匹配发送到指定交换器的消息
- “deliver.test_queue"匹配消费指定队列的消息

在Firehose开启状态，当有客户端发送或者消费 消息时，Firehose会自动封装相应的消息体，并添加详细的headers属性。看如下发送至交换器的消息体内容：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191220165244355.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9taW5neWFuZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

在消费队列时，会将这条消息封装成如下图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191220165639771.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9taW5neWFuZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

> headers中的exchange_name表示发送此条消息的交换器；routing_keys表示与exchange_name对应的路由键列表；properties表示消息本身的属性，比如delivery_mode设置为2表示消息需要持久化处理。