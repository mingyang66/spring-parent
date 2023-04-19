#### Netty进阶《ChannelPoolHandler调用处理程序》

> ChannelPoolHandler是为ChannelPool执行的各种操作调用的处理程序

##### 一、ChannelPoolHandler源码解析

```java
public interface ChannelPoolHandler {
    /**
     * Channel信道被ChannelPool#release(Channel)或ChannelPool#release(Channel, Promise)方法
     * 调用，并释放会ChannelPool连接池，
     */
    void channelReleased(Channel ch) throws Exception;

    /**
     * Channel信道通过调用ChannelPool#acquire()或ChannelPool#acquire(Promise)方法获取
     */
    void channelAcquired(Channel ch) throws Exception;

    /**
     * 在ChannelPool中创建Channel时将会被调用一次
     */
    void channelCreated(Channel ch) throws Exception;
}
```

##### 二、AbstractChannelPoolHandler源码解析

```java
public abstract class AbstractChannelPoolHandler implements ChannelPoolHandler {

    /**
     * 无操作实现方法，可以被子类覆盖
     *
     */
    @Override
    public void channelAcquired(@SuppressWarnings("unused") Channel ch) throws Exception {
        // NOOP
    }

    /**
     * 无操作实现方法，可以被子类覆盖
     */
    @Override
    public void channelReleased(@SuppressWarnings("unused") Channel ch) throws Exception {
        // NOOP
    }
}
```

> AbstractChannelPoolHandler抽象类是ChannelPoolHandler的框架实现类，其实现了两个无任何操作的方法。

##### 三、SimpleChannelPool#SimpleChannelPool构造函数中调用channelCreated方法

```java
 public SimpleChannelPool(Bootstrap bootstrap, final ChannelPoolHandler handler, ChannelHealthChecker healthCheck,
                             boolean releaseHealthCheck, boolean lastRecentUsed) {
        this.handler = checkNotNull(handler, "handler");
        this.healthCheck = checkNotNull(healthCheck, "healthCheck");
        this.releaseHealthCheck = releaseHealthCheck;
        // Clone the original Bootstrap as we want to set our own handler
        this.bootstrap = checkNotNull(bootstrap, "bootstrap").clone();
        this.bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                assert ch.eventLoop().inEventLoop();
               //此处调用ChannelPoolHandler处理程序的创建Channel信道方法
                handler.channelCreated(ch);
            }
        });
        this.lastRecentUsed = lastRecentUsed;
    }
```

##### 四、SimpleChannelPool#notifyConnect方法中调用channelAcquired获取Channel信道方法

```java
    private void notifyConnect(ChannelFuture future, Promise<Channel> promise) {
        Channel channel = null;
        try {
            if (future.isSuccess()) {
                channel = future.channel();
              //调用获取Channel信道方法
                handler.channelAcquired(channel);
                if (!promise.trySuccess(channel)) {
                    // Promise was completed in the meantime (like cancelled), just release the channel again
                    release(channel);
                }
            } else {
                promise.tryFailure(future.cause());
            }
        } catch (Throwable cause) {
            closeAndFail(channel, cause, promise);
        }
    }
```

##### 五、SimpleChannelPool#releaseAndOffer和SimpleChannelPool#releaseAndOffer调用channelReleased释放Channel信道方法

```java
    private void releaseAndOfferIfHealthy(Channel channel, Promise<Void> promise, Future<Boolean> future) {
        try {
            if (future.getNow()) { //channel turns out to be healthy, offering and releasing it.
                releaseAndOffer(channel, promise);
            } else { //channel not healthy, just releasing it.
                handler.channelReleased(channel);
                promise.setSuccess(null);
            }
        } catch (Throwable cause) {
            closeAndFail(channel, cause, promise);
        }
    }

    private void releaseAndOffer(Channel channel, Promise<Void> promise) throws Exception {
        if (offerChannel(channel)) {
            handler.channelReleased(channel);
            promise.setSuccess(null);
        } else {
            closeAndFail(channel, new ChannelPoolFullException(), promise);
        }
    }
```



GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)