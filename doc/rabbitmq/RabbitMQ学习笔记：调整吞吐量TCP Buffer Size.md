### RabbitMQ学习笔记：调整吞吐量TCP Buffer Size

> 调整吞吐量是一个共同的目标，可以通过增加TCP缓冲区大小，确保Nagle算法被禁用，启用可选的TCP功能和扩展来实现改进。
>
> 对于后两种情况，可以参考操作系统级优化部分。
>
> 请注意，吞吐量的调整将涉及权衡。例如，增加TCP缓冲区大小将增加每个连接使用的RAM大小，这可能使服务器RAM使用总量显著的增加。

##### TCP缓冲区大小

这是一个关键的可调参数。每个TCP连接都为其分配了缓冲区。一般来说，这些缓冲区越大，每个连接使用的RAM就越多，吞吐量也就越好。在linux上，操作系统默认情况下会自动调整TCP缓冲区大小，通常设置在80到120KB之间。



对于最大吞吐量，可以使用一组配置选项来增加缓冲区大小：

- AMQP 0-9-1和AMQP 1.0协议的 tcp_listen_options  
- MQTT协议的 mqtt.tcp_listen_options 
- STOMP协议的 stomp.tcp_listen_options 

请注意，增加TCP缓冲区大小将增加节点用于每个客户端连接的RAM大小。



以下示例将AMQP 0-9-1连接的TCP缓冲区设置为192KB。

```
tcp_listen_options.backlog = 128
tcp_listen_options.nodelay = true
tcp_listen_options.linger.on      = true
tcp_listen_options.linger.timeout = 0
tcp_listen_options.sndbuf = 196608
tcp_listen_options.recbuf = 196608
```

MQTT相同的示例：

```
mqtt.tcp_listen_options.backlog = 128
mqtt.tcp_listen_options.nodelay = true
mqtt.tcp_listen_options.linger.on      = true
mqtt.tcp_listen_options.linger.timeout = 0
mqtt.tcp_listen_options.sndbuf = 196608
mqtt.tcp_listen_options.recbuf = 196608
```

STOMP相同的示例：

```
stomp.tcp_listen_options.backlog = 128
stomp.tcp_listen_options.nodelay = true
stomp.tcp_listen_options.linger.on      = true
stomp.tcp_listen_options.linger.timeout = 0
stomp.tcp_listen_options.sndbuf = 196608
stomp.tcp_listen_options.recbuf = 196608
```

请注意，将发送和接收缓冲区大小设置为不同的值可能是危险的，不建议这么做。



##### Erlang VM I/O线程池

Erlang运行时使用线程池异步执行I/O操作。池的大小是通过RABBITMQ_IO_THREAD_POOL_SIZE环境变量配置的。变量是设置+A VM命令行标志的快捷方式，例如：+A 128

```
# reduces number of I/O threads from 128 to 32
RABBITMQ_IO_THREAD_POOL_SIZE=32
```

要直接设置，请使用RABBITMQ_SERVER_ADDITIONAL_ERL_ARGS环境变量：

```
RABBITMQ_SERVER_ADDITIONAL_ERL_ARGS="+A 128"
```

最近的RabbitMQ版本默认是128（之前是30）。建议具有8个或更多可用内核的节点使用高于96的值，即每个可用内核有12个或更多I/O线程。请注意，较高的值并不一定意味着由于等待I/O而导致更好的吞吐量或更低的CPU消耗。



##### 基于大量连接的调优方法

一些工作负载，通常被称为”物联网“，假设每个节点有大量客户端连接，每个节点的流量相对较低。传感器网络就是这样一种工作负载；可以部署数十万个或数百万个传感器，每几分钟发一次数据，对并发客户端的最大数量进行优化可能比总吞吐量更重要。



有几个因素可以限制单个节点可以支持多少并发连接：

- 最大数量的打开文件句柄（包括套接字）以及其他内核强制资源限制
- 每个连接使用的RAM数量
- 每个连接使用的CPU资源量
- 最大的Erlang进程数量VM被配置为允许



##### 打开文件句柄限制

大多数操作系统限制可以同时打开的文件句柄的数量。当操作系统进程（如：RabbitMQ的Erlang VM）达到极限时，它将无法打开任何新文件或接受任何更多的TCP连接。



配置限制的方式因操作系统和分布而异，例如：取决于是否使用systemd。对于Linux,在我们的Debian和RPM安装指南中提供了Linux上的控制系统限制。Linux内核限制管理包含在Web上的许多资源中，包括开放文件句柄限制。

对于Docker,主机中的Docker守护进程配置文件控制限制。

MacOS使用类似的系统。

在windows上，使用ERL_MAX_PORTS环境变量控制Erlang运行时的限制。

优化并发连接数时，请确保系统有足够的文件描述符，不仅支持客户端连接，而且支持节点可能使用的文件。要计算ballpark限制，请将每个节点的连接数乘以15。

例如，要支持100000个连接，请将限制设置为150000。

增加限制会略微增加RAM空闲机器的使用量，但这是一个合理的权衡。



##### 每个连接内存消耗：TCP Buffer Size

对于并发客户端连接的最大数量，可以使用一组配置选项来减少TCP缓冲区大小：

- AMQP 0-9-1和AMQP 1.0协议的 tcp_listen_options 选项
- MQTT协议的  mqtt.tcp_listen_options 选项
- STOMP协议的 stomp.tcp_listen_options 选项

减少TCP缓冲区大小将减少节点用于每个客户端连接的RAM数量。

在每个节点的并发连接数比吞吐量更重要的环境中，这通常是必要的。

以下示例将AMQP 0-9-1连接的TCP缓冲区设置为32KiB:

```
tcp_listen_options.backlog = 128
tcp_listen_options.nodelay = true
tcp_listen_options.linger.on      = true
tcp_listen_options.linger.timeout = 0
tcp_listen_options.sndbuf  = 32768
tcp_listen_options.recbuf  = 32768
```

MQTT协议相同的示例：

```
mqtt.tcp_listen_options.backlog = 128
mqtt.tcp_listen_options.nodelay = true
mqtt.tcp_listen_options.linger.on      = true
mqtt.tcp_listen_options.linger.timeout = 0
mqtt.tcp_listen_options.sndbuf  = 32768
mqtt.tcp_listen_options.recbuf  = 32768
```

STOMP协议相同的示例：

```
stomp.tcp_listen_options.backlog = 128
stomp.tcp_listen_options.nodelay = true
stomp.tcp_listen_options.linger.on      = true
stomp.tcp_listen_options.linger.timeout = 0
stomp.tcp_listen_options.sndbuf  = 32768
stomp.tcp_listen_options.recbuf  = 32768
```

请注意，降低TCP缓冲区大小将导致按比例的吞吐量下降，因此需要为每个工作负载找到吞吐量和每个连接RAM使用之间的最佳值。

将发送和接收缓冲区大小设置为不同的值是危险的，不建议这样做。不建议低于8KiB的值。



##### 限制连接上的信道数

信道也消耗RAM。通过优化应用程序使用的信道数量，可以减少该数量。可以使用信道最大配置设置来限制连接上的最大通道数：

```
channel_max = 16
```

注意，一些构件在RabbitMQ客户机上的库和工具可能隐式地需要一定数量的信道。很少需要大于200的值。找到一个最佳值通常是一个反复试验的问题。

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service)