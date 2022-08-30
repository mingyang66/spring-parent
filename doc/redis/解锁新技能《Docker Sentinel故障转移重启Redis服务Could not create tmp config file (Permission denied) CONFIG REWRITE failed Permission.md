### 解锁新技能《Docker Sentinel故障转移重启Redis服务Could not create tmp config file (Permission denied) CONFIG REWRITE failed: Permission denied》

在本机搭建Docker Sentinel模式测试故障转移时，刚开始可以自动的执行故障转移，但是演练了几个节点后故障转移成功，但是原先的master节点不可以和其它节点连接，通过查看控制台日志发现了如下错误：

```
1:S 26 Aug 2022 15:04:14.854 # Could not create tmp config file (Permission denied)

1:S 26 Aug 2022 15:04:14.856 # CONFIG REWRITE failed: Permission denied

1:S 26 Aug 2022 15:04:14.860 * Non blocking connect for SYNC fired the event.
```

在Redis 官方GitHub上发现一个ISSUE：https://github.com/redis/redis/issues/8172

> Hi [@bonikforever](https://github.com/bonikforever), thanks for reporting this. This is a result of a bugfix that introduced a regression when the directory where the configuration file resides cannot be written to (which is more or less the case with a single file bindmount).
>
> You can work around this easily by bind mounting the entire directory. For example:

```sh
mkdir -p ${PWD}/sentinel_conf
chmod -R 0777 ${PWD}/sentinel_conf
docker run -v ${PWD}/sentinel_conf:/usr/local/etc/redis redis:6.0.9 redis-server /usr/local/etc/redis/sentinel.conf --sentinel
```

参照上述方案执行如下命令：

```sh
chmod -R 0777 redis.conf 
```

启动该节点发现集群正常运行了，问题完美解决。



GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)