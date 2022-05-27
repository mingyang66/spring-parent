### RabbitMQ学习笔记：addReturnListener监听回退消息

##### 发布消息示例

```java
channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, true, false, properties.build(), message.getBytes());
```

参数：

- mandatory:如果为true, 消息不能路由到指定的队列时，则会调用basic.return方法将消息返回给生产者,会触发addReturnListener注册的监听器；如果为false，则broker会直接将消息丢弃
- immediate:如果为true,当exchange将消息路由到queue时发现queue上没有消费者，那么这条消息不会放入队列中，该消息会通过basic.return方法返还给生产者。

> 在RabbitMQ3.0以后的版本里去掉了immediate参数的支持，发送带immediate=true的publish会返回如下错误， com.rabbitmq.client.AlreadyClosedException: connection is already closed due to connection error; protocol method: #method<connection.close>(reply-code=540, reply-text=NOT_IMPLEMENTED - immediate=true, class-id=60, method-id=40)
>
> 为什么会取消immediate参数支持，immediate标记会影响镜像队列性能，增加代码复杂性，并建议采用TTL和DLX等方式代替

##### 生产者接收退回的消息

提供了两个接收回退消息的监听器方法，实现的方式基本一样，第二个更简洁一些，推荐使用第二种方法：

```
void addReturnListener(ReturnListener listener)
```


```java
ReturnListener addReturnListener(ReturnCallback returnCallback)

```

第一种方案示例：
```java
channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) -> {
                System.out.println("第一个监听器执行了---");
            });
```

第二种方案示例（是不是很简介）：
```java
            channel.addReturnListener((returnMessage)-> {
                System.out.println("第二个监听器执行了---");
            });
```

GitHub地址：[https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%9AaddReturnListener%E7%9B%91%E5%90%AC%E5%9B%9E%E9%80%80%E6%B6%88%E6%81%AF.md](https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%9AaddReturnListener%E7%9B%91%E5%90%AC%E5%9B%9E%E9%80%80%E6%B6%88%E6%81%AF.md)
