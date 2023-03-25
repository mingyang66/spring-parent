#### Netty进阶《ChannelPoolMap存储ChannelPool池如何实现公平访问》

> ChannelPoolMap是用来存服务器地址与ChannelPool的映射关系的，主要应用在分布式多服务器场景，如何实现公平的获取服务器对应的ChannelPool对象呢？看如下实现方案：

首先定义一个计数器，用来记录访问的次数：

```java
    private AtomicInteger counter = new AtomicInteger();
```

采用求余的方式公平的获取ChannelPoolMap中的ChannelPool对象，公平的分散请求压力：

```java
    public ChannelPool choose() {
        AbstractChannelPoolMap temp = ((AbstractChannelPoolMap) poolMap);
        List<Map.Entry<InetSocketAddress, FixedChannelPool>> list = Lists.newArrayList(temp.iterator());
        //数据增加到最大Integer.MAX_VALUE后绝对值开始减小
        int pos = Math.abs(counter.getAndIncrement());
        return poolMap.get(list.get(pos % list.size()).getKey());
    }
```



源码参考：[https://github.com/mingyang66/SkyDb](https://github.com/mingyang66/SkyDb)

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)