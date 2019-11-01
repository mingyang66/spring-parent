### RabbitMQ学习笔记：使用Docker部署RabbitMQ集群

RabbitMQ本身是基于Erlang编写，Erlang语言天生具备分布式、高并发的特性（通过同步Erlang集群各节点的magic cookie来实现）。

因此，RabbitMQ天生支持Clustering。这使得RabbitMQ本身不需要像ActiveMQ、Kafka那样通过Zookeeper分别来实现HA方案和保存集群的元数据。集群是保证可靠性的一种方式，同时可以通过水平扩展以达到增加消息吞吐量的能力。

> HA是High Available缩写，是双机集群系统简称，指高可用性集群，是保证业务连续性的有效解决方案，一般有两个或两个以上的节点，且分为活动节点及备用节点。

![三个节点架构图](https://img-blog.csdnimg.cn/20191030092359527.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhb21pbmd5YW5n,size_16,color_FFFFFF,t_70)

上图中采用三个节点组成了一个RabbitMQ集群，Exchange A(交换器)的元数据信息在所有的节点上是一致的，而Queue的完整数据则只会存在于它所创建的那个节点上，其它节点只知道这个queue的metadata信息和一个指向queue的owner node的指针。

##### RabbitMQ集群元数据的同步

RabbitMQ集群会始终同步四种类型的内部元数据（类似索引）：

- 队列元数据：队列名称和它的属性；
- 交换器元数据：交换器名称、类型和属性；
- 绑定元数据：一张简单的表格展示了如何将消息路由到队列；
- vhost元数据：为vhost内的队列、交换器和绑定提供命名空间和安全属性；

因此，当用户访问其中任何一个RabbitMQ节点时，通过rabbitmqctl查询到的queue、user、exchange、vhost等信息都是相同的。

##### 为何RabbitMQ集群仅采用元数据同步的方式

我想肯定会有不少同学会问，要想实现HA方案，那将RabbitMQ集群中的所有Queue的完整数据在所有节点上都保存一份不就可以了么？（可以类似MySQL的主从模式），这样子，任何一个节点出现故障或者跌机不可用时，那么使用者的客户端只要能连接至其他节点能够照常完成消息的发布和订阅。

##### 我想RabbitMQ的作者这么设计主要还是基于集群本身的性能和存储空间上来考虑。

> 第一，存储空间，如果每个集群节点都拥有所有Queue的完全数据拷贝，那么每个节点的存储空间会非常大，集群的消息积压能力会非常弱（无法通过集群节点的扩容提高消息积压能力）
>
> 第二，性能，消息的发布者需要将消息复制到每一个集群节点，对于持久化消息，网络和磁盘同步复制的开销都会明显的增加。

##### RabbitMQ集群发送/订阅消息的基本原理

RabbitMQ集群的工作原理如下图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191030104602367.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhb21pbmd5YW5n,size_16,color_FFFFFF,t_70)

##### 场景1：客户端直接连接队列所在节点

如果有一个消息生产者或者消息消费者通过amqp-client的客户端连接至节点1进行消息的发布或者订阅，那么此时的集群中的消息收发只与节点1相关，这个没有任何问题；如果客户端相连的是节点2或者节点3（队列1数据不在该节点上），那么情况又会是什么样呢？

##### 场景2：客户端连接的是非队列数据所在节点

如果消息生产者所连接的是节点2或者节点3，此时队列1的完整数据不在该两个节点上，那么在发送消息过程中这两个节点主要起了一个路由转发作用，根据这两个节点上的元数据（也就是上文提到的：指向queue的owner node的指针）转发至节点1上，最终发送的消息还是会存储至节点1的队列上。



同样，如果消息消费者所连接的节点2或者节点3，那这两个节点也会作为路由节点起到转发作用，将会从节点1的队列1中拉取消息进行消费。

##### 磁盘节点和RAM节点

一个节点可以是一个磁盘（disk）节点或者一个RAM节点（注意：磁盘和光盘可以互换使用）。RAM节点仅将内部数据库表存储在RAM中，这不包括消息，消息存储索引，队列索引和其它节点状态。



在大多数情况下你希望所有的节点都是磁盘（disk）节点，RAM节点是一种特殊情况，可用于提高队列、交换器或绑定交换频率比较高的性能。RAM节点不提供更高的发布/消费消息速率，如有疑问，请仅使用磁盘节点。



由于RAM节点仅将内部数据库表存储在RAM中，因此他们必须在启动时从对等节点同步它们。这意味着一个集群必须至少包含一个磁盘节点。因此，不可能手动删除集群中最后剩余的磁盘节点。

##### 1.下载rabbitmq镜像

```
docker pull rabbitmq:3.8-management
```

注意:使用后缀为“-management”的镜像版本，是包含网页控制台的。

##### 2.使用docker images命令查看下载的镜像

```
C:\Users\Administrator>docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
rabbitmq            3.8-management      5788d93cd8ad        14 hours ago        180 MB
centos              centos7             67fa590cfc1c        2 months ago        202 MB
```

##### 3.启动三个相互独立的RabbitMQ服务

```
docker run -d --hostname localhost --name myrabbit1 -p 15672:15672 -p 5672:5672 rabbitmq:3.8-management
docker run -d --hostname localhost --name myrabbit2 -p 15673:15672 -p 5673:5672 rabbitmq:3.8-management
docker run -d --hostname localhost --name myrabbit3 -p 15674:15672 -p 5674:5672 rabbitmq:3.8-management
```

这样我们就可以通过：http://ip:15672、http://ip:15673、http://ip:15674来访问各个单例服务；

##### 4.RabbitMQ集群安装

首先删除上一步骤中生成的三个RabbitMQ镜像集群，然后再操作如下指令，生成有三个节点的集群

```java
docker run -d --hostname rabbit1 --name myrabbit1 -p 15672:15672 -p 5672:5672 -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:3.8-management
    
docker run -d --hostname rabbit2 --name myrabbit2 -p 15673:15672 -p 5673:5672 --link myrabbit1:rabbit1 -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:3.8-management
    
docker run -d --hostname rabbit3 --name myrabbit3 -p 15674:15672 -p 5674:5672 --link myrabbit1:rabbit1 --link myrabbit2:rabbit2 -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:3.8-management
```

- -d: 后台进程运行
- hostname: RabbitMQ主机名称
- name:容器名称
- -p 15672:15672 访问HTTP API客户端，容器对外的接口和内部接口的映射
- -p 5672:5672 由不带TLS和带TLS的AMQP 0-9-1和1.0客户端使用

注意点：

1. 多个容器之间使用“--link”连接，此属性不能少；
2. Erlang Cookie值必须相同，也就是RABBITMQ_ERLANG_COOKIE参数的值必须相同，因为RabbitMQ是用Erlang实现的，Erlang Cookie相当于不同节点之间相互通讯的秘钥，Erlang节点通过交换Erlang Cookie获得认证。

##### 4.将RabbitMQ节点加入集群

设置节点1：

```
docker exec -it myrabbit1 bash
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl start_app
exit
```

设置节点2，加入到集群：

```
docker exec -it myrabbit2 bash
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl join_cluster --ram rabbit@rabbit1
rabbitmqctl start_app
exit
```

参数“--ram”表示设置为内存节点，忽略此参数默认为磁盘节点。

设置节点3，加入集群：

```
docker exec -it myrabbit3 bash
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl join_cluster --ram rabbit@rabbit1
rabbitmqctl start_app
exit
```

设置好之后，使用http:物理机IP:15672,默认账号密码是：guest/guest。

##### 5.RabbitMQ集群常用命令

查看集群的状态

```
rabbitmqctl cluster_status
```

停止节点

```
rabbitmqctl stop_app
```

启动节点

```
rabbitmqctl start_app
```

重置节点

```
rabbitmqctl reset
```



##### 6.从集群中删除节点

```
#停止节点
rabbitmqctl stop_app
#重置节点
rabbitmqctl reset
#启动节点
rabbitmqctl start_app
```

##### 7.我们可以远程删除节点，例如，在必须处理无响应的节点时，这很有用，例如可以删除rabbit@rabbit1节点从rabbit@rabbit2节点

```
# on rabbit1
rabbitmqctl stop_app
# on rabbit2
rabbitmqctl forget_cluster_node rabbit@rabbit1
```

注意：此时rabbit1仍然认为和rabbit2是在同一个集群，并且试图启动它将会报错，我们将会重置之后再重启。

```
# on rabbit1
rabbitmqctl start_app
# => Starting node rabbit@rabbit1 ...
# => Error: inconsistent_cluster: Node rabbit@rabbit1 thinks it's clustered with node rabbit@rabbit2, but rabbit@rabbit2 disagrees

rabbitmqctl reset
# => Resetting node rabbit@rabbit1 ...done.

rabbitmqctl start_app
# => Starting node rabbit@rabbit1 ...
# => ...done.
```

##### 8.集群节点重置（reset）

有时可能需要重置节点（擦除其所有数据），然后使其重新加入集群。一般来说，有两种可能情况：节点正在运行时，以及节点由于诸如 [ERL-430之](https://bugs.erlang.org/browse/ERL-430) 类的问题而无法启动或无法响应CLI工具命令时。



重置节点将删除其所有数据，集群成员信息，已配置的运行时参数，用户，虚拟主机以及任何其它节点数据。它还将从该群集中永久删除该节点。



要重置一个正在运行的响应节点，请首先使用rabbitmqctl stop_app停止RabbitMQ,然后使用rabbitmqctl reset对其进行重置：

```
# on rabbit1
rabbitmqctl stop_app
# => Stopping node rabbit@rabbit1 ...done.
rabbitmqctl reset
# => Resetting node rabbit@rabbit1 ...done.
```

对于无响应的节点，必须先使用任何必要的方法将其停止。对于无法启动的节点，情况也是如此。



已重置并重新加入其原始集群的节点将同步所有虚拟主机，用户，权限和拓扑（队列，交换，绑定），运行时参数和策略。如果选择托管副本，他可能会同步镜像队列的内容。重置节点上的非镜像队列内容将丢失。

##### 9.更改节点的类型disk|ram

```
# on rabbit3
rabbitmqctl stop_app
# => Stopping node rabbit@rabbit3 ...done.

rabbitmqctl change_cluster_node_type ram
# => Turning rabbit@rabbit3 into a ram node ...done.

rabbitmqctl start_app
# => Starting node rabbit@rabbit3 ...done.
```

