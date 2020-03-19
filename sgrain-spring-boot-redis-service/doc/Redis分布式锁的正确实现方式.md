### Redis分布式锁的正确实现方式

##### 1.前言

分布式锁一般有三种实现方式：

1. 数据库乐观锁
2. 基于Redis的分布式锁
3. 基于Zookeeper的分布式锁

本文介绍的是基于Redis的分布式锁；

##### 2.可靠性

首先，为了确保分布式锁可用，需要锁至少满足以下四个条件：

1. 互斥性，在任意时刻，只有一个客户端能持有锁。
2. 不会发生死锁，即使有一个客户端在持有锁的期间崩溃而没有主动释放锁，也能保证后续其它客户端能加锁。
3. 具有容错性。只要大部分Redis节点正常运行，客户端就可以加锁和解锁。
4. 解铃还需系铃人，加锁和解锁必须是同一个客户端（同一个线程），客户端自己不能把别人加的锁给解了。

##### 3.代码实现

首先我们通过Maven引入redis starter,在pom.xml文件中加入下面的代码：

```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
```

加锁代码：

```java
   /**
     * 释放锁成功返回值
     */
    private static final Long RELEASE_LOCK_SUCCESS = 1L;
    /**
     * 自动过期释放锁成功返回值
     */
    private static final Long RELEASE_LOCK_AUTO_SUCCESS = 0L;
    /**
     * @Description 尝试获取分布式锁
     * @param redisTemplate Redis客户端对象
     * @param lockKey 锁
     * @param value 唯一标识
     * @param expireTime 过期时间
     * @param util 单位
     * @return 是否获取成功
     */
    public static Boolean tryLock(RedisTemplate redisTemplate, String lockKey, String value, long expireTime, TimeUnit util){
        long currentTime = System.currentTimeMillis();
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, value, expireTime, util);
        if(System.currentTimeMillis() - currentTime >= expireTime){
            return Boolean.FALSE;
        }
        if(Boolean.TRUE.equals(result)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
```

我们可以看到加锁也就一行是有效代码，使用了setIfAbsent方法，也就是只有在lockKey不存在时才加锁，第二个为value,这个也是很有用的，解铃还须系铃人就是通过这个值来区分，客户端不可以解锁其它人的锁；第三个参数是过期时间；第四个参数是过期时间单位；

其实setIfAbsent底层实现方法是对Jedis的如下包装，具体参数的详解注解上有：

```
  /**
   * Set the string value as value of the key. The string can't be longer than 1073741824 bytes (1
   * GB).
   * @param key
   * @param value
   * @param nxxx NX|XX, NX -- Only set the key if it does not already exist. XX -- Only set the key
   *          if it already exist.
   * @param expx EX|PX, expire time units: EX = seconds; PX = milliseconds
   * @param time expire time in the units of <code>expx</code>
   * @return Status code reply
   */
  public String set(final String key, final String value, final String nxxx, final String expx,
      final long time) {
    checkIsInMultiOrPipeline();
    client.set(key, value, nxxx, expx, time);
    return client.getStatusCodeReply();
  }
```

解锁代码：

lua脚本：

```
if redis.call("get",KEYS[1]) == ARGV[1] then
    return redis.call("del",KEYS[1])
else
    return 0
end
```

java代码：

```
    /**
     * 释放锁lua脚本
     */
    private static final String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        /**
     * @Description 释放锁
     * @param redisTemplate Redis客户端对象
     * @param lockKey 锁
     * @param value 唯一标识
     * @return
     */
    public static Boolean releaseLock(RedisTemplate redisTemplate, String lockKey, String value){
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Object result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), value);
        //释放锁成功，或锁自动过期
        if(RELEASE_LOCK_SUCCESS.equals(result) || RELEASE_LOCK_AUTO_SUCCESS.equals(result)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
```

可以看到解锁也很简单，一条lua脚本加上execute方法就完成了锁删除；那么为什么要使用lua脚本？因为要确保上述操作的原子性；lua代码被当成一个命令区执行，并且直到eval命令执行完成，Redis才会执行其它命令。

当然上面的实现方式也不是很严谨，如加锁后业务逻辑还未执行完成锁已经过期，这会导致其它的客户端拿到锁；如果是单节点这样做问题不大，但是如果是个集群，加锁首先会落盘到master节点，然后再复制到salve节点，如果在未复制之前master节点挂掉，那么就会导致锁丢失的问题。为了解决上面的这些问题Redis官方推荐使用Redisson分布式锁，这是官方推荐的组件。



参考：https://redis.io/topics/distlock

Redisson:[https://github.com/redisson/redisson/wiki/8.-%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81%E5%92%8C%E5%90%8C%E6%AD%A5%E5%99%A8](https://github.com/redisson/redisson/wiki/8.-分布式锁和同步器)


GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/sgrain-spring-boot-redis-service/doc](https://github.com/mingyang66/spring-parent/tree/master/sgrain-spring-boot-redis-service/doc)
