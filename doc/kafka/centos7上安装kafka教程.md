### centos7上安装kafka教程

##### 一、centos7安装java8

去oracle官网下载：https://www.oracle.com/cn/java/technologies/javase/javase-jdk8-downloads.html

```properties
tar -zxvf jdk-8u281-linux-x64.tar.gz
mkdir /usr/java
mv jdk1.8.0_281 /usr/java
```

环境变量配置：

打开/etc/profile配置文件

```properties
vi /etc/profile
```

在配置文件末尾添加如下配置：

```properties
export JAVA_HOME=/usr/java/jdk1.8.0_281
 
export CLASSPATH=.:$JAVA_HOME/jre/lib/rt.jar:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
 
export PATH=$PATH:$JAVA_HOME/bin
```

使用source命令使环境变量修改立即生效：

```properties
source /etc/profile
```



##### 二、Zookeeper安装

去官网下载最新版zookeeper服务：https://zookeeper.apache.org/releases.html

```properties
tar zxvf apache-zookeeper-3.7.0-bin.tar.gz 
```

将conf目录下zoo_sample.cfg配置文件更改为zoo.cfg配置，然后就可以启动zookeeper服务器了：

在apache-zookeeper-3.7.0-bin统计目录创建zookeeper/data目录存放数据，修改zoo.cfg配置为：

```properties
#zookeeper数据存放目录修改
dataDir=/emis/kafka/zookeeper/data
#集群配置 server.A=B:C:D 
# A是一个数字，代表服务器的编号，就是data目录下myid里面的数字
# B是服务器IP地址
# C是服务器与集群中leader服务器交换信息的端口
# D选举时服务器相互通信的端口
server.1=xx.xx.xx.xx:2888:3888
server.2=xx.xx.xx.xx:2888:3888
server.3=xx.xx.xx.xx:2888:3888
```

在data文件夹下创建myid文件内部输入服务器标识:

```shell
# 在/emis/kafka/zookeeper/data下新建myid文件，并添加服务器标识，是上述A代表的数字
vi myid
```



```properties
# 启动服务器
bin/zkServer.sh start
# 停止服务器
bin/zkServer.sh stop
# 进入CLI操作命令
bin/zkCli.sh
# 查看集群状态
bin/zkServer.sh status
```

环境配置：

```properties
vi /etc/profile
# 添加如下配置
export ZOOKEEPER_HOME=/xxx/kafka/apache-zookeeper-3.7.0-bin
export PATH=$PATH:$ZOOKEEPER_HOME/bin
# 使环境配置立马生效
source /etc/profile
```



##### 三、kafka下载安装

去官网下载最新版本：http://kafka.apache.org/downloads

解压缩kafka

```properties
tar zxvf kafka_2.12-2.7.0.tgz
```

修改配置：

```shell
# 创建目录
/opt/kafka/logs
# 修改配置
vi config/server.properties
# broker的编号，如果集群中有多个broker,则每个broker的编号需要设置的不同，同zookeeper一致就可以
broker.id=0
# 存放消息日志文件地址
log.dirs=/opt/kafka/logs
# broker对外提供服务的入口地址
advertised.listeners=PLAINTEXT://10.10.XX.xx:9092
# Kafka所需要的Zookeeper集群地址
zookeeper.connect=xx.xx.XX.xx:2181,xx.xx.XX.xx:2181,xx.xx.XX.xx:2181
```

Kafka常用命令：

```shell
# 启动Kafka
bin/kafka-server-start.sh -daemon config/server.properties
# 停止kafka 
bin/kafka-server-stop.sh
# 查看kafka进程
jps
```

验证Kafka集群是否搭建成功：

```shell
# 生产者
bin/kafka-console-producer.sh --broker-list xx.xx.xx.xx:9092 --topic test
# 消费者
bin/kafka-console-consumer.sh --bootstrap-server xx.xx.xx.xx:9092 --topic test --from-beginning
```



GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)