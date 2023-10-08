### RabbitMQ学习笔记：Event Exchange Plugin（事件交换器插件）rabbitmq-event-exchange

>
客户端connection、channels、queues、consumers和系统其它部分自动生成的事件。例如，当一个connection被接受，虚拟主机通过了验证授权，将会发送一个connection_created事件，当一个connection关闭或者由于其它原因失败，将会发送一个connection_closed事件。RabbitMQ提供了一个最小的事件通知机制向RabbitMQ客户端公开。

##### rabbitmq-event-exchange插件

rabbitmq-event-exchange是一个消费内部事件并且重新发送到一个topic exchange的插件，因此可以展示事件给客户端应用程序。

为了消费事件，应用程序需要声明一个队列，并绑定到一个系统指定的交换器去消费消息。

插件在默认的虚拟主机上声明了一个topic类型的exchange(交换器)
amq.rabbitmq.event。所有的事件都会发送到这个exchange上并绑定一个路由键，比如，exchange.created、binding.deleted。所以你可以只订阅你关注的事件。

交换器的行为类似amq.rabbitmq.log，所有的信息都发布到这里，如果用户没有经过授权，你可以拒绝它们访问。

每个事件都有与之关联的各种属性，它们被转换成AMQP 0-9-1数据编码并插入到消息头中。消息的正文始终为空。

##### 启动插件

```
rabbitmq-plugins enable rabbitmq_event_exchange
```

##### 关闭插件

```
rabbitmq-plugins disable rabbitmq_event_exchange
```

##### 事件（Events）

RabbitMQ和相关插件通过routing keys发送事件：

### RabbitMQ Broker

Queue, Exchange and Binding events:

- queue.deleted
- queue.created
- exchange.created
- exchange.deleted
- binding.created
- binding.deleted

Connection and Channel events:

- connection.created
- connection.closed
- channel.created
- channel.closed

Consumer events:

- consumer.created
- consumer.deleted

Policy and Parameter events:

- policy.set
- policy.cleared
- parameter.set
- parameter.cleared

Virtual host events:

- vhost.created
- vhost.deleted

User related events:

- user.authentication.success
- user.authentication.failure
- user.created
- user.deleted
- user.password.changed
- user.password.cleared
- user.tags.set

Permission events:

- permission.created
- permission.deleted

### Shovel Plugin

Worker events:

- shovel.worker.status
- shovel.worker.removed

### Federation Plugin

Link events:

- federation.link.status
- federation.link.removed

##### 实战消息示例

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200106140940808.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9taW5neWFuZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

##### 集群中如何使用该插件

在集群中每个节点都要开启此插件，否则amq.rabbitmq.event交换器不可以正常的被创建。

参考：[https://www.rabbitmq.com/event-exchange.html](https://www.rabbitmq.com/event-exchange.html)
GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service)