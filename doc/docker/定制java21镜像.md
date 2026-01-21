[定制基于java21的maven镜像.md](..%2F..%2F..%2F..%2Fworkplace%2Fcom.eastmoney.emis.doc%2F%E4%B8%AD%E9%97%B4%E4%BB%B6%2FDocker%2F%E5%AE%9A%E5%88%B6%E5%9F%BA%E4%BA%8Ejava21%E7%9A%84maven%E9%95%9C%E5%83%8F.md)### 定制java21容器镜像

##### 1.获取java21镜像

```properties
#标准jdk
docker pull docker.m.daocloud.io/eclipse-temurin:21-jdk
```

```properties
#JRE运行时，仅包含运行换进，无编译工具
docker pull docker.m.daocloud.io/eclipse-temurin:21-jre-alpine
```

```properties
#经简单
docker pull docker.m.daocloud.io/eclipse-temurin:21-jdk-alpine
```

##### 2.创建容器

```properties
docker run -itd --name jre21 --entrypoint sh  docker.m.daocloud.io/eclipse-temurin:21-jre-alpine
```

##### 4.修改时区，解决差8个小时问题

```properties
docker cp /usr/share/zoneinfo/Asia/Shanghai <containerId>:/etc/localtime
```

```properties
docker cp /usr/share/zoneinfo/Asia/Shanghai <containerId>:/etc/timezone
```

##### 3.进入容器

```properties
docker exec -it <containerId> sh
```

##### 5.解决图片空指针，缺少字体库问题

```properties
apk add fontconfig
```

```properties
apk add --update ttf-dejavu
```

```properties
fc-cache --force
```



##### 6.打包镜像

```properties
docker commit -a='name' -m 'Java21运行环境' <containerId> 10.10.xx.xx:xx/library/java/jre:21-jre-alpine
```

OPTIONS说明：
-a :提交的镜像作者；
-c :使用Dockerfile指令来创建镜像；
-m :提交时的说明文字；
-p :在commit时，将容器暂停。

##### 7.将镜像倒出到本地

```properties
docker save -o java21.tar 10.10.xx.xx:xx/library/java/jre:21-jre-alpine
```

##### 8.服务器加载镜像到docker容器

```properties
docker load --input java21.tar
```

##### 9.将docker镜像推送到容器仓库

```properties
docker push 10.10.xx.xx:xx/library/java/jre:21-jre-alpine
```

