### RabbitMQ学习笔记：CentOS7中安装RabbitMQ集群

> 安装rabbitmq的前提是 在CentOS7中，如果是在docker中安装，需要先创建镜像

```
docker pull centos:7
```

创建CentOS7服务器centos7

```
docker run -d --privileged=true --hostname rabbit1 --name centos7 -p 15672:15672 -p 5672:5672 -p 4369:4369 -p 25672:25672 -p 15692:15692 -e TZ=Asia/Shanghai centos:7
```

创建CentOS7服务器centos8

```
docker run -d --privileged=true --hostname rabbit2 --name centos8 -p 15673:15672 -p 5673:5672 -p 4363:4369 -p 25673:25672 -p 15693:15692 --link centos7:rabbit1 -e TZ=Asia/Shanghai centos:7
```

创建CentOS7服务器centos9

```
docker run -d --privileged=true --hostname rabbit3 --name centos9 -p 15674:15672 -p 5674:5672 -p 4364:4369 -p 25674:25672 -p 15694:15692 --link centos7:rabbit1 --link centos8:rabbit2 -e TZ=Asia/Shanghai centos:7
```

 - 生产环境是直接部署在CentOS7上，上面的步骤可以直接忽略；
 - 15692端口是为rabbitmq-prometheus插件开放，如果没用到可以去掉；
 - -e TZ=Asia/Shanghai环境变量解决容器时间和系统时间不一致问题
 - -it参数会将当前终端连接到容器当中；-d参数启动容器，并在后台运行

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
 https://github.com/rabbitmq/rabbitmq-server/releases/tag/v3.8.5 

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200618103939618.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhb21pbmd5YW5n,size_16,color_FFFFFF,t_70)![在这里插入图片描述](https://img-blog.csdnimg.cn/20200618103944331.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhb21pbmd5YW5n,size_16,color_FFFFFF,t_70)



- 可以手动下载指定的版本然后再上传到CentOS7系统上的home目录下
- 使用wget下载指定版本的rabbitmq到CentOS7系统上的home目录下

如果wget不存在则需要安装：

```
yum install wget
```

下载rabbitmq3.8.5版本

```
#切换到home目录
cd /home
##下载RabbitMQ
wget https://github.com/rabbitmq/rabbitmq-server/releases/tag/v3.8.5/rabbitmq-server-3.8.5-1.el7.noarch.rpm
```

##### 3.安装rabbitmq

切换到CentOS7系统的home目录下，执行如下命令

```
yum install rabbitmq-server-3.8.5-1.el7.noarch.rpm
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
##后台启动节点，-detached参数是为了能让RabbitMQ服务能以守护进程的方式在后台运行
rabbitmq-server start -detached
```
下面的命令安装过程中不执行：
关闭RabbitMQ应用服务及Erlang虚拟机：

```
# rabbitmqctl stop [pid_file]停止Erlang虚拟机和RabbitMQ应用服务
# rabbitmqctl shutdown 停止Erlang虚拟机和RabbitMQ应用服务，与stop不同之处是会阻塞直到停止成功
# 如果停止失败将会返回一个非0的退出码
rabbitmqctl stop
```

 停止RabbitMQ应用服务,但Erlang虚拟机会继续运行.此命令主要用于优先执行其它管理操作（这些管理操作需要先停止RabbitMQ应用服务），如. [**reset**](http://www.rabbitmq.com/man/rabbitmqctl.1.man.html#reset).例如： 

```
# 启动RabbitMQ应用程序
# rabbitmqctl -n rabbit@hostname start_app 在集群中的一个节点启动另外一个节点的RabbitMQ应用
rabbitmqctl start_app
```

启动RabbitMQ应用服务.

此命令典型用于在执行了其它管理操作之后，重新启动停止的RabbitMQ应用服务。如[**reset**](http://www.rabbitmq.com/man/rabbitmqctl.1.man.html#reset).例如：

```
# 停止RabbitMQ应用程序，但是Erlang VM虚拟机继续运行
# rabbitmqctl -n rabbit@hostname stop_app 在集群中的一个节点停止另外一个节点的RabbitMQ应用
rabbitmqctl stop_app
```



##### 5.增加访问用户， 默认用户guest只能本地访问（只在主节点执行，如：rabbit1）

```
# 新增用户 rabbitmqctl add_user {username} {password}
# 修改用户密码 rabbitmqctl change_password {username} {newpassword}
# 验证用户密码 rabbitmqctl authenticate_user {username} {password}
# 删除用户 rabbitmqctl delete_user {username}
# 列出所有用户，结果为用户名和用户TAG rabbitmqctl list_users
# 修改用户密码 rabbitmqctl change_password [username] [new_password]
rabbitmqctl add_user admin admin
```

设置角色(标签tag)：

```
# 设置用户标签(tag) rabbitmqctl set_user_tags {username} {tag...}
# tag有如下几种选项
# none：无任何角色，新建的用户默认值
# management：可以访问WEB管理界面
# policymaker：包含management的所有权限，并且可以管理策略(Policy)和参数(Parameter)
# monitoring：包含management的所有权限，并且可以看到所有连接、信道和节点信息。
# administrator：包含monitoring的所有权限，并且可管理虚拟主机、用户、权限、策略、参数等，这是最高权限。
rabbitmqctl set_user_tags admin administrator
```

设置默认vhost("/")访问权限

```
# 设置权限rabbitmqctl set_permissions [-p vhost] {user} {conf} {write} {read}
# 举两个列子
# 为rabbitmq用户设置可以对名称为test_vhost的vhost下面的资源可配置，可写，可读rabbitmqctl set_permissions -p test_vhost rabbitmq ".*" ".*" ".*"
# 为rabbitmq用户设置可以对名称为test_vhost的vhost下面的以order开头的资源可配置，所有资源可写，所有资源可读rabbitmqctl set_permissions -p test_vhost rabbitmq "^order.*" ".*" ".*
# conf：此处的值是一个正则表达式，用于匹配用户在哪些资源上拥有可配置权限。
# write：此处的值是一个正则表达式，用于匹配用户在哪些资源上拥有可写入权限。
# read：此处的值是一个正则表达式，用于匹配用户在哪些资源上拥有可读取权限。
rabbitmqctl set_permissions -p "/" admin ".*" ".*" ".*"
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
以下命令安装过程中不执行
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
rabbitmqctl stop
```

复制rabbit1主机节点的.erlang.cookie值,确保三个节点的秘钥令牌相同，cookie相当于秘钥令牌，集群中的RabbitMQ节点需要通过交换秘钥令牌以获得相互认证；

复制秘钥要执行如下指令：
##修改文件读写权限
```
chmod 777 /var/lib/rabbitmq/.erlang.cookie
```
##修改秘钥值
```
vi /var/lib/rabbitmq/.erlang.cookie   
```

 ##cookie文件权限改为400
```
chmod 400 /var/lib/rabbitmq/.erlang.cookie
```

.erlang.cookie的路径在/var/lib/rabbitmq/.erlang.cookie或者$HOME/.erlang.cookie;

启动Erlang虚拟机和RabbitMQ应用：

```
rabbitmq-server start -detached
```
关闭RabbitMQ应用：

```
rabbitmqctl stop_app
```

执行如下命令加入集群：

```
# 指示节点成为指定节点所在集群的成员，在加入集群之前节点要stop_app,并且要reset
# rabbitmqctl join_cluster seed-node [--ram|--disc] 指定加入集群的节点是ram类型还是disc,默认disc
# 节点离开集群，可以使用rabbitmqctl reset或rabbitmqctl -n rabbit@rabbit1 forget_cluster_node rabbit@rabbit3
# rabbit@rabbit1为要离开集群中的一个节点，rabbit@rabbit3为当前节点
rabbitmqctl join_cluster rabbit@rabbit1
```
启动RabbitMQ应用：

```
rabbitmqctl start_app
```
在任意一个节点上执行设置镜像模式命令

```
# 设置集群为镜像队列
# rabbitmqctl set_policy [-p vhost] [--priority priority] [--apply-to apply-to] {name} {pattern} {definition}
rabbitmqctl set_policy -p / --priority 0 --apply-to queues ha-all "^" '{"ha-mode":"all","ha-sync-mode":"automatic"}'
```
查看已经添加的策略

```
rabbitmqctl list_policies
```

## 常用操作命令

- 6.集群相关操作命令

```
#  设置集群名称
rabbitmqctl set_cluster_name rabbit@rabbit_cluster
```
```
#重命名节点名称
rabbitmqctl  rename_cluster_node   oldnode1  newnode1  [oldnode2  newnode2]  [oldnode3  newnode3...]
```

```
# 删除集群上指定的节点，删除是远程端点必须rabbitmqctl stop_app
# --offline 启用从脱机节点删除节点。这只在所有节点都脱机并且最后一个要关闭的节点无法联机的情况下才有用，
# 从而阻止整个集群启动。它不应该在任何其他情况下使用，因为它可能导致不一致
# rabbitmqctl -n rabbit@rabbit1 forget_cluster_node rabbit@rabbit3 在其它节点上操作，从节点rabbit@rabbit1删除节点rabbit@rabbit3
rabbitmqctl forget_cluster_node NodeName [--offline] 
```

```
# 指示已加入集群的节点上线后联系clusternode,这不同于join_cluster,因为它不加入任何集群，
# 它会检查clusternode已经以集群的形式存在于集群中了
# 需要这个命令的动机是当节点离线时，集群可以变化.考虑这样的情况，节点Ａ和节点Ｂ都在集群里边，
# 这里节点Ａ掉线了，Ｃ又和Ｂ集群了，然后Ｂ又离开了集群．当Ａ醒来的时候，它会尝试联系Ｂ，
# 但这会失败，因为Ｂ已经不在集群中了.update_cluster_nodes -n A C 可解决这种场景
# rabbitmqctl update_cluster_nodes clusternode
rabbitmqctl update_cluster_nodes -n rabbit@A rabbit@B rabbit@C
```
```
# 查看集群状态
rabbitmqctl cluster_status
```
```
# 更改集群节点类型
rabbitmqctl change_cluster_node_type [disk|ram]
```
- 7.CentOS7查看主机名

```
[root@rabbit2 ~]# hostname
rabbit2
```


- 8.清空节点的状态，并将其恢复到空白状态

```
# 表示设置RabbitMQ节点为原始状态。会从该节点所属的cluster中都删除，从管理数据库中删除所有数据，例如配置的用户和vhost，还会删除所有的持久消息。
# 要想reset和force_reset操作执行成功，RabbitMQ应用需要处于停止状态，即执行过 rabbitmqctl stop_app
rabbitmqctl reset 
```

```
# 表示强制性地设置RabbitMQ节点为原始状态。它和reset的区别在于，可以忽略目前管理数据库的状态和cluster的配置，无条件的reset。
# 虽然会忽略目前管理的mnesia数据库和cluster配置，但是会删除掉当前节点的mnesia数据库配置和cluster配置，集群中的其它节点需要使用rabbitmqctl forget_cluster_node rabbit@hostname
# 该方法的使用，应当用在当数据库或者cluster配置损坏的情况下作为最后的方法。
rabbitmqctl force_reset
```

- 9.查看节点的日志

```
##日志存放在/var/log/rabbitmq目录下
tail -f /var/log/rabbitmq/rabbit\@rabbit3.log
```

 - 10.环境变量和配置文件默认位置在

```
##配置文件
/etc/rabbitmq/rabbitmq.conf
##环境变量配置文件
/etc/rabbitmq/rabbitmq-env.conf
##cookie地址
/var/lib/rabbitmq/.erlang.cookie
##mnesia数据库地址
/var/lib/rabbitmq/mnesia/
# 插件地址
/usr/lib/rabbitmq/lib/rabbitmq_server-3.8.1/plugins
##日志位置
/var/log/rabbitmq/rabbit\@rabbit1.log
```

GitHub地址：[https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%9ACentOS7%E4%B8%AD%E5%AE%89%E8%A3%85RabbitMQ%E9%9B%86%E7%BE%A4.md](https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0%EF%BC%9ACentOS7%E4%B8%AD%E5%AE%89%E8%A3%85RabbitMQ%E9%9B%86%E7%BE%A4.md)

