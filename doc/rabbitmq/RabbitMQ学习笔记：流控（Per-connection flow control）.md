### RabbitMQ学习笔记：流控（Per-connection flow control）

> 流控机制是用来避免消息发送速率过快而导致服务器难以支撑的情况。内存（memory）和磁盘（Disk
> space）达到阀值发出的警告相当于全局的流控，一旦触发会阻塞集群或者单机的所有连接（Connection）,这一块我上一篇博文已经讲解过了，而本节的流控是针对单个连接（Connection）的。

单个连接流控（Per-connection flow control）描述的是当发布服务（或发布服务组）向队列发送消息的速度快于队列处理速度时发生的情况，即：queue队列到达了性能瓶颈。

一个连接触发流控时会处于flow的状态，也就意味着这个Connection的状态每秒在blocked和unblocked之间来回切换数次，这样可以将消息发送的速率控制在服务器能够支撑的范围之内。

处于flow状态的Connection和处于running状态的Connection并没有什么不同，这个状态只是告诉系统管理员相应的发送速率受限了；而对于客户端而言，它看到的只是服务器的带宽要比正常情况下小一些。

流控制不只是作用于Connection，同样作用域Channel和queue，从Connection到Channel,再到queue，最后是消息持久化存储形成一个完整的流控链，对于处于流控链中的任意进程，只要该进程阻塞，上游的进程必定全部阻塞。也就是说，如果某个进程达到性能瓶颈，必然导致上游所有的进程被阻塞；

##### 流量控制组件

Connection、Channel、queue都有可能出现触发flow control state的情况，消息的流程如下：

NetWork -->Connection process -->Chnnel process --> Queue process -->Message store

##### 信用证

RabbitMQ的流控制机制是基于信用证（Credit）的拥塞控制机制实现的；

GitHub地址：[https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%9A%E6%B5%81%E6%8E%A7%EF%BC%88Per-connection%20flow%20control%EF%BC%89.md](https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%9A%E6%B5%81%E6%8E%A7%EF%BC%88Per-connection%20flow%20control%EF%BC%89.md)

