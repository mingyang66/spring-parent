#### Netty进阶《ChannelPoolMap源码分析》

> ChannelPoolMap是用来存储ChannelPool和指定key的一个集合Map，实际的应用场景就是服务器端是一个分布式集群服务，拥有多个配置地址，这样我们就可以配置多个服务地址，减轻单台服务器的压力；Netty框架提供了ChannelPoolMap接口和AbstractChannelPoolMap抽象方法。

##### 一、ChannelPoolMap接口源码分析

```java
package io.netty.channel.pool;

/**
 * Allows to map {@link ChannelPool} implementations to a specific key.
 *
 * @param <K> the type of the key
 * @param <P> the type of the {@link ChannelPool}
 */
public interface ChannelPoolMap<K, P extends ChannelPool> {
    /**
     * Return the {@link ChannelPool} for the {@code code}. This will never return {@code null},
     * but create a new {@link ChannelPool} if non exists for they requested {@code key}.
     *
     * Please note that {@code null} keys are not allowed.
     */
    P get(K key);

    /**
     * Returns {@code true} if a {@link ChannelPool} exists for the given {@code key}.
     *
     * Please note that {@code null} keys are not allowed.
     */
    boolean contains(K key);
}
```

> 接口提供了两个方法，get方法用于获取指定key对应的ChannelPool，contains方法用来判定Map集合中是否存在指定key对应的ChannelPool。

##### 二、AbstractChannelPoolMap抽象实现类

###### 1.get方法分析

```java
    private final ConcurrentMap<K, P> map = PlatformDependent.newConcurrentHashMap();

    @Override
    public final P get(K key) {
      //获取ChannelPool
        P pool = map.get(checkNotNull(key, "key"));
        if (pool == null) {
          //创建一个新的ChannelPool
            pool = newPool(key);
          //如果集合中存在ChannelPool,则返回老的ChannelPool对象
            P old = map.putIfAbsent(key, pool);
           //若果老的ChannelPool真实存在
            if (old != null) {
               //异步销毁新创建的ChannelPool
                // We need to destroy the newly created pool as we not use it.
                poolCloseAsyncIfSupported(pool);
                pool = old;
            }
        }
        return pool;
    }
```

- 定义了一个ConcurrentMap类型的map类变量，用来存放key及其对应的ChannelPool；
- 首先会从集合中获取ChannelPool，如果不存在则创建一个新的ChannelPool；

```java
    /**
     * Called once a new {@link ChannelPool} needs to be created as non exists yet for the {@code key}.
     */
    protected abstract P newPool(K key);
```

> newPool方法是一个抽象方法，需要用户自己实现ChannelPool的创建操作；

```java
    /**
     * If the pool implementation supports asynchronous close, then use it to avoid a blocking close call in case
     * the ChannelPoolMap operations are called from an EventLoop.
     *
     * @param pool the ChannelPool to be closed
     */
    private static Future<Void> poolCloseAsyncIfSupported(ChannelPool pool) {
        if (pool instanceof SimpleChannelPool) {
            return ((SimpleChannelPool) pool).closeAsync();
        } else {
            try {
                pool.close();
                return GlobalEventExecutor.INSTANCE.newSucceededFuture(null);
            } catch (Exception e) {
                return GlobalEventExecutor.INSTANCE.newFailedFuture(e);
            }
        }
    }
```

> 异步关闭ChannelPool，如果是SimpleChannelPool的实现，则调用异步方法closeAsync；如果是其它实现，则调用close方法。

###### 2.remove方法分析

```java
    public final boolean remove(K key) {
      //移除指定key的ChannelPool
        P pool =  map.remove(checkNotNull(key, "key"));
        if (pool != null) {
          //如果移除成功，则异步的关闭ChannelPool，避免阻塞方法
            poolCloseAsyncIfSupported(pool);
            return true;
        }
        return false;
    }
```



源码参考：[https://github.com/mingyang66/SkyDb](https://github.com/mingyang66/SkyDb)

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)