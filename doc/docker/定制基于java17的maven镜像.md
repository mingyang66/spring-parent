### 定制基于java17的maven容器镜像

##### 1.获取maven镜像

```properties
docker pull maven:3.9.9-eclipse-temurin-17-focal
```

##### 2.创建容器

```properties
docker run -itd --name maven17 --entrypoint sh   maven:3.9.9-eclipse-temurin-17-focal
```

##### 3.进入容器

```properties
# 进入容器内部
docker exec -it maven17 bash
# 找到/usr/share/maven/conf目录下setting.xml文件，修改仓库地址
```

##### 6.打包镜像

```properties
docker commit -a='mingyang' -m 'make maven17' 9780a1369e15 xx.xx.xx.xx:xx/Emily/java/jdk:17_maven-3.9.9
```

OPTIONS说明：
-a :提交的镜像作者；
-c :使用Dockerfile指令来创建镜像；
-m :提交时的说明文字；
-p :在commit时，将容器暂停。

##### 7.将镜像倒出到本地

```properties
docker save -o maven17.tar xx.xx.xx.xx:xx/Emily/java/jdk:17_maven-3.9.9
```

##### 8.服务器加载镜像到docker容器

```properties
docker load --input maven17.tar
```

##### 9.将docker镜像推送到容器仓库

```properties
docker push xx.xx.xx.xx:xx/Emily/java/jdk:17_maven-3.9.9
```

