### RabbitMQ学习笔记：节点名称详解、rabbitmq-server、及rabbitmq-env.conf

## rabbitmq-server

> rabbitmq-server 启动一个RabbitMQ节点

##### 1.rabbitmq-server在前端启动一个RabbitMQ节点，示例如下：

```
[root@rabbit3 rabbitmq]# rabbitmq-server

  ##  ##      RabbitMQ 3.8.1
  ##  ##
  ##########  Copyright (c) 2007-2019 Pivotal Software, Inc.
  ######  ##
  ##########  Licensed under the MPL 1.1. Website: https://rabbitmq.com

  Doc guides: https://rabbitmq.com/documentation.html
  Support:    https://rabbitmq.com/contact.html
  Tutorials:  https://rabbitmq.com/getstarted.html
  Monitoring: https://rabbitmq.com/monitoring.html

  Logs: /var/log/rabbitmq/rabbit5.log
        /var/log/rabbitmq/rabbit5_upgrade.log

  Config file(s): (none)

  Starting broker... completed with 3 plugins.
```

##### 2.停止RabbitMQ节点可以使用rabbitmqctl

```
rabbitmqctl stop
```



##### 2.启动RabbitMQ节点可以加上-detached参数以后端模式启动

```
rabbitmq-server -detached
```

加上-detached参数会有一个Warning: PID file not written; -detached was passed.警告。官方是这样解释的：

```
Start the server process in the background. Note that this will cause the pid not to be written to the pid file.
For example, runs RabbitMQ AMQP server in the background:
```



## Environment(环境变量)

##### 1.环境变量配置文件rabbitmq-env.conf

> 默认配置在/etc/rabbitmq目录下，如果不存在直接创建就可以，RabbitMQ应用会自动加载；rabbitmq-env.conf包含重写RabbitMQ脚本和CLI工具中内置的默认值的环境变量。

该文件由系统shell解释，因此应包含一系列shell环境变量定义。允许使用普通的shell语法（因为文件的源代码是使用shell“.”运算符），包括以#号开头的行注解。



按照优先级顺序，启动脚本从Shell环境变量（Environmen）、rabbitmq-env.conf和最后从内置的默认值获取它们的值。例如：对于RABBITMQ_NODENAME设置，首先检查Shell环境变量中的RABBITMQ_NODENAME.如果不存在或等于空字符串，则选中rabbitmq-env.conf中的NODENAME。如果它也不存在或设置为等于空字符串，则使用启动脚本中的默认值。



rabbitmq-env.conf中的变量名始终等于环境变量名，去掉了RABBITMQ_前缀；环境变量中的RABBITMQ_NODE_PORT在rabbitmq-env.conf中的名字是NODE_PORT。

##### 2.RABBITMQ_NODENAME（节点名称）

节点名称默认前缀是rabbit,默认是rabbit@后面加上计算机的主机名，可以在同一个主机上运行多个节点，在集群中每个节点必须有一个唯一的RABBITMQ_NODENAME。



RabbitMQ节点由节点名称标识，节点名称由两部分组成，前缀（通常是rabbit）和主机名，例如：rabbit@rabbit1是一个节点名包含前缀rabbit和主机名rabbit1。



在一个集群中节点名称必须是唯一的。如果在给定的主机上运行多个节点（开发和QA环境中通常是这种情况），它们必须使用不同的前缀，例如：rabbit1@hostname和rabbit2@hostname



在集群中，节点使用节点名称标识和联系彼此，这意味着必须解析每个节点名的主机名部分。CLI工具也使用节点名称标识和寻址节点。



当节点启动时，它会检查是否已为其分配了节点名。这是通过RABBITMQ_NODENAME环境变量配置，如果环境变量没有配置，则节点将解析其主机名并在其前面添加rabbit以计算其节点名。



如果系统使用完全限定名（FQDNS Fully Qualified Domain Name）作为主机名，RabbitMQ节点和CLI工具必须配置为使用所谓的长节点名称，对于服务器节点，这是通过将RABBITMQ_USE_LONGNAME环境变量设置为true来完成的。

示例：rabbit@rabbit1.qq.com  其中rabbit为前缀，rabbit1是主机名，qq.com为节点的域名



对于CLI工具，必须设置RABBITMQ_USE_LONGNAME或指定--longnames选项。

##### 3.RABBITMQ_CONFIG_FILE

默认节点配置文件路径是在/etc/rabbitmq/rabbitmq.conf.

##### 4.RABBITMQ_MNESIA_BASE

默认为/var/lib/rabbitmq/mnesia。节点数据目录将位于（或创建）在此目录中。

##### 5.RABBITMQ_NODE_IP_ADDRESS

默认情况下，RabbitMQ将绑定到所有可用的IPV6和IPv4接口。此变量将节点限制为一个网络接口或地址族。

##### 6.RABBITMQ_NODE_PORT

AMQP 0-9-1 和AMQP 1.0端口。默认是5672。

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service)