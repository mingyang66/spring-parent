### RabbitMQ学习笔记：内置Prometheus支持rabbit_prometheus插件

从3.8.0开始，RabbitMQ提供内置的Prometheus和Grafana支持。

[rabbitmq_prometheus](https://github.com/rabbitmq/rabbitmq-prometheus)
插件中提供了对Prometheus指标收集的支持。该插件以Prometheus文本格式在专用的TCP端口（默认端口15692）上公开所有RabbitMQ指标。

[rabbitmq_prometheus](https://github.com/rabbitmq/rabbitmq-prometheus)
插件是RabbitMQ指标的核心导出器，由RabbitMQ核心团队开发。这是一个干净的设计，用于替换[kbudde/rabbitmq_exporter](https://github.com/kbudde/rabbitmq_exporter),[kbudde/rabbitmq_exporter](https://github.com/kbudde/rabbitmq_exporter)
由Prometheus开发用于RabbitMQ指标收集。

这些指标提供了对RabbitMQ节点和运行时状态的深入了解。它们对使用RabbitMQ的应用程序和各种基础设施元素的行为进行了更深入的推理。

> rabbitmq_prometheus插件是新增的，相对来说还不成熟。它从3.8.0开始在RabbitMQ发行版中发布。

##### 1.rabbitmq_prometheus插件安装

这个插件包含在RabbitMQ3.8.x版本中。与所有的插件一样，必须启用它才能使用；

启用插件：

```
rabbitmq-plugins enable rabbitmq_prometheus
```

关闭插件：

```
rabbitmq-plugins disable rabbitmq_prometheus
```

插件启动成功后可以在web UI上看到开启了15692端口，如下：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200114174532643.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9taW5neWFuZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

可以通过http://localhost:15692/metrics访问插件到处的指标数据：

```
# TYPE erlang_mnesia_held_locks gauge
# HELP erlang_mnesia_held_locks Number of held locks.
erlang_mnesia_held_locks 0
# TYPE erlang_mnesia_lock_queue gauge
# HELP erlang_mnesia_lock_queue Number of transactions waiting for a lock.
erlang_mnesia_lock_queue 0
# TYPE erlang_mnesia_transaction_participants gauge
# HELP erlang_mnesia_transaction_participants Number of participant transactions.
erlang_mnesia_transaction_participants 0
# TYPE erlang_mnesia_transaction_coordinators gauge
# HELP erlang_mnesia_transaction_coordinators Number of coordinator transactions.
erlang_mnesia_transaction_coordinators 0
# TYPE erlang_mnesia_failed_transactions counter
# HELP erlang_mnesia_failed_transactions Number of failed (i.e. aborted) transactions.
erlang_mnesia_failed_transactions 0
# TYPE erlang_mnesia_committed_transactions counter
# HELP erlang_mnesia_committed_transactions Number of committed transactions.
erlang_mnesia_committed_transactions 246
# TYPE erlang_mnesia_logged_transactions counter
# HELP erlang_mnesia_logged_transactions Number of transactions logged.
erlang_mnesia_logged_transactions 312
# TYPE erlang_mnesia_restarted_transactions counter
# HELP erlang_mnesia_restarted_transactions Total number of transaction restarts.
erlang_mnesia_restarted_transactions 1
#下面的省略....
```

##### 2.配置

此导出器通过prometheus.*配置键支持以下选项：

- prometheus.path 定义到处端点，默认是“/metrics”。
- prometheus.tcp.*
  控制匹配的HTTP监听器设置[those used by the RabbitMQ HTTP API](https://www.rabbitmq.com/management.html#configuration)。
- prometheus.ssl.* 控制匹配的TLS(HTTPS)
  监听器设置[those used by the RabbitMQ HTTP API](https://www.rabbitmq.com/management.html#single-listener-https)。

简单示例：

```
# these values are defaults
prometheus.path = /metrics
prometheus.tcp.port =  15692
```

这些配置可以通过rabbitmq的配置文件来修改，配置文件默认路径如下：

```
/etc/rabbitmq/rabbitmq.conf
```

插件地址：https://github.com/rabbitmq/rabbitmq-prometheus/blob/master/metrics.md

指标说明：https://github.com/rabbitmq/rabbitmq-prometheus/blob/master/metrics.md

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service)