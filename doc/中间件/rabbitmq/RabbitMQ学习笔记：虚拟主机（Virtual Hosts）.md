### RabbitMQ学习笔记：虚拟主机（Virtual Hosts）

##### 简介

RabbitMQ是一个多租户系统，connetions、exchange、queues、bindings、user permissions、policies和其它一些属于虚拟主机的东西；

##### Virtual vhost

虚拟主机（vhost）提供逻辑分组和资源分离。每一个vhost本质上是一个mini版的RabbitMQ服务器，拥有自己的connection、exchange、queue、binding等，拥有自己的权限。vhost之于RabbitMQ就像虚拟机于物理机一样，他们通过在各个实例间提供逻辑上分离，允许为不同的应用程序安全保密的运行数据。

##### vhost和客户端连接

vhost主机具有名称，当AMQP 0-9-1客户端连接到RabbitMQ时，它将指定要连接的虚拟主机名称。如果身份验证成功并且提供的用户名被授予了对虚拟主机的权限，则建立连接。

与虚拟主机的连接只能在该虚拟主机中的交换器、队列、绑定等上运行。仅当应用程序同时连接到两个虚拟主机时，才可能在不同的虚拟主机中进行队列和交换器的“互联”。例如：一个应用程序可以从一个虚拟主机中获取数据，然后发送到另外一个虚拟主机。

##### CLI工具创建虚拟主机

- 列举所有虚拟主机

```
 rabbitmqctl list_vhosts
```

- 添加虚拟主机

```
rabbitmqctl add_vhost <vhost_name>
```

- 删除虚拟主机

```
rabbitmqctl delete_vhost <vhost_name>
```

- 添加用户

```
rabbitmqctl add_user <username> <password>
```

- 设置用户标签

```
rabbitmqctl set_user_tags <username> <tag>
```

1. management:用户可以访问管理插件
2. policymaker:用户可以访问管理插件，并管理它们有权访问的vhost的策略和参数
3. monitoring:用户可以访问管理插件，查看所有连接和通道以及与节点相关的信息
4. administrator:用户可以执行monitoring可以执行的所有操作，管理用户、vhosts和权限，关闭其它用户的连接，以及管理所有vhosts的策略和参数。

- 设置用户权限

```
rabbitmqctl set_permissions [-p <vhost>] <user> <conf> <write> <read>
```

权限设置包括：配置（队列和交换器的创建和删除）、写（发布消息）、读（有关消息的任何操作，包括清除这个队列）；

1. conf:一个正则表达式match哪些配置资源能够被该用户访问。
2. write:一个正则表达式match哪些配置资源能够被该用户读。
3. read:一个正则表达式match哪些配置资源能够被该用户访问 。

- 查看用户列表

```
rabbitmqctl list_users
```

##### 删除用户

```
rabbitmqctl delete_user Username
```

##### 修改用户密码

```
rabbitmqctl change_password Username Newpassword
```

##### 配置最大连接限制

要限制vhost为vhost_name的并发客户端连接总数，请使用一下限制定义：

```
rabbitmqctl set_vhost_limits -p vhost_name '{"max-connections": 256}'
```

要禁用客户端与虚拟主机的连接，请将限制设置为零：

```
rabbitmqctl set_vhost_limits -p vhost_name '{"max-connections": 0}'
```

要取消限制，请将其设置为负值：

```
rabbitmqctl set_vhost_limits -p vhost_name '{"max-connections": -1}'
```

##### 配置最大队列数

要限制vhost为vhost_name的队列总数，请使用以下限制：

```
rabbitmqctl set_vhost_limits -p vhost_name '{"max-queues": 1024}
```

要取消限制，请将其设置为：

```
rabbitmqctl set_vhost_limits -p vhost_name '{"max-queues": -1}'
```

GitHub地址：[https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%9A%E8%99%9A%E6%8B%9F%E4%B8%BB%E6%9C%BA%EF%BC%88Virtual%20Hosts%EF%BC%89.md](https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%9A%E8%99%9A%E6%8B%9F%E4%B8%BB%E6%9C%BA%EF%BC%88Virtual%20Hosts%EF%BC%89.md)