### RabbitMQ学习笔记：消息追踪rabbitmq_tracing插件

> rabbitmq_tracing插件相当于Firehose的GUI版本，它同样能跟踪RabbitMQ中消息的流入流出情况。rabbitmq_tracing插件同样会对流入流出的消息进行封装，然后将封装后的日志存入相应的trace文件中。

##### 启动rabbitmq_tracing插件：

```
[root@rabbit1 /]# rabbitmq-plugins enable rabbitmq_tracing
Enabling plugins on node rabbit@rabbit1:
rabbitmq_tracing
The following plugins have been configured:
  rabbitmq_management
  rabbitmq_management_agent
  rabbitmq_tracing
  rabbitmq_web_dispatch
Applying plugin configuration to rabbit@rabbit1...
The following plugins have been enabled:
  rabbitmq_tracing

started 1 plugins.
```

##### 对应关闭rabbitmq_tracing插件指令是：

```
[root@rabbit1 /]# rabbitmq-plugins disable rabbitmq_tracing
Disabling plugins on node rabbit@rabbit1:
rabbitmq_tracing
The following plugins have been configured:
  rabbitmq_management
  rabbitmq_management_agent
  rabbitmq_web_dispatch
Applying plugin configuration to rabbit@rabbit1...
The following plugins have been disabled:
  rabbitmq_tracing

stopped 1 plugins.
```

##### 启动rabbitmq_tracing插件后，在web管理界面Admin右侧会多出一个Tracing选项：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191220172623283.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9taW5neWFuZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

可以在此Tab项中添加相应的trace,如上图所示；在添加完成trace之后，会根据匹配规则将相应的日志输出到对应的trace文件中，文件的默认路径是/var/tmp/rabbitmq-tracing，可以在页面中直接点击Trace log files查看追踪到的消息，如下图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191220173351810.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9taW5neWFuZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

我们添加了两个trace任务，与之对应的有两个trace文件，可以通过trace文件追踪消息的流入和流出情况；

##### 队列

在添加完成两个trace任务后，会发现多了两个队列，如下图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191220173714184.png)

就以第一个队列amq.gen-K1qSTxD4k3z8aLWMEn5Z3w而言，其所绑定的交换器就是amq.rabbitmq.trace,如下图所示：

![在这里插入图片描述](https://img-blog.csdnimg.cn/2019122017390038.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9taW5neWFuZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

由此可以看出整个rabbitmq_tracing插件和Firehouse在实现上如出一辙，只不过rabbitmq_tracing插件比Firehouse多了一层GUI的包装，更容易使用和管理。

##### 新增trace时参数含义

- Name:即将创建的trace的名称

- Format:表示输出消息日志的格式，有Text和JSON两种，Text格式方便人类阅读，JSON格式方便程序解析

- Max payload bytes:表示每条消息的最大限制，单位为B。比如设置了此值为10，那么当有超过10B的消息经过RabbitMQ流转时就会被载断，如：trace test payload会被载断成trace test.

- Pattern用来设置匹配的模式，和Firehose类似，详解如下：

  "publish.#"匹配发送至所有交换器的消息

  “deliver.#"匹配消费所有队列的消息

  “#”包含“publish.#"和“deliver.#"

  "publish.test_exchange"匹配发送到指定交换器的消息

  “deliver.test_queue"匹配消费指定队列的消息

