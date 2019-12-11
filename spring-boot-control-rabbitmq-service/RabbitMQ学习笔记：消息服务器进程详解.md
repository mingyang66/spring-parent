### RabbitMQ学习笔记：消息服务器进程详解

##### 查看后台rabbitmq服务进程

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191211133613759.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9taW5neWFuZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

> 可以看到一共有五个进程，其中PID为155（/usr/lib64/erlang/erts-10.5.6/bin/epmd -daemon）的进程是Erlang虚拟机的一个守护进程，与RabbitMQ节点一起运行，运行时使用它来发现特定节点监听端口；其它四个节点属于父子节点的关系；

##### 停止Erlang虚拟机和RabbitMQ应用服务

```
#停止erlang虚拟机和RabbitMQ应用服务器
rabbitmqctl stop
```

##### 查看Erlang虚拟机和RabbitMQ应用服务停止后的进程情况

```
[root@rabbit3 /]# ps aux
USER       PID %CPU %MEM    VSZ   RSS TTY      STAT START   TIME COMMAND
root         1  0.0  0.1  11836  2804 pts/0    Ss+  Dec10   0:00 /bin/bash
rabbitmq   155  0.0  0.1  50896  3520 ?        S    Dec10   0:00 /usr/lib64/erlang/erts-10.5.6/bin/epmd -daemon
root     43981  0.0  0.1  11836  3140 pts/1    Ss   02:49   0:00 bash
root     53569  0.0  0.1  51760  3472 pts/1    R+   05:42   0:00 ps aux
```

可以看到erlang虚拟机和RabbitMQ停止服务后，其它四个进程都消失了，只剩下一个epmd守护进程在运行。

##### 仅仅停止RabbitMQ应用服务器，不停止Erlang虚拟机

```
rabbitmqctl stop_app
```

##### 查看进程情况

```
[root@rabbit3 /]# ps auxf
USER       PID %CPU %MEM    VSZ   RSS TTY      STAT START   TIME COMMAND
root      5321  0.0  0.1  11836  3064 pts/1    Ss   07:36   0:00 bash
root      5549  0.0  0.1  51756  3504 pts/1    R+   07:38   0:00  \_ ps auxf
rabbitmq  3711  1.0  4.1 2204860 84368 ?       Sl   07:05   0:20 /usr/lib64/erlang/erts-10.5.6/bin/beam.smp -W w -A 64 -
rabbitmq  3778  0.0  0.0   4364  1360 ?        Ss   07:05   0:00  \_ erl_child_setup 1048576
rabbitmq  3797  0.0  0.0  11600   944 ?        Ss   07:05   0:00      \_ inet_gethost 4
rabbitmq  3798  0.0  0.0  13724  1720 ?        S    07:05   0:00          \_ inet_gethost 4
rabbitmq   162  0.0  0.0  49048  1800 ?        S    06:07   0:00 /usr/lib64/erlang/erts-10.5.6/bin/epmd -daemon
root         1  0.0  0.1  11836  2932 pts/0    Ss+  06:06   0:00 /bin/bash
```

可以看到RabbitMQ应用服务停止的时候进程没有发生任何变化，个人理解rabbit应用是建立在Erlang虚拟机节点之上，rabbit应用启动不会创建单独的进程表现到操作系统。如果有正确的答案请留言，谢谢！

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service)