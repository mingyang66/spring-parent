### RabbitMQ学习笔记：Connections、Channels、Queues之state状态

##### 连接、信道、队列状态如下：

|    Name     | running(运行中) | flow（流控） | idle（空闲） | blocked(阻塞) | unblocked(未阻塞) |
| :---------: | :-------------: | :----------: | :----------: | :-----------: | :---------------: |
| Connections |      true       |     true     |     true     |     true      |       true        |
|  Channels   |      true       |     true     |     true     |     true      |       true        |
|    Queue    |      true       |     true     |     true     |     true      |       true        |

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service)