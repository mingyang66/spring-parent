#### RabbitMQ学习笔记：队列的HA高可用性

##### 一、HA高可用性有哪些模式

使classic queues队列镜像，创建一个与他们匹配的策略，并设置策略的=ha-mode和可选的ha-params参数，下表说明了这些键的选项：

| ha-mode | ha-params  | Result                                                       |
| ------- | ---------- | ------------------------------------------------------------ |
| exactly | count      | 集群中的队列副本数量（leader加上镜像队列），计数值1表示一个单个副本：只是队列的leader。如果运行queue的leader节点变的不可用，则行为取决于队列的持久性。计数值2表示2个副本：1个queue的leader和1个队列镜像。换句话说：NumberOfQueueMirrors = NumberOfNodes - 1。如果运行queue的leader节点不可用，则队列镜像将根据配置的镜像升级策略自动升级为leader。如果集群中的节点少于count个，则队列将镜像到所有节点。如果集群中有超过count个节点，并且包含镜像的节点出现故障，则将在另一个节点上创建新镜像。将exactly模式和"ha-promote-on-shutdown": "always"一起使用可能是危险的，因为队列可能会在集群中迁移，并在集群关闭时变得不同步。 |
| all     | (none)     | 队列在集群中的所有节点上进行镜像。将新节点添加到集群中时，队列将镜像到该节点。这种设置非常保守。推荐镜像节点数为（N/2+1）的仲裁节点，镜像到所有节点会给所有集群节点带来额外的压力，包括网络I/O、磁盘I/O和磁盘空间使用。 |
| nodes   | node names | 队列将镜像到节点名称中列出的节点。节点名称是Erlang节点名称，它们出现在rabbitmqctl cluster_status命令执行后的结果中，它们通常的格式是‘rabbit@hostname’。如果这些节点名称中的任何一个都不是集群的一部分，则这并不构成错误。如果在声明队列的列表中没有任何节点处于联机状态，则将在声明客户端连接的节点上创建队列。 |

##### 二、有多少个镜像是最佳的？

​		镜像到所有的节点是最保守的选择。这将给所有集群节点带来额外的压力，包括网络I/O、磁盘I/O和磁盘空间使用。在大多数情况下，在每个节点上都有一个复制副本是不必要的。

​		对于3个及以上节点的集群，建议复制到法定数量的节点，例如：3个节点集群中的2个节点或5个节点集群的3个节点。

​		由于某些数据可能是固有的瞬态数据或对事件非常敏感，因此对某些队列使用较少数量的镜像（甚至不使用任何镜像）是完全合理的。

##### 三、队列leader副本、leader迁移、数据本地化

- 队列leader位置分布设置

​		RabbitMQ中的每个队列都有一个主副本。该副本被称为队列leader，所有队列操作都首先经过主副本，然后复制到其它副本。这对于保证消息的FIFO排序是必要的。

​	    为了避免集群中的某些节点承载大多数队列leader副本的职责，从而处理大部分负载，队列leader应该合理地均匀分布在集群节点之间。

​        队列leader可以使用多种策略分布在节点之间，使用那种策略是通过三种方式控制的，第一种是在队列配置x-queue-master-locator可选队列参数，第二种是在Policy上设置queue-master-locator，第三种是在配置文件中定义queue_leader_locator策略key，如下有三种供选择的策略：

1. min-masters：选择master数最少的那个服务节点；
2. client-local：选择client相连接的那个服务节点；
3. random：随机分配

命令模式设置分配策略

```sh
rabbitmqctl set_policy -p vhostname --priority 0 --apply-to queues ha-two "^" '{"ha-mode":"exactly","ha-params":2,"ha-sync-mode":"automatic","queue-master-locator":"min-masters"}'
```



- “nodes”策略和迁移leader

​		请注意，如果leader节点没有列在新策略中，设置或修改”nodes“策略可能会导致现有的leader节点离职。为了防止消息丢失，RabbitMQ将保留现有的leader，直到至少有一个其它镜像同步为止（即使这是一段很长的时间）。然而一旦同步发生，事情就会像节点发生故障一样继续进行：消费者将与leader断开连接，需要重新连接。

​       例如：如果一个队列在[A B]（A是leader），并且您给它一个节点策略，告诉它在[C D]上，它最初会在[A C D]上。一旦队列在其新镜像[C D]上同步，A做为leader将会关闭。

- 独占队列的镜像


​	   当声明独占队列的连接关闭时，独占队列将被删除。因此，镜像独占队列（或非持久队列）是没有用的，因为当承载它的节点关闭时，连接将关闭，并且无论如何都需要删除队列。

​       因此，独占队列永远不会镜像（即使它们与声明应该镜像的策略相匹配）。它们也永远不会持久（即使被宣布为持久）。

##### 四、HA命令案例

- 查询根Virtualhost策略配置命令：

```sh
rabbitmqctl list_policies
```

- 查询指定VirtualHost策略配置命令

```sh
rabbitmqctl -p trade list_policies
```

- 删除RabbitMQ策略配置：

```sh
rabbitmqctl clear_policy --vhost <vhost> <name>
```

- 设置镜像队里示例

```sh
# 设置集群为镜像队列
# rabbitmqctl set_policy [-p vhost] [--priority priority] [--apply-to apply-to] {name} {pattern} {definition}
rabbitmqctl set_policy -p / --priority 0 --apply-to queues ha-all "^" '{"ha-mode":"all","ha-sync-mode":"automatic"}'
rabbitmqctl set_policy -p / --priority 0 --apply-to queues ha-two "^" '{"ha-mode":"exactly","ha-params":2,"ha-sync-mode":"automatic","queue-master-locator":"min-masters"}'

```

- [ ] `<name>`：策略的名称，用于标识该策略。
- [ ] `<pattern>`：用于匹配队列名称的模式，支持正则表达式。
- [ ] `<definition>`：策略的定义，包括如何处理匹配到的队列。
- [ ] --apply-to：all【Exchanges and queues】、exchanges【Exchanges】、queues【Queues】
- [ ] -p <vhost>：可选参数，指定虚拟主机（vhost）的名称。如果省略，则使用默认的虚拟主机。
- [ ] --priority <priority>：可选参数，指定策略的优先级。如果省略，则使用默认的优先级。
- [ ] ha-mode：all【镜像到集群所有节点】、exactly【镜像到集群中指定数量的节点】、nodes【镜像到集群中指定节点名称的节点】
- [ ] ha-params：如果ha-mode为all值缺省、如果ha-mode为exactly值为数量、如果ha-mode为nodes值为节点名称列表
- [ ] ha-sync-mode：manual【手动】、automatic【自动】
- [ ] queue-master-locator：min-masters：选择master数最少的那个服务节点 ,client-local：选择与client相连接的那个服务节点 ，random：随机分配

- [ ] ha-promote-on-shutdown ：当 RabbitMQ 节点意外关闭时，该参数指定是否将非主节点（mirror 节点）提升为主节点。默认情况下，该参数为 `when-synced`，表示只有在镜像队列与主队列完全同步后才会进行提升。如果设置为 `always`，则无论镜像队列是否与主队列同步，都会进行提升。

- [ ] ha-promote-on-failure：当 RabbitMQ 节点发生故障并重新启动时，该参数指定是否将非主节点（mirror 节点）提升为主节点。默认情况下，该参数为 `when-synced`，表示只有在镜像队列与主队列完全同步后才会进行提升。如果设置为 `always`，则无论镜像队列是否与主队列同步，都会进行提升。

- [ ] message-ttl：消息发送到队列中后可以存活的时长，单位：毫秒

- [ ] expires：队列在自动删除之前可以闲置的时间，单位：毫秒

- [ ] queue-mode：设置队列模式，默认：default 、lazy【classic queues中的消息会尽可能早的移入硬盘】

- 下面是一个名字以"two."开头的队列将镜像到集群中的任何两个节点，并自动同步：

```sh
rabbitmqctl set_policy --vhost trade --apply-to queues ha-two "^" '{"ha-mode":"exactly","ha-params":2,"ha-sync-mode":"automatic"}' 
```

- 以下示例声明了一个策略，该策略与名称以"ha"开头的队列匹配，并将镜像配置为集群中的所有节点。


```sh
rabbitmqctl set_policy ha-all "^ha\." '{"ha-mode":"all"}'
```

- 以下是将名称以"nodes."开头的队列镜像到集群中特定的节点：


```sh
rabbitmqctl set_policy ha-nodes "^nodes\." '{"ha-mode":"nodes","ha-params":["rabbit@nodeA", "rabbit@nodeB"]}'
```

##### 五、镜像队列的实现与语义

对于每个镜像队列，都有一个leader副本和几个镜像，每个镜像都位于不同的节点上。对镜像节点的操作与leader节点的操作完全保持同步，从而保持状态的一致性。除发布之外的所有操作都只发送给leader节点，然后leader节点将操作的结果广播给镜像，从镜像队列中消费的客户端实际上是从leader节点哪里消费的消息。

如果leader故障，则其中一个镜像将晋升为领导者，如下所示：

1. 运行时间最长的镜像被提升为leader，假设它最有可能与leader完全同步。如果没有与leader同步的镜像，那么只存在于leader上的信息就会丢失。
2. 镜像节点认为以前的所有消费者都突然断开了连接。它重新排队已传递到客户端但正在等待确认的所有消息。这可以包括客户端已经发出确认的消息，如果确认在到达承载队列leader的节点之前在线路上丢失，或者在从leader向镜像广播时丢失。在任何一种情况下，新leader都别无选择，只能 重新排列所有未收到确认的消息。
3. 请求在队列故障转移时得到客户端的请求通知将会收到取消通知。
4. 重新排队的结果是，从队列中重新消费的客户端必须意识到，他们很可能随后会收到已经收到的消息。
5. 当所选镜像称为leader时，在此期间发布到镜像队列的任何消息都不会丢失（除非升级节点上出现后续故障）。发布到承载队列镜像的节点的消息将路由到队列leader节点，然后复制到所有镜像。如果leader故障，消息将继续发送到镜像，并在完成将镜像升级到leader后添加到队列中。
6. 即使leader(或任何镜像)在发布的消息和发布者收到的确认之间故障，客户端使用发布者确认发布的消息仍将得到确认。从发布者的角度来看，发布到镜像队列与发布到非镜像队列没有什么不同。

如果消费者使用自动确认模式，那么消息可能会丢失。当然，这与非镜像队列没有什么不同；一旦消息以自动确认模式发送给消费者，代理就会认为消息已得到确认。

如果客户端突然断开连接，则可能永远不会收到该消息。在镜像队列的情况下，如果leader故障，在自动确认模式下发送给消费者的消息可能永远不会被这些客户端接收，也不会被新的leader重新排队。由于消费客户端可能连接到幸存的节点，因此消费者取消通知有助于识别此类时间何时发生，当然，在实践中，如果数据安全不如吞吐量重要，那么自动确认模式是可行的。

##### 六、停止节点和同步

如果停止包含镜像队列leader的RabbitMQ节点，则其它节点上的某个镜像将升级为leader（假设存在同步镜像；请参阅下文）。如果继续停止节点，那么镜像队列将不再有镜像：它只存在于一个节点上，该节点现在是它的leader。如果镜像队列被声明为持久，那么，如果其最后一个剩余节点关闭，队列中的持久消息将在该节点重新启动后继续存在。通常，在重新启动其它节点时，如果它们以前是镜像队列的一部分，则它们将重新加入镜像队列。

然而，镜像目前无法知道其队列内容是否已偏离其重新加入前的leader（例如，这可能发生在网络分区间）。因此，当镜像重新加入镜像队列时，它会丢弃它已经拥有的任何持久本地内容，并开始清空。在这一点上，它的行为与加入集群的新节点相同。

##### 七、停止仅使用未同步镜像承载队列leader的节点

当您关闭leader节点时，所有可用的镜像都可能不同步。发生这种情况的常见情况是滚动集群升级。

默认情况下，RabbitMQ将拒绝在受控的leader关闭（即显式停止RabbitMQ服务或关闭操作系统）时升级未同步镜像，以避免消息丢失；相反，整个队列将关闭，就好像不存在未同步的镜像一样。

ha-promote-on-shutdown：默认：when-synced，always

ha-promote-on-failure：

##### 八、当所有的镜像停止时可能会丢失队列的leader

当队列的所有镜像都关闭时，可能会丢失队列的leader。在正常操作中，队列关闭的最后一个节点将成为leader，我们希望该节点在再次启动时仍然是leader（因为它可能收到了其它镜像没有看到的消息）。

但是，当您调用rabbitmqctl forget_cluster_node时，RabbitMQ将尝试为每个队列找到一个当前停止的镜像，该队列在我们forgetting的节点上有其leader，并在镜像重启时“提拔”它称为新的leader。如果有多个候选人，将选择最近停止的镜像。

重要的是要理解，RabbitMQ只能在forget_cluster_node期间提升已停止的镜像，因为任何重新启动的镜像都会清除其内容，如上文“停止节点和同步”中所述。因此在删除已停止集群中丢失的leader时，必须在再次启动镜像之前调用rabbitmqctl forget_cluster_node。

##### 九、批量同步

classic队列leader批量执行同步，批处理可以通过ha-sync-batch-size队列参数配置，如果未设置任何值，mirroring_sync_batch_size作为默认值。默认情况下，早期版本（3.6.0之前）将一次同步1条消息。通过批量同步消息，同步过程可以大大加快。

要为ha-sync-batch-size选择一个正确的值，您需要考虑：

- 消息的平均大小。
- RabbitMQ节点之间的网络吞吐量。
- net_ticktime值。

例如，如果将ha-sync-batch-size大小设置为50000条消息，并且队列中的每条消息大小为1KB,则每个节点之间的同步消息为49MB。您需要确保队列镜像之间的网络能够容纳这种流量。如果网络发送一批消息的时间比net_ticktime长，那么集群中的节点可能会认为他们存在网络分区。

还可以通过设置参数mirroring_sync_max_throughput来控制通过网络发送的数据量。该参数指定每秒传输的字节数。默认值为0，这将禁止此功能。

##### 十、配置同步

让我们从队列同步的最重要方面开始：在同步队列时，所有其它队列 操作都将被阻塞。根据多种因素，队列可能会被同步阻塞数分钟或数小时，在极端情况下甚至会阻塞数天。

队列同步可以配置如下：

ha-sync-mode: manual：这是默认模式。新队列镜像将不会接收现有消息，只会接收新消息。随着时间的推移，一旦消费者耗尽了只存在于leader上的消息，新的队列镜像将成为leader的精确镜像。如果在耗尽所有未同步的消息之前leader队列故障，则这些消息将丢失。你可以手动完全同步队列。

ha-sync-mode: automatic：当一个 新的镜像加入时，队列将自动同步。值得注意的是，队列同步是一种阻塞操作。如果队列很小，或者RabbitMQ节点之间有一个快速网络，并且ha同步批处理大小的到了优化，这是一个不错的选择。

