### ssh dns反向解析导致登录事件过长

> 最近通过ssh工具连接centos7服务器，发现耗时比较长；经过分析得知是连接时服务器时会将IP地址发送给DNS服务器，反向解析获取其对应的域名；假如经过5秒中还没有收到回复，就再次发送查询，如果第二次还是等了5秒钟没有回复，就彻底放弃查询；由于DNS服务器上没有它的域名配置记录，所以两次查询都等待了5秒钟还没有结果，一共浪费了10秒钟时间。

针对连接时耗时过长的问题有两种解决方案

##### 方案一：在DNS服务器上配置IP地址对应的域名

##### 方案二：禁用IP地址DNS反向解析

查询IP地址DNS反向解析配置：

```sh
[root@localhost ~]# cat /etc/ssh/sshd_config |grep -i usedns
#UseDNS no
```

默认UseDNS配置是注解掉的，其默认值是：

```sh
UseDNS yes
```

将/etc/ssh/sshd_config配置的UseDNS配置为no：

```sh
UseDNS no
```

重启sshd服务：

```sh
systemctl restart sshd
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)