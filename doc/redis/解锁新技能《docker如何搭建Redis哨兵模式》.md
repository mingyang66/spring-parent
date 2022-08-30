### 解锁新技能《docker如何搭建Redis哨兵模式》

> 在我们搭建Redis哨兵架构时我们先了解一些Redis及其相关的一些知识；

##### 问题：Redis是什么？

Redis是C语言开发的一个开源的，遵从BSD协议的高性能键值对（key-value）内存数据库，可以用作缓存、数据库、消息中间件等。

它是一种NoSQL(Not-only sql，泛指非关系型数据库)的数据库。

1.性能优秀，数据在内存中，读写速度非常快，支持10W+ QPS.
2.单进程单线程，是线程安全的，采用IO多路复用机制。
3.丰富的数据类型，支持字符串（strings）、哈希（hashes）、列表（lists）、集合（sets）、有序集合（sorted sets）等。
4.支持数据持久化，可以将内存中数据保存到磁盘上，重启时加载。
5.支持单机模式、集群、哨兵模式。
6.可以作为分布式锁。
7.可以作为消息中间件使用，支持发布订阅。

##### 问题：Redis为何这么快？

官方提供的数据是10w+的QPS(每秒内的查询数)，Redis是单进程单线程模型，完全基于内存操作，CPU不是Redis的瓶颈，Redis的瓶颈最有可能是机器内存的大小或者网络的带宽。
Redis单线程为什么还能这么快？
1.Redis完全基于内存，绝大多数请求是纯粹的内存操作，非常迅速，数据存在内存中，类似于HashMap,HashMap的优势就是查找和操作的时间复杂度都是O(1)。
2.数据结构简单，对数据操作也简单。
3.采用单线程，避免了不必要的上下文切换和竞争条件，不存在多线程导致的CPU切换，不用去考虑各种锁的问题，不存在加锁和释放锁操作，没有死锁问题导致的性能消耗。
4.使用多路复用IO模型，非阻塞IO.

##### 问题：什么是主从复制？

主从复制是指将一台Redis服务器的数据，复制到其它的Redis服务器；主从复制是哨兵和集群模式实施的基石，前者称之为主节点(master)，后者称为从节点（slave）,数据的复制是单向的，只能由主节点到从节点。

默认情况下，每台Redis服务器都是主节点，且一个主节点可以有0个或多个从节点，但是一个从节点只能有一个主节点。一般主节点负责接收写请求，从节点负责接收读请求，从而实现读写分离。

主从一般部署在不同机器上，复制时存在网络延时问题，可以通过参数repl-disable-tcp-nodelay选择是否关闭TCP_NODELAY，默认关闭：
  关闭：无论数据大小都会及时同步到从节点，占带宽，适用于主从网络好的场景；
  开启：主节点每隔指定时间合并数据为TCP包节省带宽，默认为40毫秒同步一次，适用于网络环境复杂或带宽紧张的环境。

主从复制的作用：
  数据冗余：主从复制实现了数据的热备份，是持久化之外的一种数据冗余方式。
  故障恢复：当主节点出现问题时，可以由从节点提供服务，实现快速的故障恢复。
  负载均衡：在主从复制的基础上，配合读写分离，可以由主节点提供写服务，从节点提供读服务，分担服务器负载；尤其是在写少读多的场景下，通过多个从节点分担读负载，可以大大提高Redis服务器的并发量。
  读写分离：主库写、从库读，读写分离不仅可以提高服务器的负载能力，同时可根据需求的变化，改变从库的数量。
  高可用基石：除了上述作用外，主从复制还是哨兵和集群能够实施的基石。

##### 问题：什么是哨兵（sentinel）？

哨兵（sentinel），用于对主从结构中的每一台服务器进行监控，当主节点出现故障后通过投票机制来挑选新的主节点，并且将所有的从节点连接到新的主节点上。
主从复制是最基础的一种提升Redis服务器稳定性的一种实现方式，但是我们也看到master节点也只有一台，若主节点挂了，所有从服务器都不会有新的数据进来，如何也让主节点实现高可用，当主节点跌机以后自动的从从节点中选举一台节点提升为主节点就是哨兵要实现的功能。
作用：
  监控：监控主从节点的运行情况。
  通知：当监控节点出现故障，哨兵之间进行通信。
  自动故障转移：当监控到主节点故障跌机后，断开与跌机主节点连接的所有从节点，然后从从节点中选取一个作为主节点，将其它从节点连接到这个最新的主节点，最后通知客户端最新的服务器地址。

哨兵也是一台redis服务器，只是不对外提供服务；哨兵节点最少要三台且必须为单数，所以必须是三台及以上的单数。  

##### 问题：为何要使用哨兵模式？

Redis单机模式无法实现读取操作的负载均衡和读写分离，无法保证服务的高可用。

##### 问题：什么是Redis雪崩？

Redis缓存大面积失效过期，直接打到DB上，将DB打崩了；举个例子：电商首页所有key的失效时间都是12小时，中午12点刷新的，晚上零点有个大促活动大量用户涌入，假设每秒6000个请求，本来缓存可以扛着全部或者5000个缓存，但是缓存中的key都失效了，此时6000/秒的请求全部落在了数据库上，这时数据库扛不住，直接挂掉。

##### 问题：什么是缓存穿透？

缓存穿透就是缓存中和数据库中都没有数据，用户每次发起请求都会打到DB上。

##### 问题：什么是缓存击穿？

缓存击穿有点类似雪崩，不同的是雪崩是指大面积的Key过期，请求直接将DB打崩了；而击穿是指一个key，在大并发的场景下不停的扛着大量请求，当这个key突然失效的瞬间，这些请求直接落到了数据库上，就在这个key的点上击穿了缓存。

##### 问题：Redis分布式锁存在的一些问题

1.在高并发下的分布式锁实现中，key的过期时间不能设置的太长，否则发生异常未正常释放锁就导致后续线程一直在等待锁。
2.分布式锁的过期时间不能太短，否则锁过期了，持有该锁的线程还未执行完任务；接着下一个线程获取到该锁，这时候前一个线程执行完成后触发del释放该锁，其实这个把锁这个时候是另外一个线程持有；

##### 问题：什么是布隆过滤器？

一种数据结构，由一串很长的二进制向量组成，可以将其看成一个二进制数组；里面存的是0或1，初始默认值都是0；

##### 如何向布隆过滤器中添加数据？

当要向布隆过滤器中添加一个元素时，我们通过多个hash函数算出多个个值，然后将这些值所在的方格置为1。

##### 如何判定数据是否存在？

知道了如何向布隆过滤器中添加一个数据，那么新来了一个数据，我们如何判断是否存在于这个布隆过滤器中呢？
很简单，我们只需要将这个新的数据通过上面自定义的几个hash函数，分别算出各个值，看其对应的地方是否都是1，如果存在一个不是1的情况，那么我们就可以判断该新数据一定不存在于这个布隆过滤器中。

结论：布隆过滤器可以判断某一个数据一定不存在，但是无法判断一定存在。

##### docker搭建Redis哨兵模式

1. 首先去https://github.com/redis/redis下载完整版本的redis.conf和sentinel.conf配置文件；

2. 建三个文件夹6379、6380、6381分别将redis.conf文件复制一份到文件夹下，并新建一个data文件夹；

3. 修改6379主节点配置文件

   ```
   port 6379
   replica-announce-ip 172.30.71.xx
   replica-announce-port 6379
   requirepass emily123
   ```
   
4. 修改两个从节点配置文件

   ```
   port 6379
   replicaof 172.30.71.xx 6379
   masterauth emily123
   replica-announce-ip 172.30.71.xx
   replica-announce-port 6379
   requirepass emily123
   ```

5. 使用如下命令启动主从集群

   ```sh
   docker run -p 6379:6379 --name redis-6379 \
   -e TZ=Asia/Shanghai \
   -v /Users/xx/Documents/IDE/redis/masterSlave/6379/redis.conf:/etc/redis/redis.conf \
   -v /Users/xx/Documents/IDE/redis/masterSlave/6379/data:/data \
   --privileged=true \
   -d redis:7.0.4 redis-server /etc/redis/redis.conf
   
   
   docker run -p 6380:6379 --name redis-6380 \
   -v /Users/xx/Documents/IDE/redis/masterSlave/6380/redis.conf:/etc/redis/redis.conf \
   -v /Users/xx/Documents/IDE/redis/masterSlave/6380/data:/data \
   --privileged=true \
   -e TZ=Asia/Shanghai \
   -d redis:7.0.4 redis-server /etc/redis/redis.conf
   
   
   docker run -p 6381:6379 --name redis-6381 \
   -v /Users/xx/Documents/IDE/redis/masterSlave/6381/redis.conf:/etc/redis/redis.conf \
   -v /Users/xx/Documents/IDE/redis/masterSlave/6381/data:/data \
   --privileged=true \
   -e TZ=Asia/Shanghai \
   -d redis:7.0.4 redis-server /etc/redis/redis.conf
   ```

   验证：

   ```sh
   docker exec -it 2dea6731a8ef redis-cli -p 6379
   
   127.0.0.1:6379> info replication
   # Replication
   role:master
   connected_slaves:2
   slave0:ip=172.30.71.xx,port=6380,state=online,offset=13973049,lag=1
   slave1:ip=172.30.71.xx,port=6381,state=online,offset=13973049,lag=1
   master_failover_state:no-failover
   master_replid:3b0395a39938d4231b97200e5e5fb66a572558f6
   master_replid2:9c034f980bf56ee14234ce1c180456ca1c9319eb
   master_repl_offset:13973327
   second_repl_offset:12707620
   repl_backlog_active:1
   repl_backlog_size:1048576
   repl_backlog_first_byte_offset:12912020
   repl_backlog_histlen:1061308
   ```

   

6. 建三个哨兵节点配置文件26379、26380、26382三个文件夹，并将sentinel.conf配置文件复制到三个文件夹下；

7. 修改三个哨兵配置文件sentinel.conf

   ```
   port 26379
   sentinel announce-ip "172.30.71.xx"
   sentinel announce-port 26379
   sentinel monitor mymaster 172.30.71.xx 6379 2
   sentinel auth-pass mymaster emily123
   sentinel down-after-milliseconds mymaster 30000
   ```

8. 执行如下命令创建哨兵节点

   ```sh
   docker run -it --name sentinel-26379 -p 26379:26379 \
   -v /Users/xx/Documents/IDE/redis/sentinel/26379/:/etc/redis/ \
   --privileged=true \
   -e TZ=Asia/Shanghai \
   -d redis:7.0.4 redis-sentinel /etc/redis/sentinel.conf
   
   docker run -it --name sentinel-26380 -p 26380:26379 \
   -v /Users/xx/Documents/IDE/redis/sentinel/26380/:/etc/redis/ \
   --privileged=true \
   -e TZ=Asia/Shanghai \
   -d redis:7.0.4 redis-sentinel /etc/redis/sentinel.conf
   
   docker run -it --name sentinel-26381 -p 26381:26379 \
   -v /Users/xx/Documents/IDE/redis/sentinel/26381/:/etc/redis/ \
   --privileged=true \
   -e TZ=Asia/Shanghai \
   -d redis:7.0.4 redis-sentinel /etc/redis/sentinel.conf
   ```

9. 哨兵集群模式搭建完成之后可以通过如下命令验证

   ```sh
   docker exec -it 2ad3bb0be5fe redis-cli -p 26379
   
   127.0.0.1:26379> info sentinel
   # Sentinel
   sentinel_masters:1
   sentinel_tilt:0
   sentinel_tilt_since_seconds:-1
   sentinel_running_scripts:0
   sentinel_scripts_queue_length:0
   sentinel_simulate_failure_flags:0
   master0:name=mymaster,status=ok,address=172.30.71.xx:6379,slaves=2,sentinels=3
   ```



##### 问题：哨兵起来之后报如下警告：

```sh
1:X 13 Aug 2022 02:06:56.730 # Could not rename tmp config file (Device or resource busy)
1:X 13 Aug 2022 02:06:56.732 # WARNING: Sentinel was not able to save the new configuration on disk!!!: Device or resource busy

```

解决方案：将文件映射改为目录映射
-v /Users/xx/Documents/IDE/redis/sentinel/26382/sentinel.conf:/etc/redis/sentinel.conf

##### 问题：如何查询sentinel主节点的配置信息？

```sh
SENTINEL master mymaster
```

##### 问题：redis和sentinel如何指定强制绑定IP和端口号？因为默认都是docker容器内的内网IP地址?

```sh
主从复制指定副本IP和PORT方式：
replica-announce-ip 172.30.71.xx
replica-announce-port 6379
哨兵指定副本IP和PORT方法：
sentinel announce-ip 172.30.71.xx
sentinel announce-port 26379
```

##### 问题：sentinel哨兵中什么是sDown和oDown？

- sDown(Subjectively Down)主观下线，即：一个哨兵自己认为一个master下线了，那么就是主观跌下线；
- oDown(Objectively Down)客观下线，即：如果quorum数量的哨兵都觉得master下线了，那么就是客观下线；
- sDown达成条件其实很简单，如果一个哨兵ping一个master节点，超过sentinel down-after-milliseconds <master-name> <milliseconds>指定的毫秒数之后就认为maseter主观下线；
- sDown转oDown的条件其实很简单，如果一个哨兵在指定的时间内，收到了quorum指定数量的其它哨兵也认为那个master是sDown了，那么就认为是oDown了，客观认为下线了；
- sentinel down-after-milliseconds <master-name> <milliseconds>

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)