### 定制java11容器镜像

##### 1.获取java11镜像

```properties
docker pull adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.9_11
```

##### 2.创建容器

```properties
docker run -itd --name java11 -p 9000:9000 adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.9_11
```

##### 3.进入容器

```properties
docker exec -it <containerId> sh
```

##### 4.修改时区，解决差8个小时问题

```properties
docker cp /usr/share/zoneinfo/Asia/Shanghai <containerId>:/etc/localtime
```

```properties
docker cp /usr/share/zoneinfo/Asia/Shanghai <containerId>:/etc/timezone
```

##### 5.解决图片空指针，缺少字体库问题

```properties
apk add fontconfig
apk add --update ttf-dejavu
fc-cache --force
```

##### 6.打包镜像

```properties
docker commit -a='emily' -m 'desc' <containerId> x.x.x.x/emily/java/openjdk11:alpine
```

OPTIONS说明：
-a :提交的镜像作者；
-c :使用Dockerfile指令来创建镜像；
-m :提交时的说明文字；
-p :在commit时，将容器暂停。

##### 7.将镜像到出到本地

```properties
docker save -o java11.tar x.x.x.x/emily/java/openjdk11:alpine
```

##### 8.服务器加载镜像到docker容器

```properties
docker load --input java11.tar
```

##### 9.将docker镜像推送到容器仓库

```properties
docker push x.x.x.x/emily/java/openjdk11
```

