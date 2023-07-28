#### 解锁新技能《logback标记日志过滤器MarkerFilter》

开源日志SDK(纯java版)

```xml
<!-- Java基于logback的日志组件SDK -->
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-logger</artifactId>
    <version>4.3.7</version>
</dependency>
<!--Java通用日志组件SDK-->
<dependency>
  <groupId>io.github.mingyang66</groupId>
  <artifactId>oceansky-logger</artifactId>
  <version>4.3.7</version>
</dependency>
```



> 在logback-classic中存在一个全局过滤器TurboFilter，TurboFilter是与LoggerContext绑定，会在会在其它过滤器之前执行；MarkerFilter是TurboFilter的一个子类，其作用是标记日志是否记录入文件之中，可以指定标记的日志记录到文件中；也可以指定标记的日志拒绝记录到文件中，其它未标记的记录入文件；

##### 一、定义MarkerFilter初始化实例对象

```java
    /**
     * 全局标记过滤器，接受指定标记的日志记录到文件中
     *
     * @param context 上下文
     * @param marker  marker标识
     * @return 标记过滤器，将会接受被标记的日志记录到文件中
     */
    public MarkerFilter getAcceptMarkerFilter(Context context, String marker) {
        MarkerFilter filter = new MarkerFilter();
        //过滤器名称
        filter.setName(StrUtils.join("AcceptMarkerFilter-", marker));
        //上下文
        filter.setContext(context);
        //日志过滤级别
        filter.setMarker(marker);
        //设置符合条件的日志接受
        filter.setOnMatch(FilterReply.ACCEPT.name());
        //不符合条件的日志拒绝
        filter.setOnMismatch(FilterReply.DENY.name());
        //添加内部状态信息
        filter.addError("Build AcceptMarkerFilter Success");
        //标记为启用状态
        filter.start();
        return filter;
    }

    /**
     * 全局标记过滤器，拒绝标记的日志记录到文件中
     *
     * @param context 上下文
     * @param marker  marker标识
     * @return 标记过滤器，将会拒绝被标记的日志记录到文件中
     */
    public MarkerFilter getDenyMarkerFilter(Context context, String marker) {
        MarkerFilter filter = new MarkerFilter();
        //过滤器名称
        filter.setName(StrUtils.join("DenyMarkerFilter-", marker));
        //上下文
        filter.setContext(context);
        //日志过滤级别
        filter.setMarker(marker);
        //设置符合条件的日志接受
        filter.setOnMatch(FilterReply.DENY.name());
        //不符合条件的日志拒绝
        filter.setOnMismatch(FilterReply.ACCEPT.name());
        //添加内部状态信息
        filter.addError("Build DenyMarkerFilter Success");
        //标记为启用状态
        filter.start();
        return filter;
    }
```

> 上述定义了一个指定标记的日志会被记录入文件的MarkerFilter过滤器，一个指定标记的日志不会被记录日志文件，其它日志文件会被记录入文件。

##### 二、将MarkerFilter添加到LoggerContext上下文

```java
private static final LoggerContext LOGGER_CONTEXT = (LoggerContext) LoggerFactory.getILoggerFactory();
 context.addTurboFilter(LogbackFilter.getSingleton().getAcceptMarkerFilter(context, marker));
 context.addTurboFilter(LogbackFilter.getSingleton().getDenyMarkerFilter(context, marker));
```

> 上述代码比较简单，详情可以参考源码；上述会将定义好的两个全局过滤器添加到LoggerContext中，接下来就可以根据需要标记日志，控制是否输出到日志文件。

##### 三、标记日志使用示例

```java
private static final Logger baseLogger = LoggerFactory.getLogger(LogbackController.class);
private static final Marker marker = MarkerFactory.getMarker("instance_marker");

baseLogger.error("--------error");
baseLogger.info("--------info");
baseLogger.debug("--------debug");
baseLogger.warn("--------warn");
baseLogger.trace("--------trace");

baseLogger.error(marker, "--------marker error");
baseLogger.info(marker, "--------marker info");
baseLogger.debug(marker, "--------marker debug");
baseLogger.warn(marker, "--------marker warn");
baseLogger.trace(marker, "--------marker trace");
```

> 上述示例如果指定的标记设置为接受打印到日志文件，则你会在日志文件中、控制台上看到带有marker的日志信息；如果标记的日志被设置为拒绝，则在日志文件、控制台上看到的就是不带marker的日志信息；



GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

