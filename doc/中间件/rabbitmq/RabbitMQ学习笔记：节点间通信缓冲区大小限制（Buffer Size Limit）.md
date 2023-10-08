### RabbitMQ学习笔记：节点间通信缓冲区大小限制（Buffer Size Limit）

节点间连接对待发送的数据使用缓冲区，当缓冲区达到最大允许容量时，对节点间通信量应用临时限制。该限制是通过环境变量RABBITMQ_DISTRIBUTION_BUFFER_SIZE来设置，单位是kilobytes,默认值是128MB(
128000KB)。

在具有大量节点间通信量的集群中，增加此值可能会对吞吐量产生积极影响。不建议小于64MB的值。

环境变量配置方式，在/etc/rabbitmq/rabbitmq-env.conf配置文件中加上如下配置：

```
#单位KB
DISTRIBUTION_BUFFER_SIZE=128000
```

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service)

