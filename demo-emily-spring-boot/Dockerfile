#定制镜像所需的基础镜像
#FROM openjdk:11
#https://github.com/apache/skywalking-docker
FROM apache/skywalking-java-agent:8.7.0-jdk11
# 用于执行后面跟着的命令行命令
RUN echo 'JDK11 Images Download Success'
#作者
MAINTAINER Emily
#工作目录路径
WORKDIR /app
#构建参数
ARG JAR_FILE=target/emilyframework.jar
#复制指令，从上下文目录中复制文件或目录到容器里指定路径
COPY ${JAR_FILE} emilyframework.jar
#运行程序指令
ENTRYPOINT ["java","-jar","emilyframework.jar"]