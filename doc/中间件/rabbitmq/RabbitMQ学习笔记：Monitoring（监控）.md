### RabbitMQ学习笔记：Monitoring（监控）

> 本文概述了与RabbitMQ相关的主题。监控RabbitMQ和使用它的应用程序非常重要。监控有助于在问题影响到环境的其它部分以及最终影响最终用户之前检测到问题。

系统的许多方面都可以被监控，本文档将它们分为几个类别：

- 什么是监控，有什么共同的方法存在，为什么它是重要的。
- 内置和外部监视选项。
- 哪些基础设施和内核指标是重要的监视对象。
- 有哪些RabbitMQ指标可用：
    - [ ] 节点指标
    - [ ] 队列指标
    - [ ] 集群范围指标
- 应多久执行一次监控检查？
- 应用程序级指标
- 如何处理节点运行状态检查，以及为什么它比单个CLI命令更复杂。
- 日志聚合
- 基于命令行的observer工具

跨所有节点和应用程序的日志聚合与监控密切相关，本文档中也提到了这一点。

许多流行的工具，包括开源工具和商业工具，都可以用来监视RabbitMQ。[Prometheus and Grafana](https://www.rabbitmq.com/prometheus.html)
是一个强烈的推荐选择。

##### 什么是监控？

在本文中，我们将监控定义为一个通过健康检查和随时间变化捕获系统指标行为的过程。这有助于检测异常情况：当系统不可用时，会经历异常负载、某些资源耗尽或其它不在其正常（预期）参数范围内的行为。监控包括收集和长期存储指标，这不仅对异常监控很重要，而且对根本原因分析、趋势检测和容量规划也很重要。

监控系统通常与警报系统集成。当监控系统检测到异常时，通常会向警报系统传递某种类型的警报，警报系统会通知相关方，如技术操作团队。

实时监控意味着系统行为中的重要偏差（从某些区域的服务降级到完全不可用）更容易发现，而查找根本原因所需的时间要少的多。操作一个分布式系统有点像不带GPS导航设备或指南针就试图走出森林。不管这个人有多聪明或经验丰富，掌握相关信息对于取得好的结果非常重要。

##### 健康检查在监控中的作用

健康检查是监控的最基本方面，它包含一个或一组 命令，这些命令随时间收集被监控系统的一些基本度量并测试它们。例如，RabbitMQ的Erlang
VM是否正在运行就是这样一个检查。本例中的度量标准是“is an OS process running?”。正常操作参数为“the process must be
running”。最后，还有一个评估步骤。

当然，健康检查的种类有很多。哪些是最合适的取决于所使用的“healthy node”的定义。所以，这是一个特定于系统和团队的决策。RabbitMQ
CLI工具提供的命令可以用作有用的运行状态检查。本文将稍后介绍它们。

虽然运行状态检查是一个有用的工具，但它们只能提供对系统状态的如此多的检查，因为它们在设计上侧重于一个或少数指标，通常检查单个节点，并且只能在特定时刻推断该节点的状态。要进行更全面的评估，请随时间收集更多指标。这将检测更多类型的异常，因为有些异常只能在较长时间内识别。这通常是由被称为监视工具的工具来完成的，这些工具有很多种。本文涵盖用于RabbitMQ监视的一些工具。

##### 系统和RabbitMQ指标

一些指标是RabbitMQ特有的：它们由RabbitMQ节点收集和报告。在本文中，我们将它们称为“RabbitMQ
metrics”。示例包括使用的套接字描述符的数量、排队消息的总数或节点间通信流量率。其它指标由操作系统内核收集和报告。这种度量通常称为系统度量或基础设施度量。系统度量不是RabbitMQ特有的。示例包括CPU利用率、进程使用的内存量、网络包丢失率等，这些都是重要的追踪指标。单独的指标并不总是有用的，但是当一起分析时，它们可以提供对系统状态的更完整的洞察。然后，操作者可以形成一个关于正在发生的事情和需要解决的问题的假设。

##### 基础设施和核心指标

建立一个有用的监控系统的第一步是从基础设施和内核指标开始。它们中有不少，但是有些指标比其它的更重要。在运行RabbitMQ节点或应用程序的所有主机上收集以下指标：

- CPU状态（user、system、iowait&idle percentages
- 内存使用率（used、buffered、cached & free percentages）
- 虚拟内存统计信息（dirty page flushes, writeback volume）
- 磁盘I/O（operations & amount of data transferred per unit time, time to service operations）
- 装载上用于节点数据目录的可用磁盘空间
- beam.smp使用的文件描述符与最大系统限制
- 按状态列出的TCP连接（ESTABLISHED,CLOSE_WAIT,TIME_WATT）
- 网络吞吐量（bytes received,bytes sent） & 最大网络吞吐量
- 网络延迟（集群中所有RabbitMQ节点之间以及客户端之间）

不存在现有的工具（如Pometheus或Datadog）收集基础设施和内核指标，在一段时间内存储和可视化它们。

##### 监控频率

许多监控系统定期轮询其监视的服务。这一操作的频率因工具而异，但通常可以由操作人员配置。

非常频繁的轮询会对被监控的系统产生负面影响。例如，打开到节点的测试TCP连接的负载平衡器检查过多会导致连接中断。RabbitMQ中对信道和队列的过度检查将
增加其CPU消耗。当一个节点上有许多这样的检查时，这种差异可能是显著的。

建议指标的收集间隔为15秒。要以更接近实时的间隔进行采集，请使用5秒，但不能低于5秒。对于速率指标，请使用跨越4个度量收集间隔的时间范围，以使其能够容忍竞争条件，并对擦写失败具有弹性。

对于生产系统，建议收集间隔为30秒甚至60秒。Prometheus设计为每隔15秒收集一次，包括生产系统。

##### 管理用户界面和外部监控系统

RabbitMQ带有一个管理UI和HTTP API,它公开了节点、连接、队列、消息速率等的RabbitMQ指标。对于开发这是一个方便的选择，以及在外部监控难以或不可能引入的环境中。

但是，管理UI有许多限制：

- 监控系统与被监控系统交织在一起
- 占用系统一定的开销
- 它只存储最近的数据（最多一天，不是几天天或几个月）
- 它有一个基本的用户界面
- 它的设计强调易用性，而不是最佳可用性
- 管理UI访问通过RabbitMQ权限标记系统（或JWT令牌作用域的约定）进行控制

Prometheus和Grafana或ELK stack等长期度量存储和可视化服务更适合用于生产系统。它们提供：

- 监控系统与被监控系统的解耦
- 低开销
- 指标长期存储
- 访问其它相关指标，如Erlang运行时度量
- 更强大和可定制的用户界面
- 指标数据共享方便：包括指标状态和控制面版
- 指标访问权限不是特定于RabbitMQ的
- 收集和聚合特定于节点的度量，这些指标对于单个节点故障更具弹性

RabbitMQ从3.8版本开始为[Prometheus and Grafana](https://www.rabbitmq.com/prometheus.html)提供一级支持，建议用于生产环境。

##### RabbitMQ指标

RabbitMQ管理插件提供了访问RabbitMQ指标的API。该插件将存储最多一天的指标数据。应使用外部工具完成长期监控。

本文将介绍监控的多个RabbitMQ特定方面。

##### 监控集群

在监控集群时，理解HTTP
API的保证非常重要。在集群环境中，每个节点都可以为指标端点请求提供服务。集群范围内的指标可以从任何可以从任何可以与其对等节点联系的节点获取。在生成响应之前，该节点将根据需要收集和组合来自其对等方的数据。

每个节点还可以向为其自身以及其他集群节点提供特定于节点的指标的端点提供请求。与[infrastructure and OS metrics](https://www.rabbitmq.com/monitoring.html#system-metrics)
一样，必须为每个节点收集特定于节点的指标。监控工具可以对任何节点执行HTTP API请求。

如前所述，节点间连接问题将影响HTTP API行为。为监控请求选择一个随机联机节点。例如，使用负载均衡器或循环DNS。

某些端点在目标节点上执行操作。节点本地运行状况检查是最常见的示例。这是个例外，不是规则。

##### 集群范围的指标

集群范围的指标提供了集群状态的高级视图。其中描述了节点之间的交互。此类指标的示例包括集群链路通信量和检测到的网络分区。其它的则将集群成员的指标组合在一起。所有节点的连接的完整列表就是一个例子。这两种类型都是基础设施和节点指标的补充。

GET /api/overview是个HTTP API端点用来返回集群范围的指标。

| Metric                                                                      | JSON field name                                                                                                                  |
|-----------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------|
| Cluster name                                                                | `cluster_name`                                                                                                                   |
| Cluster-wide message rates                                                  | `message_stats`                                                                                                                  |
| Total number of connections                                                 | `object_totals.connections`                                                                                                      |
| Total number of channels                                                    | `object_totals.channels`                                                                                                         |
| Total number of queues                                                      | `object_totals.queues`                                                                                                           |
| Total number of consumers                                                   | `object_totals.consumers`                                                                                                        |
| Total number of messages (ready plus unacknowledged)                        | `queue_totals.messages`                                                                                                          |
| Number of messages ready for delivery                                       | `queue_totals.messages_ready`                                                                                                    |
| Number of [unacknowledged](https://www.rabbitmq.com/confirms.html) messages | `queue_totals.messages_unacknowledged`                                                                                           |
| Messages published recently                                                 | `message_stats.publish`                                                                                                          |
| Message publish rate                                                        | `message_stats.publish_details.rate`                                                                                             |
| Messages delivered to consumers recently                                    | `message_stats.deliver_get`                                                                                                      |
| Message delivery rate                                                       | `message_stats.deliver_get.rate`                                                                                                 |
| Other message stats                                                         | `message_stats.*` (see [this document](https://rawcdn.githack.com/rabbitmq/rabbitmq-management/v3.7.19/priv/www/doc/stats.html)) |

##### 节点指标

这里提供了两个HTTP API用来提供访问指定节点的指标：

- GET /api/nodes/{node}返回单个节点的状态
- GET /api/nodes 返回所有集群成员节点的状态

后一个API返回一个数组对象。支持作为输入的监视工具应该更喜欢该接口，因为它减少了请求的数量。如果不是这样，则使用前一个端点一次检索每个集群成员的统计信息。这意味着监控系统要知道集群成员的列表信息。

大多数指标表示时间点的绝对值。有些表示最近一段时间内的活动（例如，GC运行和回收的字节）。与以前的值和历史平均值/百分位值相比，后一个度量值最有用。

| Metric                                                                                        | JSON field name                     |
|-----------------------------------------------------------------------------------------------|-------------------------------------|
| Total amount of [memory used](https://www.rabbitmq.com/memory-use.html)                       | `mem_used`                          |
| Memory usage high watermark                                                                   | `mem_limit`                         |
| Is a [memory alarm](https://www.rabbitmq.com/memory.html) in effect?                          | `mem_alarm`                         |
| Free disk space low watermark                                                                 | `disk_free_limit`                   |
| Is a [disk alarm](https://www.rabbitmq.com/disk-alarms.html) in effect?                       | `disk_free_alarm`                   |
| [File descriptors available](https://www.rabbitmq.com/networking.html#open-file-handle-limit) | `fd_total`                          |
| File descriptors used                                                                         | `fd_used`                           |
| File descriptor open attempts                                                                 | `io_file_handle_open_attempt_count` |
| Sockets available                                                                             | `sockets_total`                     |
| Sockets used                                                                                  | `sockets_used`                      |
| Message store disk reads                                                                      | `message_stats.disk_reads`          |
| Message store disk writes                                                                     | `message_stats.disk_writes`         |
| Inter-node communication links                                                                | cluster_links                       |
| GC runs                                                                                       | `gc_num`                            |
| Bytes reclaimed by GC                                                                         | `gc_bytes_reclaimed`                |
| Erlang process limit                                                                          | `proc_total`                        |
| Erlang processes used                                                                         | `proc_used`                         |
| Runtime run queue                                                                             | `run_queue`                         |

##### 单个队列指标

单个队列的指标通过HTTP APIGET /api/queues/{vhost}/{qname}接口提供。

| Metric                                                                      | JSON field name                                                                                                                  |
|-----------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------|
| Memory                                                                      | `memory`                                                                                                                         |
| Total number of messages (ready plus unacknowledged)                        | `messages`                                                                                                                       |
| Number of messages ready for delivery                                       | `messages_ready`                                                                                                                 |
| Number of [unacknowledged](https://www.rabbitmq.com/confirms.html) messages | `messages_unacknowledged`                                                                                                        |
| Messages published recently                                                 | `message_stats.publish`                                                                                                          |
| Message publishing rate                                                     | `message_stats.publish_details.rate`                                                                                             |
| Messages delivered recently                                                 | `message_stats.deliver_get`                                                                                                      |
| Message delivery rate                                                       | `message_stats.deliver_get.rate`                                                                                                 |
| Other message stats                                                         | `message_stats.*` (see [this document](https://rawcdn.githack.com/rabbitmq/rabbitmq-management/v3.7.19/priv/www/doc/stats.html)) |

##### 应用程序级指标

使用消息传递的系统几乎都是分布式的。在这样的系统中，通常不能立即看出那个组件行为不端。系统的每个部分，包括应用程序，都应被监控和调查。

一些基础设施级别和RabbitMQ指标可以显示异常系统行为或问题的存在，但不能指出根本原因。例如，很容易判断一个节点的磁盘不足，但并不总是很容易直到原因。这就是应用程序指标的来源：它们可以帮助识别一个运行的发布者、一个不断失败的消费者、一个跟不上速度的消费者，甚至是一个正在经历减速的下游服务（例如，消费者使用的数据库中缺少索引）。

一些客户端库和框架提供了注册指标收集器或收集现成指标的方法。RabbitMQ Java客户端和Spring AMQP就是两个例子。其它开发人员必须跟踪应用程序代码中的指标。

应用程序跟踪的指标可以是特定于系统的，但有些指标与大多数系统相关；

- Connection opening rate
- Channel opening rate
- Connection failure (recovery) rate
- Publishing rate
- Delivery rate
- Positive delivery acknowledgement rate
- Negative delivery acknowledgement rate
- Mean/95th percentile delivery processing latency

翻译自：https://www.rabbitmq.com/monitoring.html

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service)