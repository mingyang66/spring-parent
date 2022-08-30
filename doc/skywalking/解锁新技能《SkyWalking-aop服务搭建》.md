### 解锁新技能《SkyWalking-aop服务搭建》

> 在本机测试，由于机器资源的原因，使用mysql作为存储

##### 一、docker搭建SkyWalking

mysql版本：

```sh
docker run \
--name oap -itd \
-e TZ=Asia/Shanghai \
-p 12800:12800 \
-p 11800:11800 \
-v /Users/xx/Documents/IDE/skwalking/skywalking/config:/skywalking/ext-config \
-v /Users/xx/Documents/IDE/skwalking/skywalking/libs/mysql-connector-java-8.0.28.jar:/skywalking/ext-libs/mysql-connector-java-8.0.28.jar \
-e SW_STORAGE=mysql \
apache/skywalking-oap-server:9.1.0
```

es版本：

```sh
docker run \
--name oap -itd \
-e TZ=Asia/Shanghai \
-p 12800:12800 \
-p 11800:11800 \
--link es7:es7 \
-v /Users/xx/Documents/IDE/skwalking/skywalking/config:/skywalking/ext-config \
-e SW_STORAGE=elasticsearch \
-e SW_STORAGE_ES_CLUSTER_NODES=es7:9200 \
apache/skywalking-oap-server:9.1.0
```

- -e TZ=Asia/Shanghai：指定时区。
- --link es7:es7：关联es7容器，通过容器名字来解决ip会发生变更的问题。
- -e SW_STORAGE=elasticsearch：设置环境变量，指定存储方式。
- -e SW_STORAGE_ES_CLUSTER_NODES=es7:9200：设置环境变量，指定ES的地址

参考地址：[https://hub.docker.com/r/apache/skywalking-oap-server](https://hub.docker.com/r/apache/skywalking-oap-server)

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)