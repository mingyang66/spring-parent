# 清空并打包
mvn clean package
# docker build命令用于使用Dockerfile创建镜像
# 语法：docker build [OPTIONS] PATH | URL | -
# -f:指定要使用的Dockerfile路径
# -t:镜像的名字及标签，通常是name:tag或name格式
# .代表本次执行的上下文路径
docker build -f ./Dockerfile . -t emilyframework:1.0.1
# 运行构建的镜像
docker run \
-e JAVA_ACL_TOKEN=03259e78-848c-3ea8-c0f6-524279d52929 \
-e JAVA_LOCAL_IP=172.30.67.122 \
-e JAVA_LOCAL_PORT=802 \
--restart=always \
--privileged=true \
-itd --name emilyframework \
-p 80:802 \
-p 443:4432 \
-p 7743:7742 \
-v /Users/yaomingyang/Documents/logs:/app/logs \
emilyframework:1.0.1