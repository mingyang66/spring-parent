### RabbitMQ学习笔记：死信交换机DLX

> DLX，即Dead Letter Exchange死信交换机

##### 概述

队列中的消息可能会变成死信消息（dead-lettered）,当发生如下事件时会导致消息变为死信：

- rejected： the message was rejected with requeue parameter set to false (消息被消费者使用basic.reject或basic.nack方法，并且requeue参数设置为false,通过这种方式进行消息确认)
- expired:  the [message TTL](https://www.rabbitmq.com/ttl.html) has expired （消息过期）
- maxlen: the [maximum allowed queue length](https://www.rabbitmq.com/maxlength.html) was exceeded （消息由于队列长度及容量限制被丢弃的消息）

##### DLXs(Dead Letter Exchanges)

死信交换器就是普通的交换器，它们可以是任何我们常见的类型，并像往常声明一样。DLX可以通过queue队列的参数来定义，也可以通过policy的方式来定义。

##### 死信交换器的使用方式

- 声明一个交换器

```java
            channel.exchangeDeclare("some.exchange.name", BuiltinExchangeType.DIRECT);
```

- 声明队列时需要设置的参数(此队列不是死信队列)

```java
            Map<String, Object> arguments = Maps.newHashMap();
            /**
             * 设置消息发送到队列中在被丢弃之前可以存活的时间，单位：毫秒
             */
            arguments.put("x-message-ttl", 10*60*1000);
            /**
             * 设置一个队列多长时间未被使用将会被删除，单位：毫秒
             */
            //arguments.put("x-expires", 15*60*1000);
            /**
             * queue中可以存储处于ready状态的消息数量
             */
            arguments.put("x-max-length", 6);
            /**
             * queue中可以存储处于ready状态的消息占用的内存空间
             */
            arguments.put("x-max-length-bytes", 1024);
            /**
             * queue溢出行为，这将决定当队列达到设置的最大长度或者最大的存储空间时发送到消息队列的消息的处理方式；
             * 有效的值是：drop-head（删除queue头部的消息）、reject-publish（拒绝发送来的消息）、reject-publish-dlx（拒绝发送消息到死信交换器）
             * 类型为quorum 的queue只支持drop-head;
             */
            arguments.put("x-overflow", "reject-publish");
            /**
             * 死信交换器，消息被拒绝或过期时将会重新发送到的交换器
             */
            arguments.put("x-dead-letter-exchange", "some.exchange.name");
            /**
             * 当消息是死信时使用的可选替换路由
             */
            arguments.put("x-dead-letter-routing-key", "some-routing-key");
```

- 声明死信队列

```java
channel.queueDeclare("some.queue.name", true, false, false, null);
```

- 死信队列、交换器、路由绑定

```java
channel.queueBind("some.queue.name", "some.exchange.name", "some-routing-key");
```

##### 路由死信消息（Routing Dead-Lettered Messages）

死信消息将会被路由到它们的死信交换器

- 使用其所在队列指定的死信交换器路由，或者，没有设置此项
- 与最初发布时使用的路由相同

例如：如果你发布一条消息到交换器，使用的路由是foo;但是这条消息是一个死信，它将发送消息到路由是foo的死信交换器;如果队列最初声明的时候已经设置了 x-dead-letter-routing-key 的值为bar,那么消息将会使用路由bar发布到死信交换器上。

​		注意，如果队列没有设置死信路由关键字，那消息被死信路由时将会使用它自身的原始路由关键字。这包含了CC和BCC头参数设置的路由的关键字。



 当死信消息被重新发送时，消息确认机制也会在内部被开启，因此，在原始队列删除这条消息之前，消息最终到达的队列—死信队列必须确认该消息。**换句话说，发送队列在接收到死信队列的确认消息之前不会删除原始消息**。注意，如果在特殊情况下服务器宕机，那么同样的消息将会在原始队列和死信队列中同时出现。 



 消息的死信路由可能会形成一个循环。比如，一个队列的死信的消息没有使用指定的死信路由关键字被发送到默认的交换机时。消息在整个循环(消息到达同一个队列两次)中没有被拒绝，那么消息将被丢弃。 

##### 死信对消息的影响（Dead-Lettered Effects on Messages）

死信消息修改了它的头部信息：

- 交换机的名称被修改为最后的死信交换机
- 路由关键字可能被改为队列指定的死信路由关键字
- 如果以上发生，名为CC的头参数将被删除
- 名为BCC的头参数将被删除

**进行死信路由时，会给每个死信消息的头部增加一个名为x-death的数组。这个数组包含了过于每次死信路由的信息实体，通过一个键值对{queue, reason}区分**。每个实体是一张表，包含了一下的字段信息：

- queue：消息称为死信时所在队列的名称
- reason：消息成为死信的原因
- time：消息成为死信的时间，是一个64位的时间戳
- exchange：消息被发送的交换机(当消息多次称为死信消息时，该值为死信交换机)
- routing-keys: 消息被发送时使用的路由关键字，包含了CC关键字但是不包含BCC
- count：在该队列中消息由于这个原因被死信路由的次数
- original-expiration(如果消息时因为消息的TTL称为死信时，有该值)：消息的原始过期时间属性，这个值在消息被死信路由时将被移除，为了避免消息在其他队列中再次过期

**新的信息实体将被发送x-death数组的首位，如果该数组中已经存在同样的队列和同样的死信原因的信息实体，那么该实体的count字段将加1并且实体被移到数组的首位。**

reason这个属性的值表示了消息变为死信的原因，有以下几种：

- rejected：消息被消息者拒绝并且requeue参数值为false
- expired：消息因为消息的TTL过期
- maxlen：超过了队列允许的最大长度

当进行消息进行首次路由时，将添加三个顶级的头信息：

- x-first-death-reason
- x-first-death-queue
- x-first-death-exchange

它们与消息进行首次死信路由时，设置的reason, queue, exchange字段值相同。**一旦添加，它们值将不会再被修改。**

GitHub地址：[https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%9A%E6%AD%BB%E4%BF%A1%E4%BA%A4%E6%8D%A2%E6%9C%BADLX.md](https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%9A%E6%AD%BB%E4%BF%A1%E4%BA%A4%E6%8D%A2%E6%9C%BADLX.md)