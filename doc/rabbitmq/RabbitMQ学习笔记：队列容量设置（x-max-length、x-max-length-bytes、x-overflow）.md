### RabbitMQ学习笔记：队列容量设置（x-max-length、x-max-length-bytes、x-overflow）

> rabbitmq可以设置队列的最大长度、队列最大容量及溢出后的处理逻辑

##### Max length

```
How many (ready) messages a queue can contain before it starts to drop them from its head.
(Sets the "x-max-length" argument.)
```

即：设置队列中可以存储处于ready状态消息的数量

```java
            /**
             * queue中可以存储处于ready状态的消息数量
             */
            arguments.put("x-max-length", 6);
```

##### Max length bytes

```
Total body size for ready messages a queue can contain before it starts to drop them from its head.
(Sets the "x-max-length-bytes" argument.)
```

即：队列中可以存储处于ready状态消息占用内存的大小(只计算消息体的字节数，不计算消息头、消息属性占用的字节数)

```java
            /**
             * queue中可以存储处于ready状态的消息占用的内存空间，单位：字节
             */
            arguments.put("x-max-length-bytes", 1024);
```

##### Overflow behaviour

```
Sets the queue overflow behaviour. This determines what happens to messages when the maximum length of a queue is reached. Valid values are drop-head, reject-publish or reject-publish-dlx. The quorum queue type only supports drop-head.
```

即：队列的处于ready状态存储消息的个数或消息占用的容量超过设定值后的处理策略

```java
            /**
             * queue溢出行为，这将决定当队列达到设置的最大长度或者最大的存储空间时发送到消息队列的消息的处理方式；
             * 有效的值是：
             * drop-head（删除queue头部的消息）、
             * reject-publish（最近发来的消息将被丢弃）、
             * reject-publish-dlx（拒绝发送消息到死信交换器）
             * 类型为quorum 的queue只支持drop-head;
             */
            arguments.put("x-overflow", "reject-publish");
```

```
The default behaviour for RabbitMQ when a maximum queue length or size is set and the maximum is reached is to drop or dead-letter messages from the front of the queue (i.e. the oldest messages in the queue). To modify this behaviour, use the overflow setting described below.
```

> x-overflow属性默认的处理策略是丢掉或者死信消息从队列的头部（也可以说是队列中最老的消息）

GitHub地址：[https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%9A%E9%98%9F%E5%88%97%E5%AE%B9%E9%87%8F%E8%AE%BE%E7%BD%AE%EF%BC%88x-max-length%E3%80%81x-max-length-bytes%E3%80%81x-overflow%EF%BC%89.md](https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%9A%E9%98%9F%E5%88%97%E5%AE%B9%E9%87%8F%E8%AE%BE%E7%BD%AE%EF%BC%88x-max-length%E3%80%81x-max-length-bytes%E3%80%81x-overflow%EF%BC%89.md)