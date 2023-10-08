### 解锁新技能《Docker Sentinel NAT存在的问题》

##### NAT(Network Address Translation)端口映射技术

运行在docker容器中的程序可以暴露在宿主机不同的端口号上；Sentinel模式可以指定多个容器内部运行相同的端口号，暴露出不同的端口号给外部服务（有时候会重新映射IP地址）；

运行在docker容器中的sentinel无法通过自动发现的方式和Redis服务和sentinel节点进行通信，应为自动发现的方式找到的IP或端口号都是容器内部的IP和端口号，所以是无法有效的进行通信；

##### Redis服务重映射设置IP和端口号

```
#重新映射IP
replica-announce-ip 5.5.5.5
#重映射Port
replica-announce-port 1234
```

##### Sentinel服务重映射IP和端口号

```
#重映射IP
sentinel announce-ip <ip>
#重映射Port
sentinel announce-port <port>
```

> Redis主从服务和Sentinel通过上述 IP和端口号的重映射，各个节点和哨兵之间就可以正常进行通信，故障转移，节点负载。

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)