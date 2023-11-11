#### Redis学习笔记7：基于springboot的Lettuce redis客户端keepAlive保活机制

> Lettuce是基于netty来实现的，Netty支持通过设置ChannelOption.SO_KEEPALIVE属性来控制保活机制，底层实现是基于操作系统，操作系统的保活机制一般要等待7200秒，如centos的net.ipv4.tcp_keepalive_time设置；lettuce客户端另外提供了扩展保活机制，方便客户端灵活的控制保活机制的空闲时间、次数、间隔。

一个对springboot redis框架进行重写，支持lettuce、jedis、连接池、同时连接多个集群、多个redis数据库、开发自定义属性配置的开源SDK

```xml
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-redis</artifactId>
    <version>4.4.0</version>
</dependency>
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

##### 一、Lettuce提供基于操作系统的保活机制

- 通过SocketOptions属性设置keepAlive，默认：false

```java
 ClientOptions.Builder builder = this.initializeClientOptionsBuilder(properties);
        Duration connectTimeout = properties.getConnectTimeout();
        if (connectTimeout != null) {
            builder.socketOptions(SocketOptions.builder()
                    .keepAlive(true)
                    .build());
        }
```

- io.lettuce.core.ConnectionBuilder#configureBootstrap设置保活参数

```java
 bootstrap.option(ChannelOption.SO_KEEPALIVE, options.isKeepAlive());
```

##### 二、Lettuce提供扩展keepAlive保活机制

- 通过SocketOptions属性设置扩展保活机制参数

```java
        ClientOptions.Builder builder = this.initializeClientOptionsBuilder(properties);
        Duration connectTimeout = properties.getConnectTimeout();
        if (connectTimeout != null) {
            builder.socketOptions(SocketOptions.builder()
                    .connectTimeout(connectTimeout)
                    .keepAlive(SocketOptions.KeepAliveOptions.builder()
                            //两次keep-alive之间的间隔
                            .interval(Duration.ofSeconds(5))
                            //连接空闲多久开始keep-alive
                            .idle(Duration.ofSeconds(5))
                            //keep-alive多少次之后断开连接
                            .count(3)
                            //是否开启保活连接
                            .enable()
                            .build())
                    .build());
        }
```

- io.lettuce.core.ConnectionBuilder#configureBootstrap保活连接配置

```java
 public void configureBootstrap(boolean domainSocket,
            Function<Class<? extends EventLoopGroup>, EventLoopGroup> eventLoopGroupProvider) {
        ...
        SocketOptions options = clientOptions.getSocketOptions();
        EventLoopGroup eventLoopGroup = eventLoopGroupProvider.apply(eventLoopGroupClass);

        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Math.toIntExact(options.getConnectTimeout().toMillis()));

        if (!domainSocket) {
            //基于操作系统的keepAlive机制
            bootstrap.option(ChannelOption.SO_KEEPALIVE, options.isKeepAlive());
            bootstrap.option(ChannelOption.TCP_NODELAY, options.isTcpNoDelay());
        }

        bootstrap.channel(channelClass).group(eventLoopGroup);
				//开启扩展保活机制的前提是必须开启基于操作系统的保活机制
        if (options.isKeepAlive() && options.isExtendedKeepAlive()) {

            SocketOptions.KeepAliveOptions keepAlive = options.getKeepAlive();
						//判断是否可以使用IOUringProvider，即是否可用IO uring库。如果可用，调用IOUringProvider提供的applyKeepAlive方法，传入bootstrap（Netty的引导类）和keepAlive的参数（count、idle、interval），应用IOUringProvider提供的keepalive参数配置
            if (IOUringProvider.isAvailable()) {
                IOUringProvider.applyKeepAlive(bootstrap, keepAlive.getCount(), keepAlive.getIdle(), keepAlive.getInterval());
            } else if (EpollProvider.isAvailable()) {
              //判断是否可以使用EpollProvider，即是否可用Epoll网络库。如果可用，调用EpollProvider提供的applyKeepAlive方法，传入bootstrap和keepAlive的参数，应用EpollProvider提供的keepalive参数配置。
                EpollProvider.applyKeepAlive(bootstrap, keepAlive.getCount(), keepAlive.getIdle(), keepAlive.getInterval());
            } else if (ExtendedNioSocketOptions.isAvailable() && !KqueueProvider.isAvailable()) {
              //判断是否可以使用ExtendedNioSocketOptions，并且不可使用KqueueProvider。如果可用，调用ExtendedNioSocketOptions提供的applyKeepAlive方法，传入bootstrap和keepAlive的参数，应用ExtendedNioSocketOptions提供的keepalive参数配置
              ExtendedNioSocketOptions.applyKeepAlive(bootstrap, keepAlive.getCount(), keepAlive.getIdle(),
                        keepAlive.getInterval());
            } else {
                logger.warn("Cannot apply extended TCP keepalive options to channel type " + channelClass.getName());
            }
        }
    }
```

- IOUring是Netty中的一个实验性模块，用于支持Linux上的IO uring库。IOUring是Netty中的一个实验性模块，用于支持Linux上的IO uring库
- EpollProvider是Netty中的一个类，用于在Linux上提供基于Epoll的网络通信支持。Epoll是Linux内核提供的一种事件通知机制，用于处理大量并发连接的高性能I/O操作。它通过将文件描述符（包括套接字）注册到一个事件集合中，并且可以监听多个事件类型（如可读、可写、错误等），从而实现了高效的事件驱动模型。

##### 三、SocketOptions.KeepAliveOptions保活机制参数配置类

```java
    public static class KeepAliveOptions {
       //默认保活机制检查次数上限
        public static final int DEFAULT_COUNT = 9;
			 //默认信道空闲2小时开始进行keep-alive
        public static final Duration DEFAULT_IDLE = Duration.ofHours(2);
			 //每次keep-alive的时间间隔，默认：75秒
        public static final Duration DEFAULT_INTERVAL = Duration.ofSeconds(75);

        private final int count;

        private final boolean enabled;

        private final Duration idle;

        private final Duration interval;

        private KeepAliveOptions(KeepAliveOptions.Builder builder) {

            this.count = builder.count;
            this.enabled = builder.enabled;
            this.idle = builder.idle;
            this.interval = builder.interval;
        }
}
```

