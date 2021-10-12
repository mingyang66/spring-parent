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
-e JAVA_ACL_TOKEN=7e9b1b50-c5b8-d786-c4f2-42c0155a7e1e \
-e JAVA_LOCAL_IP=172.30.67.122 \
-e JAVA_LOCAL_PORT=9000 \
--restart=always \
--privileged=true \
-itd --name emilyframework \
-p 9000:9000 \
-p 9088:9088 \
-p 9443:9443 \
-v /Users/yaomingyang/Documents/logs:/app/logs \
emilyframework:1.0.1