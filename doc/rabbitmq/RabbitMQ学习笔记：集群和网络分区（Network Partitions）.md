### RabbitMQ学习笔记：集群和网络分区（Network Partitions）

> 集群成员之间的网络连接故障会影响客户机操作的数据一致性和可用性（如CAP定理）。由于不同的应用程序对一致性有不同的要求，并且对不可用性的容忍程度不同，所以可以使用不同的的分区处理策略。

##### 1.检测网络分区

如果一个节点在一段时间内（默认是60秒）无法与对等端节点联系，则节点将认为其对等节点是否已关闭。如果两个节点重新接触，都认为另一个已关闭，则这些节点将确定已发生分区。将以如下形式记录到RabbitMQ日志：

```
2020-05-18 06:55:37.324 [error] <0.341.0> Mnesia(rabbit@warp10): ** ERROR ** mnesia_event got {inconsistent_database, running_partitioned_network, rabbit@hostname2}
```

分区状态判定：可以通过服务器日志、HTTP API(用于监视)和CLI命令进行标识：

```
rabbitmq-diagnostics cluster_status
```

rabbitmq-diagnostics cluster_status通常会为分区显示一个空列表（经过实际的测试会显示如：Node rabbit@rabbit1 cannot communicate with rabbit@rabbit2）：

官网示例：

```
rabbitmq-diagnostics cluster_status
# => Cluster status of node rabbit@warp10 ...
# => Basics
# =>
# => Cluster name: local.1
# =>
# => ...edited out for brevity...
# =>
# => Network Partitions
# =>
# => (none)
# =>
# => ...edited out for brevity...
```

分区状态查看：如果网络分区已经发生，则分区信息将按展示如下信息：

```
rabbitmqctl cluster_status
# => Cluster status of node rabbit@warp10 ...
# => Basics
# =>
# => Cluster name: local.1
# =>
# => ...edited out for brevity...
# =>
# => Network Partitions
# =>
# => Node flopsy@warp10 cannot communicate with hare@warp10
# => Node rabbit@warp10 cannot communicate with hare@warp10
```

HTTP API将返回 GET /api/nodes端点中分区下的每个节点信息；如果发生分区，web管理页面上将显示警告信息；

##### 2.网络分区期间的行为

当网络分区发生后，两个（或更多个）分区双方都认为对方已经崩溃。这种情况被称为分裂集群（split-brain），队列、绑定、交换器可以单独创建或删除。

经典的镜像队列将在分区的每一次都有一个master队列，而且两侧都独立运行。

除非配置分区处理策略（如：pause_minority），否则即使在网络连接恢复后分区仍将继续存在（因为默认策略是ignore）。

##### 3.由挂起和恢复引起的分区

当我们提到网络分区时，实际上分区是指集群的不同节点可以在没有任何节点故障的情况下中断通信。除了网络故障，挂起和恢复整个操作系统在针对运行的集群节点使用时也可能导致分区-由于挂起的节点不会认为自己已失败，甚至不会停止，但是集群中其它节点会认为他已经挂掉了。

虽然你可以通过在笔记本电脑上运行集群节点并通过关闭它的盖子来挂起，但是发生这种情况最常见的原因是虚拟机被hypervisor挂起。

虽然在虚拟化环境或容器中运行RabbitMQ集群很好，但是要确保VM在运行时没有挂起。

请注意，一些虚拟化功能（如：将虚拟机从一台主机迁移到另一台主机）往往会导致虚拟机被挂起。

由suspend和resume引起的分区往往是不对称的-挂起的节点不一定会看到其它节点已关闭，但会被集群的其余部分视为已关闭。这是pause_minority模式特别有用。

##### 4.手动恢复网络分区（Recovering from a split-brain）

要恢复分区，首先选择一个你最信任的分区，此分区将成为（系统状态、模式、消息、队列、交换器）受信任的主分区，在其它分区上的任何更改将会丢失。

手动恢复分区有两种方法：

- A分区受信任，停止其它分区的所有节点，然后启动所有节点。当它们重新加入集群时，它们将从受信任分区恢复状态。

```
#停止节点（包括RabbitMQ应用和Erlang VM虚拟机）
rabbitmqctl stop
#启动节点
rabbitmq-server -detached
```

最后，你应该重启信任分区的所有节点，目的是为了清除警告（实测不用重启也可以清除受信任分区的警告信息）

- 停止所有分区的节点，并启动所有的节点（启动的第一个节点必须是受信任分区的节点）

```
#停止节点（包括RabbitMQ应用和Erlang VM虚拟机）
rabbitmqctl stop
#启动节点
rabbitmq-server -detached
```

##### 5.网络分区处理策略

RabbitMQ提供了四种方法自动处理网络分区（默认行为被称为ignore模式，需手动处理）：

- ignore模式

- pause-minority模式
- pause-if-all-down模式
- autoheal模式

ignore模式在/etc/rabbitmq/advanced.config中的配置：

```
 [
        {
                rabbit, [
                        {cluster_partition_handling, ignore}
                ]

        }
 ].

```

或在/etc/rabbitmq/rabbitmq.conf中的配置：

```
cluster_partition_handling = ignore
```

在pause-minority模式下，当发生网络分区时，集群的节点在观察到某些节点down掉时，会自动检测自身是否处于少数派（即少于或等于节点总数的一半），少数派将会在分区发生时自动关闭，并且在分区结束时自动启动。这里的关闭是只RabbitMQ 应用程序关闭，对应CTL命令是rabbitmqctl stop_app，而Erlang VM并不关闭。处于关闭的节点会每秒检测一次是否可以连通到剩余的集群中，如果可以则启动自身的应用，这种配置防止了网络分区，因此能够自动从网络分区恢复而不出现不一致。

需要注意的是RabbitMQ也会关闭不是严格意义上的大多数，比如在一个集群中只有两个节点的时候并不适合pause-minority模式，因为由于其中任何一个节点失败而发生网络分区时，两个节点都会关闭。当网络恢复时，有可能两个节点会自动启动恢复网络分区，也有可能保持关闭状态。然而如果集群中的节点远大于两个时，pause-minority模式比ignore模式更加可靠，特别是网络分区通常是由于单个节点网络故障而脱离原分区引起的。

在/etc/rabbitmq/advanced.config配置文件中的配置：

```
 [
        {
                rabbit, [
                        {cluster_partition_handling, pause_minority}
                ]
        }
 ].


```

或在/etc/rabbitmq/rabbitmq.conf中的配置：

```
cluster_partition_handling = pause_minority
```

在pause-if-all-down模式下，RabbitMQ会自动关闭不能和list中节点通信的节点。语法为{pause-if-all-down,[nodes],ignore|autoheal}，其中[nodes]就是前面所说的list。如果一个节点与list中的所有节点都无法通信时，关闭其自身。如果list中的所有节点都down时，其余节点是正常的话，也会根据这个规则关闭其自身。此时集群中所有的节点会关闭。如果某个节点可以和list中的节点恢复通信，那么会启动其自身的RabbitMQ应用，慢慢的集群可以恢复。

有两种配置如下：

```
 [
        {
                rabbit, [
                        {cluster_partition_handling, {pause_if_all_down,  ['rabbit@node1'], autoheal}}
                ]
        }
 ].

```

或

```
 [
        {
                rabbit, [
                        {cluster_partition_handling, {pause_if_all_down,  ['rabbit@node1'], ignore}}
                ]
        }
 ].

```

或在/etc/rabbitmq/rabbitmq.conf中的配置：

```
## pause_if_all_down strategy require additional configuration
# cluster_partition_handling = pause_if_all_down

## Recover strategy. Can be either 'autoheal' or 'ignore'
# cluster_partition_handling.pause_if_all_down.recover = ignore

## Node names to check
# cluster_partition_handling.pause_if_all_down.nodes.1 = rabbit@localhost
# cluster_partition_handling.pause_if_all_down.nodes.2 = hare@localhost
```

配置中会有ignore和autoheal模式设置，考虑会有这样一种情况，有两个节点在A、B在机架M上，节点C、D在机架N上，此时机架M和N的通信出现了异常，如果使用pause-minority模式的话会关闭所有的节点，如果此时采用pause-if-all-down，list中配置成[A,C]的话，集群中的四个节点都不会关闭，但是会形成两个分区，此时就需要使用ignore和autoheal来指引如何处理这种分区情况。



在autoheal模式下，当认为发生网络分区时，RabbitMQ会自动决定一个获胜的分区，然后重启不在这个分区中的节点以恢复网络分区。一个获胜的分区是指客户端连接最多的一个分区。如果产生平局，即两个或者多个分区的客户端连接数一样多，那么节点数最多的一个分区就是获胜的分区。如果此时节点数一样多，将以一种特殊的方式来挑选获胜分区。

```
 [
        {
                rabbit, [
                        {cluster_partition_handling, autoheal}
                ]
        }
 ].


```

rabbitmq.conf配置文件示例地址：https://github.com/rabbitmq/rabbitmq-server/blob/master/docs/rabbitmq.conf.example

rabbitmq.conf配置文件在服务端的位置是/etc/rabbitmq/rabbitmq.conf



###### 6.如何关闭端口号或者域名

```
封单个IP的命令：
iptables -I INPUT -s 124.115.0.199 -j DROP

解封单个IP的命令：
iptables -D INPUT -s 124.115.0.199 -j DROP

封整个段的命令：
iptables -I INPUT -s 194.42.0.0/8 -j DROP

解封整个段的命令：
iptables -D INPUT -s 194.42.0.0/8 -j DROP

封指定的端口：
iptables -A INPUT -p tcp --dport 80 -j DROP

解封指定的端口：
iptables -A INPUT -p tcp --dport 80 -j ACCEPT

#一键清空所有规则
iptables -F

清空：
iptables -D INPUT 数字

查看：
iptables -L -n
```

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/doc/rabbitmq](https://github.com/mingyang66/spring-parent/tree/master/doc/rabbitmq)