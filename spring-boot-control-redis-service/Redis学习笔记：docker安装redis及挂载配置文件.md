### Redis学习笔记：docker安装redis及挂载配置文件

##### 1.下载redis 5.0版本的镜像

```java
git pull redis:5.0
```

##### 2.下载redis.conf配置文件，下载地址（选择需要的版本配置文件）：

```java
https://redis.io/topics/config
```

##### 3.创建redis容器对象，并且使用指定的配置文件启动，将配置文件挂载到本地方便修改

```
docker run -v D:/work/redis/conf/redis.conf:/usr/local/etc/redis/redis.conf --name redis-server -p 6379:6379 --privileged=true -d redis:5.0 redis-server /usr/local/etc/redis/redis.conf
```

##### 4.根据以上步骤创建成功之后客户端连接会报Reconnected to 127.0.0.1:6379异常

解决方法是将redis.conf配置文件中的127.0.0.0更改为0.0.0.0

##### 5.127.0.0.1、0.0.0.0和localhost的区别

- 在服务器中，0.0.0.0指的是本机上的所有IPV4地址，是真正表示“本网络中的本机”。一般我们在服务端绑定端口的时候可以选择绑定到0.0.0.0，这样服务访问方就可以通过我的多个IP地址访问我的服务。
- 在路由中，0.0.0.0表示的是默认路由，即当路由表中没有找到完全匹配的路由的时候所对应的的路由。
- 而127.0.0.1是本地回环地址中的一个，大多数windows和linux电脑上都将localhost指向了127.0.0.1这个地址，相当于本机地址。
- localhost是一个域名，可以用它来获取运行在本机上的网络服务。在大多数系统中，localhost被指向了IPV4的127.0.0.1和IPV6的::1

##### 6.客户端工具

- RedisClient: https://github.com/caoxinyu/RedisClient 
- RedisDesktopManager: https://github.com/uglide/RedisDesktopManager 
- RedisStudio: https://github.com/cinience/RedisStudio 