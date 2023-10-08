### RabbitMQ学习笔记：内存(Memory)|磁盘空间（Disk space）阀值

> 相关文档指南： https：//rabbitmq.com/memory.html

##### 支持的单位符号

```
## k, kiB: kibibytes (2^10 - 1,024 bytes)
## M, MiB: mebibytes (2^20 - 1,048,576 bytes)
## G, GiB: gibibytes (2^30 - 1,073,741,824 bytes)
## kB: kilobytes (10^3 - 1,000 bytes)
## MB: megabytes (10^6 - 1,000,000 bytes)
## GB: gigabytes (10^9 - 1,000,000,000 bytes)
```

##### 内存警告

RabbitMQ服务器在启动和执行 rabbitmqctl set_vm_memory_high_watermark
的时候会检测机器上的RAM大小，默认情况下，RabbitMQ使用内存超过40%的时候，会发出内存警告，阻塞所有发布消息的连接，一旦警告解除（例如：服务器paging消息到硬盘或者分发消息到消费者并且确认）服务会恢复正常。

默认的内存阀值是40%，注意，这并不会阻止RabbitMQ
Server使用不到40%，仅仅意味着到达这个点的时候，发布者会被阻塞block，最坏的情况下，Erlang虚拟机会引起双倍的内存使用（RAM的80%），强烈建议开启操作系统的SWAP和Page
files.

内存达到阀值后，发布者会被阻塞，但是消费者不会被阻塞，消费者继续消费消息，当内存降低到阀值以下后，发布者继续开始发布消息。

##### 配置内存阀值

```
vm_memory_high_watermark.relative = 0.4
```

- 另外我们可以设置节点使用的RAM的限制（以字节为单位）

```
vm_memory_high_watermark.absolute = 1073741824
```

- 另外你可以使用内存单位设置阀值，如果定义了relative，那么absolute将会被忽略

```
vm_memory_high_watermark.absolute = 2GB
```

rabbitmq ctl工具修改阀值，服务重启后设置失效，如果要永久生效要修改配置文件

```
rabbitmqctl set_vm_memory_high_watermark 0.4
```

- 配置paging阀值

队列中的消息到达阀值上限之前，它会尝试将页面消息page到磁盘上以释放内存；

默认情况下：在broker达到阀值的50%时（默认内存阀值是0.4）会发生这种情况，可以通过修改如下配置进行修改：

```
vm_memory_high_watermark_paging_ratio = 0.5
```

- 禁用所有的发布者

设置阀值（threshold）为0，会立即出发内存警告，阻塞所有的发布连接（如果你希望禁用全局发布）；

```
rabbitmqctl set_vm_memory_high_watermark 0
```

- 经典配置，可以直接配置在advanced.config配置文件中，永久有效

```
[{rabbit, [{vm_memory_high_watermark_paging_ratio, 0.75},{vm_memory_high_watermark, 0.4}]}].
```

##### 磁盘警告

当可用的磁盘空间下降到配置值（最低阀值默认为48MIB,版本3.8.0）之下，会触发所有的警告，所有的生产者会被阻塞，目标是避免填充整个磁盘，这个会导致写操作的失败，导致RabbitMQ中断，为了减小填充磁盘的风险，所有进来的消息都会阻塞；但是消费者不会被阻塞，直到磁盘的可用空间升高到最低阀值之上，生产者就会继续开始推送消息。

##### 配置磁盘阀值

- 低于1.0的值很危险，应谨慎使用，值为1.0代表16GIB

```
disk_free_limit.relative = 2.0
```

- 磁盘可用空间absolute设置，单位：字节

```
disk_free_limit.absolute = 1000000000
```

加上单位

```
disk_free_limit.absolute = 1GB
```

- 使用CTL工具修改阀值，服务重启后设置失效，如果要永久生效要修改配置文件

```
rabbitmqctl set_disk_free_limit 42949672000
```

- 经典配置，mem_relative值为1.0代表16GIB，可以直接配置在advanced.config配置文件中，永久有效

```
[{rabbit, [{disk_free_limit, {mem_relative, 1.0}}]}].
```

RabbitMQ配置文件地址： https://github.com/rabbitmq/rabbitmq-server/blob/master/docs/rabbitmq.conf.example

GitHub地址：[https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%9A%E5%86%85%E5%AD%98(Memory)%E7%A3%81%E7%9B%98%E7%A9%BA%E9%97%B4%EF%BC%88Disk%20space%EF%BC%89%E9%98%80%E5%80%BC.md](https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%9A%E5%86%85%E5%AD%98(Memory)%E7%A3%81%E7%9B%98%E7%A9%BA%E9%97%B4%EF%BC%88Disk%20space%EF%BC%89%E9%98%80%E5%80%BC.md)
