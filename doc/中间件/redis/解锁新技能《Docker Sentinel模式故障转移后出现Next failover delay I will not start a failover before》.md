### 解锁新技能《Docker Sentinel模式故障转移后出现日志Next failover delay: I will not start a failover before》

今天在做Sentinel故障演练时发现哨兵节点打印如下日志：

```sh
1:X 26 Aug 2022 17:25:07.660 # Next failover delay: I will not start a failover before Fri Aug 26 17:31:08 2022
```

> 其意思是下次故障转移将会在Fri Aug 26 17:31:08
> 2022后才会执行，跟前面日期比对相差6分钟；其实意思很明白，在上次故障转移发生后的6分钟内，再次出现节点故障，在6分钟内哨兵不会执行故障转移，会在6分钟后执行；

在sentinel.conf配置文件中有一个配置故障转移超时时间的配置：

```sh
sentinel failover-timeout <master-name> <milliseconds>
```

默认故障转移超时时间3分钟，它有多种用途：

- 在给定的哨兵已经针对同一主机尝试了上一次故障转移后，重新启动故障转移所需的时间是故障转移超时的两倍（对准日志提示这种场景）；
- 根据Sentinel当前配置，Slave从节点从一个错误的Master节点复制副本，到强制使用正确主机进行复制，这正是故障转移的超时时间（从哨兵检测到错误配置的哪一刻起计算）；
- 取消已在进行但未产生任何配置更改的故障转移所需的时间（升级的副本没有得到任何Slave节点确认）
- 正在进行的故障转移等待将所有副本重新配置为新主机副本的最长时间。然而，即使在这一时间之后，复制品仍将由哨兵重新配置，但不具有指定的精确并行同步进程。

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)