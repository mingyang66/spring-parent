### RabbitMQ学习笔记：端口号解析（Port Access）

##### 1.端口4369：epmd,RabbitMQ节点和CLI工具使用的对等发现服务端口

EPMD与节点间相互通信

epmd(Erlang Port Mapping Daemon)是一个小的附加守护进程，与每个RabbitMQ节点一起运行，运行时使用它来发现特定节点监听端口。然后，对等节点和CLI工具使用该端口。

当一个节点或CLI工具需要连接节点rabbit@hostname2时，它将执行以下操作：

- 使用标准操作系统解析器或inetrc文件中指定的自定义解析程序将hostname2解析为IP4或IPv6地址
- 使用上述地址联系在hostname2上运行的epmd
- 向epmd询问rabbit节点使用的端口号
- 使用解析的IP地址和发现的端口号连接节点
- 继续沟通联系

##### 2.端口5672,5671（ used by AMQP 0-9-1 and 1.0 clients without and with TLS ）由AMQP0-9-1和1.0客户机使用，不带和带TLS

AMQP是Advanced Message Queuing Protocol的缩写，一个提供统一消息服务的应用层标准高级消息队列协议，是应用层协议的一个开放标准，专为面向消息的中间件设计。基于此协议的客户端与消息中间件之间可以传递消息，并不受客户端/中间件不同产品，不同的开发语言等条件的限制，Erlang中的实现有RabbitMQ等。

##### 3.端口25672

用于节点间和CLI工具通信（Erlang分发服务器端口），并且是从动态范围分配（默认情况下限制为一个端口，计算为AMQP端口+20000）。除非确实需要这个端口上的外部连接（例如：在子网络之外的计算机上使用联合或CLI工具的集群），否则不应公开这个端口。

##### 4.端口15672

通过 http://serverip:15672 访问 RabbitMQ 的 Web 管理界面，默认用户名密码都是 guest。（注意：RabbitMQ 3.0之前的版本默认端口是55672，不同）。

也可以通过此端口访问HTTP API接口。





