### RabbitMQ学习笔记：rabbitmq-server -detached Warning: PID file not written; -detached was passed

##### 1.停止Erlang节点及节点上的RabbitMQ应用

```
[root@rabbit3 /]# rabbitmqctl stop
warning: the VM is running with native name encoding of latin1 which may cause Elixir to malfunction as it expects utf8. Please ensure your locale is set to UTF-8 (which can be verified by running "locale" in your shell)
Stopping and halting node rabbit@rabbit3 ...
```

##### 2.启动Erlang节点及节点上的RabbitMQ应用

```
[root@rabbit3 /]# rabbitmq-server -detached
Warning: PID file not written; -detached was passed.
```

或者

```
[root@rabbit3 /]# rabbitmq-server start -detached
Warning: PID file not written; -detached was passed.
```

启动的时候报Warning: PID file not written; -detached was
passed.警告，经过多方努力查找到了原因，[官网是这样解释的](https://www.rabbitmq.com/rabbitmq-server.8.html#OPTIONS)：

```
Start the server process in the background. Note that this will cause the pid not to be written to the pid file.
For example, runs RabbitMQ AMQP server in the background:

rabbitmq-server -detached
```

也就是说只要加上-detached参数，RabbitMQ应用启动运行就会包上面的警告信息,应该是服务的遗留bug.

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service)