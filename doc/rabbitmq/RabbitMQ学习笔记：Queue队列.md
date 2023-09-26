#### RabbitMQ学习笔记：Queue队列

#### 一、队列介绍

- 队列名：队列名最大255bytes的UTF8字符，队列名以“amq.”开头的是broker内部保留使用的命名规则，违反命名规则的将会报通道级别的异常403（ACCESS_REFUSED）

#### 二、队列参数介绍：

| 参数名                    | 描述                                                         |
| ------------------------- | ------------------------------------------------------------ |
| x-queue-master-locator    | min-masters：选择master数最少的那个服务节点 <br />client-local：选择与client相连接的那个服务节点 <br />random：随机分配 |
| x-message-ttl             | 消息发送到队列后可以存活的时长，单位：毫秒                   |
| x-expires                 | 队列未被使用存活的时长，单位：毫秒                           |
| x-max-length              | 队列在开始从其头部丢弃消息之前可以包含多少（就绪）消息       |
| x-max-length-bytes        | 队列再开始从其头部丢弃就绪消息之前可以包含的就绪消息的总正文大小 |
| x-overflow                | 缺省                                                         |
| x-dead-letter-exchange    |                                                              |
| x-dead-letter-routing-key |                                                              |
| x-single-active-consumer  |                                                              |
| x-max-priority            | 队列要支持的最大优先级数量；如果未设置，队列将不支持消息优先级。 |
| x-queue-mode              | 默认：default，lazy 设置队列为延迟加载模式，将尽可能多的消息写入磁盘减少RAM的使用率 |



