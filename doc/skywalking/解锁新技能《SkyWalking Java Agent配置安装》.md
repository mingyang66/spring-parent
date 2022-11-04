### 解锁新技能《SkyWalking Java Agent配置安装》

##### 一、下载java agent

https://archive.apache.org/dist/skywalking/java-agent

##### 二、解压缩下载的java-agent包，目录结构如下：

```sh
+-- agent
    +-- activations
         apm-toolkit-log4j-1.x-activation.jar
         apm-toolkit-log4j-2.x-activation.jar
         apm-toolkit-logback-1.x-activation.jar
         ...
    +-- config
         agent.config  
    +-- plugins
         apm-dubbo-plugin.jar
         apm-feign-default-http-9.x.jar
         apm-httpClient-4.x-plugin.jar
         .....
    +-- optional-plugins
         apm-gson-2.x-plugin.jar
         .....
    +-- bootstrap-plugins
         jdk-http-plugin.jar
         .....
    +-- logs
    skywalking-agent.jar
```

##### 三、启动应用服务并添加java agent探针

```sh
java -javaagent:/Users/XX/Documents/IDE/workplace-java/spring-parent/demo-emily-spring-boot/agent/skywalking-agent.jar -Dskywalking.agent.service_name=demoskywalking -Dskywalking.collector.backend_service=127.0.0.1:11800 -jar emilyframework.jar
```

- agent.service_name：指定服务名称，必须为字符串英文标识；
- collector.backend_service：指定OAP收集数据的地址；
- 添加-javaagent:/path/to/skywalking-package/agent/skywalking-agent.jar作为虚拟机参数，必须在-jar前面的参数；
- 其中agent/agent.config配置文件必须和skywalking-agent.jar在同一级目录；
- 默认日志输出陌路是agent/logs；

Java agent属性配置优先级顺序如下：

Agent Options > System.Properties(-D) > System environment variables > Config file

##### 四、docker搭建java agent



```sh
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
```

> 核心是将FROM openjdk:11更换为FROM apache/skywalking-java-agent:8.7.0-jdk11，官网地址：https://skywalking.apache.org/docs/skywalking-java/latest/en/setup/service-agent/java-agent/containerization/#docker只提供了jdk8版本的配置可以通过skywalking-docker的配置说明推测到java11版本，官网地址：https://github.com/apache/skywalking-docker

可以通过docker run传递参数更改指标收集地址值和服务名称：

```sh
  docker run \
  --privileged=true \
  --net=bridge \
  -itd --name emilyframework \
  -p ${httpPort}:9001 \
  -p ${httpsPort}:8080 \
  -p ${managementPort}:9443 \
  -v /Users/yaomingyang/Documents/IDE/workplace-java/logs/emilyframework:/app/logs \
  -e SW_AGENT_NAME=demo-emily-spring-boot \
  -e SW_AGENT_COLLECTOR_BACKEND_SERVICES=127.0.0.1:11800 \
  emilyframework:${VERSION}
```

- SW_AGENT_NAME：指定服务名称
- SW_AGENT_COLLECTOR_BACKEND_SERVICES：指标收集地址

参考地址：[Setup java agent](https://skywalking.apache.org/docs/skywalking-java/latest/en/setup/service-agent/java-agent/readme/)

GitHub地址:[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

