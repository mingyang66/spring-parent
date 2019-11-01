### RabbitMQ学习笔记：高可用性（镜像）队列

##### 什么是镜像队列

默认情况 下，RabbitMQ集群中队列的内容位于单个节点（声明该队列的节点）上。这与交换器和绑定相反，交换器和绑定始终可以被视为在所有节点上。可以选择使队列跨多个节点进行镜像。



每个镜像队列由一个主服务器和一个或多个镜像组成。主节点托管在一个通常称为主节点的节点上。每个队列都有其自己的主节点。给定队列的所有操作都首先应用于队列的主节点，然后传播到镜像。这涉及排队发布，向消费者传递消息，跟踪来自消费者的确认等。



队列镜像意味着节点的集群。因此，不建议在WAN中使用它（当然，客户端仍然可以根据需要进行远近连接）。



发布到队列的消息将复制到所有镜像。消费者连接到主服务器，无论它们连接到那个节点，镜像都会丢弃已在主服务器上确认的消息。因此，队列镜像可以提高可用性，但不会在节点之间分配负载（所有参与节点均完成所有工作）。



如果承载队列主服务器的节点发生故障，则最早的镜像将在同步后提升为新的主服务器。根据队列镜像参数，也可以升级不同步的镜像参数。

##### 镜像的配置方式

| ha-mode | ha-params |                        Desc                        |
| :-----: | :-------: | :------------------------------------------------: |
|   all   |   忽略    |  all表示镜像到集群上的所有节点，ha-params参数忽略  |
| exactly | 节点数量  | exactly表示镜像到设置数量的节点，ha-params节点数量 |
|  nodes  | 节点列表  |  nodes表示镜像到指定节点列表上，ha-params节点列表  |

设置镜像队列策略：

```
##匹配所有的队列
rabbitmqctl set_policy ha-all "^" '{"ha-mode":"all"}'
```

```
##匹配所有以test开头的队列
rabbitmqctl set_policy ha-all "^test" '{"ha-mode":"all"}'
```

> 除了使用CLI命令行工具设置策略外，还可以通过Management UI控制台来设置。

##### 同步镜像和非同步镜像

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191101093116756.png)

设置完策略之后，我们可以看到Queues页面中镜像队列的Node属性有两个标识，一个是+1（Synchronised mirrors:rabbit@rabbit1,即同步镜像节点数量），一个是+1(Unsynchronised mirrors:rabbit@rabbit1,rabbit@rabbit2，即非同步节点镜像数量);很显然表示当前节点队列对应的同步节点数量，和非同步节点数量。

默认情况下如果一个新的节点加入集群成为镜像节点，新节点中没有任何数据，而其它的节点是有数据的，这时候集群中其它节点的数据不会同步到新的节点，只有新发布到集群中的消息才会同步到新节点中，这样集群队列尾部的消息不断的同步到新节点，头部消息慢慢消费掉，新节点也会变成同步节点。



这里会有一个疑问？新加入的节点为何默认是非同步节点？

```
A newly added mirror provides no additional form of redundancy or availability of the queue's contents that existed before the mirror was added, unless the queue has been explicitly synchronised. Since the queue becomes unresponsive while explicit synchronisation is occurring, it is preferable to allow active queues from which messages are being drained to synchronise naturally, and only explicitly synchronise inactive queues.
```

新加入集群的镜像队列不会主动的同步集群中其它节点的老数据，除非队列显式同步。由于队列显式同步时队列变得无响应，因此最好允许新发布的消息自动同步，老消息无需同步。

```
When enabling automatic queue mirroring, consider the expected on disk data set of the queues involved. Queues with a sizeable data set (say, tens of gigabytes or more) will have to replicate it to the newly added mirror(s), which can put a significant load on cluster resources such as network bandwidth and disk I/O. This is a common scenario with lazy queues, for example.
```

启用自动同步镜像时，请考虑所涉及队列的预期磁盘上数据集具有大量数据集（例如：数10GB或更大）的队列将不得不将其复制到新添加的镜像中，这可能会给集群资源（如：网络带宽和磁盘I/O）带来很大的负载。

#####  ha-sync-mode 参数设置

 ha-sync-mode 参数是用来控制新加入集群群的镜像节点是否自动同步镜像队列中的消息；

 默认情况下ha-sync-mode=manual ，表示镜像队列中的消息不会主动的同步到新节点，除非显式调用命令。当调用同步命令后，队列开始阻塞，无法对其进行操作，直到同步完成；



当 ha-sync-mode=automatic 时，新加入节点时会默认同步已知的镜像队列。同步过程中所有的消息都会被阻塞，直到同步完成。



查看集群中哪些节点已经完成了同步，哪些未完成同步

```
rabbitmqctl list_queues name slave_pids synchronised_slave_pids
```

结果：

```
root@rabbit1:/# rabbitmqctl list_queues name slave_pids synchronised_slave_pids
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name    slave_pids      synchronised_slave_pids
test_queue      [<rabbit@rabbit1.3.503.0>, <rabbit@rabbit3.3.9913.0>]   [<rabbit@rabbit1.3.503.0>]
```

- name:队列名称

- slave_pids：表示已经同步过的节点
- synchronised_slave_pids：未同步的节点

未同步的节点可以通过如下命令手工同步：

```
rabbitmqctl sync_queue {name}
```

结果：

```
root@rabbit1:/# rabbitmqctl sync_queue test_queue
Synchronising queue 'test_queue' in vhost '/' ...
root@rabbit1:/# rabbitmqctl list_queues name slave_pids synchronised_slave_pids
Timeout: 60.0 seconds ...
Listing queues for vhost / ...
name    slave_pids      synchronised_slave_pids
test_queue      [<rabbit@rabbit1.3.503.0>, <rabbit@rabbit3.3.9913.0>]   [<rabbit@rabbit3.3.9913.0>, <rabbit@rabbit1.3.503.0>]
```

同样也可以取消队列的同步：

```
rabbitmqctl cancel_sync_queue {name}
```

#####  **ha-promote-on-shutdown** 主节点选择参数配置

表示主动停止主节点的服务室，其它节点如何替代主节点，选取主节点的行为，是在可用性和可靠性之间做出的权衡的选择；

-  **ha-promote-on-shutdown** 有两个值when-synced、always，默认是when-synced,在可控的master关闭时（比如停止RabbitMQ服务或者关闭操作系统），RabbitMQ会拒绝故障恢复到一个非同步的slave镜像，也就是拒绝把一个非同步的slave提升成新的master。只有在非可控的master关闭时（比如server crash,断网），才会故障恢复到一个非同步的slave镜像。
- 当取值为always时，则在所有情况下，都不会拒绝故障恢复到非同步的slave镜像。

##### ha-promote-on-failure故障转移参数配置

表示异常情况下其它节点如何替代主节点，比如Crash、断网等，默认参数always,参数值意思跟ha-promote-on-shutdown一样

