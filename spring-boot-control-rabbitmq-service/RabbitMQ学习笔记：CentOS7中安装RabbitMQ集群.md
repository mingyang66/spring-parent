### RabbitMQ学习笔记：CentOS7中安装RabbitMQ集群

> 安装rabbitmq的前提是 在CentOS7中，如果是在docker中安装，需要先创建镜像

```
docker pull centos:7
```

创建CentOS7服务器centos7

```
docker run -itd --hostname rabbit1 --name centos7 -p 15672:15672 -p 5672:5672 -p 4369:4369 -p 25672:25672 centos:7
```

创建CentOS7服务器centos8

```
docker run -itd --hostname rabbit2 --name centos8 -p 15673:15672 -p 5673:5672 -p 4363:4369 -p 25673:25672 --link centos7:rabbit1  centos:7
```

创建CentOS7服务器centos9

```
docker run -itd --hostname rabbit3 --name centos9 -p 15674:15672 -p 5674:5672 -p 4364:4369 -p 25674:25672 --link centos7:rabbit1 --link centos8:rabbit2  centos:7
```

生产环境是直接部署在CentOS7上，上面的步骤可以直接忽略；

## erlang安装

第一种方案：

##### 1.从 https://github.com/rabbitmq/erlang-rpm 中可以查看到.repo的配置，选择如下版本

-  To use Erlang 22.x on CentOS 7: 

```
# In /etc/yum.repos.d/rabbitmq-erlang.repo
[rabbitmq-erlang]
name=rabbitmq-erlang
baseurl=https://dl.bintray.com/rabbitmq-erlang/rpm/erlang/22/el/7
gpgcheck=1
gpgkey=https://dl.bintray.com/rabbitmq/Keys/rabbitmq-release-signing-key.asc
repo_gpgcheck=0
enabled=1
```

- 打开CentOS-Base.repo文件

```
vi /etc/yum.repos.d/CentOS-Base.repo
```

将上一步中的rabbitmq-erlang配置添加到CentOS-Base.repo文件之中

- 安装erlang

```
yum install erlang
```

- 验证erlang是否安装成功

```
[root@f1239428339c /]# erl
Erlang/OTP 22 [erts-10.5.5] [source] [64-bit] [smp:2:2] [ds:2:2:10] [async-threads:1] [hipe]

Eshell V10.5.5  (abort with ^G)
1>
```

- ##### 出现上述信息说明安装成功

第二种方案：

从https://dl.bintray.com/rabbitmq-erlang/rpm/erlang/22/el/7下载erlang-22.1.8-1.el7.x86_64.rpm包

从https://dl.bintray.com/rabbitmq/Keys/rabbitmq-release-signing-key.asc下载rabbitmq-release-signing-key.asc签名秘钥

```
rpm --import rabbitmq-release-signing-key.asc  #导入rpm库的密钥
```

```
rpm -ivh erlang-21.2.4-1.el7.centos.x86_64.rpm  #安装erlang包
```

## RabbitMQ安装

##### 2.RabbitMQ下载，下载地址是

 https://github.com/rabbitmq/rabbitmq-server/releases/tag/v3.8.0 

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191126132137716.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9taW5neWFuZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)



- 可以手动下载指定的版本然后再上传到CentOS7系统上的home目录下
- 使用wget下载指定版本的rabbitmq到CentOS7系统上的home目录下

如果wget不存在则需要安装：

```
yum install wget
```

下载rabbitmq3.8.0版本

```
#切换到home目录
cd /home
##下载RabbitMQ
wget https://github.com/rabbitmq/rabbitmq-server/releases/download/v3.8.0/rabbitmq-server-3.8.0-1.el7.noarch.rpm
```

##### 3.安装rabbitmq

切换到CentOS7系统的home目录下，执行如下命令

```
yum install rabbitmq-server-3.8.0-1.el7.noarch.rpm
```

##### 4.启动web管理页面，启动rabbitmq-management插件

```
rabbitmq-plugins enable rabbitmq_management
```

如果系统提示找不到，则使用如下查看插件名称：

```
rabbitmq-plugins list
```
启动RabbitMQ应用服务并顺带启动Erlang虚拟机：

```
##后台启动节点
rabbitmq-server start -detached
```

关闭RabbitMQ应用服务及Erlang虚拟机：

```
rabbitmqctl stop
```

 停止RabbitMQ应用服务,但Erlang虚拟机会继续运行.此命令主要用于优先执行其它管理操作（这些管理操作需要先停止RabbitMQ应用服务），如. [**reset**](http://www.rabbitmq.com/man/rabbitmqctl.1.man.html#reset).例如： 

```
rabbitmqctl start_app
```

启动RabbitMQ应用服务.

此命令典型用于在执行了其它管理操作之后，重新启动停止的RabbitMQ应用服务。如[**reset**](http://www.rabbitmq.com/man/rabbitmqctl.1.man.html#reset).例如：

```
rabbitmqctl stop_app
```



##### 5.增加访问用户， 默认用户guest只能本地访问

```
rabbitmqctl add_user admin admin
```

设置角色：

```
rabbitmqctl set_user_tags admin administrator
```

设置默认vhost("/")访问权限

```
rabbitmqctl set_permissions -p "/" admin "." "." ".*"
```





##### =========重复上面的步骤分别在不同的服务器上安装Erlang和RabbitMQ====================

##### 接下来在三台主机rabbit1(主机名)、rabbit2(主机名)、rabbit3(主机名)上的hosts文件添加IP地址和节点（主机名）的映射信息

```
vi /etc/hosts
```

配置IP和主机名的映射：

```
172.30.67.121  rabbit1
172.30.67.122  rabbit2
172.30.67.123  rabbit3
```

查看主机名：

```
[root@rabbit3 /]# hostname
rabbit3
```

查看CentOS版本号：

```
[root@rabbit3 /]# cat /etc/centos-release
CentOS Linux release 7.7.1908 (Core)
```



##### 以rabbit1节点为主节点，停止rabbit2、rabbit3节点的服务，命令如下：

```
rabbitctl stop
```

复制rabbit1主机节点的.erlang.cookie值,确保三个节点的秘钥令牌相同，cookie相当于秘钥令牌，集群中的RabbitMQ节点需要通过交换秘钥令牌以获得相互认证；

复制秘钥要执行如下指令：

```
chmod 777 /var/lib/rabbitmq/.erlang.cookie ##修改文件读写权限
vi /var/lib/rabbitmq/.erlang.cookie        ##修改秘钥值
chmod 400 /var/lib/rabbitmq/.erlang.cookie ##cookie文件权限改为400
```

.erlang.cookie的路径在/var/lib/rabbitmq/.erlang.cookie或者$HOME/.erlang.cookie;

执行如下命令加入集群：

```
rabbitctl join_cluster rabbit@rabbit1
```

## 常用操作命令

- 6.设置集群名称

```
rabbitmqctl set_cluster_name rabbit@rabbit_cluster
```

- 7节点名称修改

```
rabbitmqctl  rename_cluster_node   oldnode1  newnode1  [oldnode2  newnode2]  [oldnode3  newnode3...]
```

- 8.删除集群中的节点

```
##添加--offline参数表示当前的节点处于非运行状态时一样可以剔除指定节点
rabbitmqctl  forget_cluster_node  NodeName [--offline] 
```

- 9.CentOS7查看主机名

```
[root@rabbit2 ~]# hostname
rabbit2
```

- 10.查看集群状态

```
rabbitctl cluster_status
```

- 11.更改集群节点类型

```
rabbitmqctl change_cluster_node_type [disk|ram]
```

- 12.清空节点的状态，并将其恢复到空白状态

```
rabbitmqctl reset ##可以用在将一个节点从集群中移除出来
```

- 13.查看节点的日志

```
##日志存放在/var/log/rabbitmq目录下
tail -f /var/log/rabbitmq/rabbit\@rabbit3.log
```

- 14.修改用户密码

```
rabbitmqctl change_password [username] [new_password]
```

GitHub地址：[https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%9ACentOS7%E4%B8%AD%E5%AE%89%E8%A3%85RabbitMQ%E9%9B%86%E7%BE%A4.md](https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%9ACentOS7%E4%B8%AD%E5%AE%89%E8%A3%85RabbitMQ%E9%9B%86%E7%BE%A4.md)