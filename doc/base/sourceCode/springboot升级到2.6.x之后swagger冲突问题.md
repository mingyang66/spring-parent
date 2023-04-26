##### springboot升级到2.6.x之后swagger冲突问题

##### 一、将springboot升级到2.6.x之后启动报如下错误

```java
org.springframework.context.ApplicationContextException: Failed to start bean 'documentationPluginsBootstrapper'; nested exception is java.lang.NullPointerException
	at org.springframework.context.support.DefaultLifecycleProcessor.doStart(DefaultLifecycleProcessor.java:181) ~[spring-context-5.3.14.jar:5.3.14]
	at org.springframework.context.support.DefaultLifecycleProcessor.access$200(DefaultLifecycleProcessor.java:54) ~[spring-context-5.3.14.jar:5.3.14]
	at org.springframework.context.support.DefaultLifecycleProcessor$LifecycleGroup.start(DefaultLifecycleProcessor.java:356) ~[spring-context-5.3.14.jar:5.3.14]
	at java.lang.Iterable.forEach(Iterable.java:75) ~[na:1.8.0_181]
```

二、解决方案是：springboot2.6.x之后将spring
MVC默认路径匹配策略从ANT_PATH_MATCHER模式改为PATH_PATTERN_PARSER模式导致出错，解决方法是切换会原先的ANT_PATH_MATCHER模式

```properties
spring.mvc.pathmatch.matching-strategy=ant_path_matcher
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

