### RabbitMQ学习笔记：RabbitMQ能打开最大连接数

##### 1.通过rabbitmqctl命令查看

```
[root@rabbit1 /]# rabbitmqctl status
...

File Descriptors

Total: 2, limit: 1048469
Sockets: 0, limit: 943620

...
```

##### 2.通过Management UI查看

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191219140111799.png)

Socket descriptors说明：

>  The network sockets count and limit managed by RabbitMQ.
> When the limit is exhausted RabbitMQ will stop accepting new network connections. 

File descriptors说明：

> File descriptor count and limit, as reported by the operating system. The count includes network sockets and file handles.
>
> To optimize disk access RabbitMQ uses as many free descriptors as are available, so the count may safely approach the limit. However, if most of the file descriptors are used by sockets then persister performance will be negatively impacted.
>
> To change the limit on Unix / Linux, use "ulimit -n". To change the limit on Windows, set the ERL_MAX_PORTS environment variable
>
> To report used file handles on Windows, handle.exe from sysinternals must be installed in your path. You can download it [here](https://technet.microsoft.com/en-us/sysinternals/bb896655).

RabbitMQ的Socket连接数（socket descriptors）是文件描述符（File descriptors）的一个子集，也就是说，RabbitMQ能同时打开的最大连接数和最大文件句柄数都是受限于操作系统关于文件描述符数量的限制，两者是此消彼长的关系。

查看CentOS7文件系统可以打开文件句柄数：

```
[root@rabbit1 /]# ulimit -n
1048566
```


GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service)

