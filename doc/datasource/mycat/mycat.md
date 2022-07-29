mycat 

```
docker run --net=host --restart=always -itd --name mycat centos:7
```

安装unzip包：

```
yum install -y unzip zip --nogpgcheck
```

添加ll命令：

切换到/root/.bashrc在文件中添加如下一行命令：

```
alias ll='ls $LS_OPTIONS -l'
source /root/.bashrc
```

mycat2相关命令：

```sh
# mycat启动停止
mycat restart|start|stop
#mycat状态
mycat status
#mycat登录
mysql -uroot -psmallgrain -P8066 -hlocalhost
```

mycat2 UI管理页面下载地址：http://dl.mycat.org.cn/2.0/ui/

```
java -jar xxx.jar
```

