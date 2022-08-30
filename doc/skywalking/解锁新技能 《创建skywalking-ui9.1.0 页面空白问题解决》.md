### 解锁新技能 《创建skywalking-ui9.1.0 页面空白问题解决》

创建skywalking-ui docker命令：

```
docker run -d --name skywalking-ui \
--restart=always \
-e TZ=Asia/Shanghai \
-p 8088:8080 \
--link oap:oap \
-e SW_OAP_ADDRESS=oap:12800 \
apache/skywalking-ui:9.1.0
```

> 这时候通过浏览器访问页面空白，通过docker容器查看日志服务正常启动，浏览器F12看到响应数据是We're sorry but Apache SkyWalking doesn't work properly without JavaScript enabled. Please enable it to continue.

解决方案（中间的解决过程不在复述）：

将SW_OAP_ADDRESS=oap:12800修改为SW_OAP_ADDRESS=http://oap:12800，如下：

```
docker run -d --name skywalking-ui \
--restart=always \
-e TZ=Asia/Shanghai \
-p 8088:8080 \
--link oap:oap \
-e SW_OAP_ADDRESS=http://oap:12800 \
apache/skywalking-ui:9.1.0
```



GitHub地址:[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
