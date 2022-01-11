### Mac电脑安装protobuf

##### 1.protobuf下载地址

> https://github.com/protocolbuffers/protobuf/releases

##### 2.将下载包解压缩到指定目录

> /Users/yaomingyang/Documents/IDE/protobuf-3.19.0

##### 3.设置编译目录

```text
cd /Users/yaomingyang/Documents/IDE/protobuf-3.19.0
./configure --prefix=/usr/local/protobuf
```

##### 4.切换到root用户

```
sudo -i
```

##### 5.安装

```
cd /Users/yaomingyang/Documents/IDE/protobuf-3.19.0
make install
```

##### 6.添加环境变量

```
vi ~/.bash_profile
#添加如下配置
export PROTOBUF=/usr/local/protobuf 
export PATH=$PROTOBUF/bin:$PATH
#使修改立即生效
source ~/.bash_profile
```

##### 7.测试安装结果

```
protoc --version
```

##### 8.将proto文件生成java文件

```
protoc ./Msg.proto --java_out=./
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
