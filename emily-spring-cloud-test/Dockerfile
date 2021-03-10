#定制镜像所需的基础镜像
FROM adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.9_11
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