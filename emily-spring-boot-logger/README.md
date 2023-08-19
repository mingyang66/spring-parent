#### 解锁新技能《基于logback的纯java版本SDK实现》

开源SDK：

```xml
<!--Java通用日志组件SDK-->
<dependency>
  <groupId>io.github.mingyang66</groupId>
  <artifactId>oceansky-logger</artifactId>
  <version>4.3.8</version>
</dependency>
<!-- Java基于logback的日志组件SDK -->
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-logger</artifactId>
    <version>4.3.5</version>
</dependency>
```

> 在项目开发过程中通常会使用logback作为日志记录的依赖工具，使用方式是引入logback相关jar包，然后配置logback.xml配置文件的方式来实现；xml的配置方案如果是一个两个项目还好，那如果是几十个项目呢？每个项目都要写一遍配置文件也是一键很繁琐的事情，而且配置文件还容易出错，那我们有没有办法将其改造成一个纯java版本的SDK组件呢？如果我们需要用的时候只需要将其依赖引入项目中开箱即用是不是很方便。

##### 一、开源SDK项目规划

- 新建oceansky-logger sdk，不依赖任何三方组件，不依赖任何web容器；
- 新建emily-spring-boot-logger sdk，为基于springboot的项目提供开箱即用的starter;

##### 二、oceansky-logger基础库SDK支持哪些功能

- 支持基础日志打印；

```java
private static final Logger baseLogger = LoggerFactory.getLogger(LogbackController.class);
baseLogger.error("--------error");
baseLogger.info("--------info");
baseLogger.debug("--------debug");
baseLogger.warn("--------warn");
baseLogger.trace("--------trace");
```

> 这些日志会打印到指定的日志文件夹，分别存储到error、warn、info、debug文件夹下；

- 支持分组日志打印；

```java
 private static final Logger logger = LoggerFactory.getGroupLogger(LogbackController.class, "group/test");
groupLogger.error("+++++++++++==ttttttttttttt");
groupLogger.debug("+++++++++++==ttttttttttttt");
groupLogger.info("+++++++++++==ttttttttttttt");
groupLogger.warn("+++++++++++==ttttttttttttt");
groupLogger.trace("+++++++++++==ttttttttttttt");

```

> 这些日志会分别打印到指定的分组文件夹group/test下面，分别存储到error、warn、info、debug文件夹下；

- 支持模块日志打印；

```java
 private static final Logger logger = LoggerFactory.getModuleLogger(LogbackController.class, "test1", "tt0");
logger..info("ni-----------------" + System.currentTimeMillis());
```

> 这些日志分别被记录到指定的文件夹及指定的文件名中，同一个项目中可以指定N多个这样的日志记录模块；

- 支持是否将上述三种日志展示到控制台上，控制统一通过LoggerContextInitializer.init方法实现；
- console控制台支持基于ANSI编码的颜色高亮展示；
- 异常堆栈允许展示每一行末尾追加对应所属的jar包，如下示例的中括号内：

```sh
2023-07-19 14:16:38.405 ERROR default --- [tp-nio-8080-exec-1] c.e.i.t.controller.LogbackController:35   : -----error test---- 
java.lang.NullPointerException: Cannot invoke "String.length()" because "s" is null
	at com.emily.infrastructure.test.controller.LogbackController.debug(LogbackController.java:33) ~[classes/:na]
	at com.emily.infrastructure.test.controller.LogbackController$$FastClassBySpringCGLIB$$2de19373.invoke(<generated>) ~[classes/:na]
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:218) ~[spring-core-5.3.28.jar:5.3.28]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:793) ~[spring-aop-5.3.28.jar:5.3.28]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:163) ~[spring-aop-5.3.28.jar:5.3.28]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:763) ~[spring-aop-5.3.28.jar:5.3.28]
	at com.alibaba.druid.support.spring.stat.DruidStatInterceptor.invoke(DruidStatInterceptor.java:70) ~[druid-1.2.18.jar:na]
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186) ~[spring-aop-5.3.28.jar:5.3.28]
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:763) ~[spring-aop-5.3.28.jar:5.3.28]
```

- 支持日志debug模式，展示内部状态信息及调试信息；

- 支持基于ZIP、GZ的归档日志压缩，压缩率在80%以上；

- 允许重复初始化logger，多次初始化以最后一次为准（每次初始化都会重置原来的初始化内容）

  ```java
  LoggerContextInitializer.init(new LoggerProperties())
  ```

##### 三、emis-spring-boot-logger基础库SDK支持哪些功能

- 完全基于oceansky-logger sdk组件为springboot提供的starter组件；
- 提供基于自动化配置的开箱即用的组件模式，只需在pom.xml引入starter既可以按照ocean-logger中指定的使用方案使用；
- 自动化配置属性控制root、group、module、appender、滚动策略等，具体配置如下：

```properties
#日志组件
#启动日志访问组件，默认：true
spring.emily.logger.enabled=true
#是否开启debug模式，默认：false
spring.emily.logger.debug=true
#发生异常打印异常堆栈时是否将包信息追加到每行末尾，默认：true
spring.emily.logger.packaging-data=true
#日志文件存放路径，默认是:./logs
spring.emily.logger.appender.path=./logs
#如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true
spring.emily.logger.appender.append=true
#如果是 true，日志会被安全的写入文件，即使其他的FileAppender也在向此文件做写入操作，效率低，默认是 false|Support multiple-JVM writing to the same log file
spring.emily.logger.appender.prudent=false
#设置是否将输出流刷新，确保日志信息不丢失，默认：true
spring.emily.logger.appender.immediate-flush=true
#是否开启基于文件大小和时间的SizeAndTimeBasedRollingPolicy归档策略
#归档策略（SIZE_AND_TIME_BASED、TIME_BASE），默认：TIME_BASE
spring.emily.logger.appender.rolling-policy.type=TIME_BASE
#设置要保留的最大存档文件数量，以异步方式删除旧文件,默认 7
spring.emily.logger.appender.rolling-policy.max-history=2
#最大日志文件大小 KB、MB、GB，默认:500MB
spring.emily.logger.appender.rolling-policy.max-file-size=10KB
#控制所有归档文件总大小 KB、MB、GB，默认:5GB
spring.emily.logger.appender.rolling-policy.total-size-cap=5GB
#设置重启服务后是否清除历史日志文件，默认：false
spring.emily.logger.appender.rolling-policy.clean-history-on-start=true
#压缩模式(NONE、GZ、ZIP)，默认：ZIP
spring.emily.logger.appender.rolling-policy.compression-mode=ZIP
#是否开启异步记录Appender，默认：false
spring.emily.logger.appender.async.enabled=false
#队列的最大容量，默认为 256
spring.emily.logger.appender.async.queue-size=256
#默认，当队列还剩余 20% 的容量时，会丢弃级别为 TRACE, DEBUG 与 INFO 的日志，仅仅只保留 WARN 与 ERROR 级别的日志。想要保留所有的事件，可以设置为 0
spring.emily.logger.appender.async.discarding-threshold=0
# 根据所引用 appender 队列的深度以及延迟， AsyncAppender 可能会耗费长时间去刷新队列。
# 当 LoggerContext 被停止时， AsyncAppender stop 方法会等待工作线程指定的时间来完成。
# 使用 maxFlushTime 来指定最大的刷新时间，单位为毫秒。在指定时间内没有被处理完的事件将会被丢弃。这个属性的值的含义与 Thread.join(long)) 相同
# 默认是 1000毫秒
spring.emily.logger.appender.async.max-flush-time=1000
# 在队列满的时候 appender 会阻塞而不是丢弃信息。设置为 true，appender 不会阻塞你的应用而会将消息丢弃，默认为 false
spring.emily.logger.appender.async.never-block=false
#是否将日志信息输出到控制台，默认：true
spring.emily.logger.root.console=false
#基础日志文件路径,默认：""
spring.emily.logger.root.file-path=base
#日志级别,即该等级之上才会输出，ERROR > WARN > INFO > DEBUG > TRACE >ALL, 默认：INFO
spring.emily.logger.root.level=info
#记入文件日志格式-不带颜色
spring.emily.logger.root.pattern=%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %cn --- [%18.18thread] %-36.36logger{36}:%-4.4line : %msg %n
#控制台输出格式-带颜色，默认：[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%thread] [%-5level] [%-36.36logger{36}:%-4.4line] : %msg%n
spring.emily.logger.root.console-pattern=%d{yyyy-MM-dd HH:mm:ss.SSS} %highlight(%-5level) %cn --- [%18.18thread] %cyan(%-36.36logger{36}:%-4.4line) : %msg %n
#是否将模块日志输出到控制台，默认：false
spring.emily.logger.group.console=true
#日志级别,即该等级之上才会输出，ERROR > WARN > INFO > DEBUG > TRACE >ALL, 默认：INFO
spring.emily.logger.group.level=info
#模块日志输出格式，默认：%msg%n
spring.emily.logger.group.pattern=%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %cn --- [%18.18thread] %-36.36logger{36}:%-4.4line : %msg %n
#是否将模块日志输出到控制台，默认：false
spring.emily.logger.module.console=true
#日志级别,即该等级之上才会输出，ERROR > WARN > INFO > DEBUG > TRACE >ALL, 默认：DEBUG
spring.emily.logger.module.level=info
#模块日志输出格式，默认：%msg%n
spring.emily.logger.module.pattern=%msg%n

```

##### 四、组件使用方法

- oceansky-logger组件SDK使用方法，调用初始化方法后即可开始日志记录的愉快旅程

```java
LoggerProperties properties = new LoggerProperties();
LoggerContextInitializer.init(properties);
```

- emily-spring-boot-logger是一个基于springboot自动化配置，开箱即用的组件，只需要引入组件既可以使用；



如果对源码感兴趣，可以查看GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)