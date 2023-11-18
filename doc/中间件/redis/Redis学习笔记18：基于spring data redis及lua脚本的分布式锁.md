#### Redis学习笔记18：基于spring data redis及lua脚本的分布式锁

> Redis分布式锁是一种在分布式系统中使用Redis实现的互斥锁。它可以确保在多个客户端同时访问共享资源时，只有一个客户端能够获取到锁，其它客户端需要等待或执行相应的逻辑。

一个对springboot redis框架进行重写，支持lettuce、jedis、连接池、同时连接多个集群、多个redis数据库、开发自定义属性配置的开源SDK

```xml
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-redis</artifactId>
    <version>4.4.0</version>
</dependency>
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

##### 一、lua脚本SET指令实现的锁逻辑

```lua
-- 键值
local key = KEYS[1]
-- 值
local value = ARGV[1]
-- 过期时间
local expire = ARGV[2]
-- SET key value [NX | XX] [GET] [EX seconds | PX milliseconds | EXAT unix-time-seconds | PXAT unix-time-milliseconds | KEEPTTL]
-- NX 仅当key不存在时才设置key
-- XX 仅当key已经存在时才设置key
local success = redis.call('SET', key, value, 'NX', 'EX', expire)
if success then
    return true
else
    return false
end
```

> 上述脚本通过redis的SET指令及参数NX控制仅当key不存在时才设置key，通过参数EX控制过期时间单位是秒。

##### 二、lua脚本实现解锁的逻辑

```lua
-- 键
local key = KEYS[1]
-- 删除已存在的键,不存在的 key 会被忽略
local success = redis.call('DEL', key)
if success then
    return true
else
    return false
end
```

##### 三、spring data redis代码实现调用执行加锁lua脚本

```java
    /**
     * 尝试获取锁
     * 只有在key不存在的时候才可以加锁成功
     *
     * @param redisTemplate redis 模板工具类
     * @param key           键名
     * @param expire        过期时间
     * @return true-加锁成功 false-加锁失败
     */
    public static Boolean tryGetLock(RedisTemplate redisTemplate, String key, Duration expire) {
        try {
            if (StringUtils.isEmpty(LUA_SCRIPT_LOCK_GET)) {
                LUA_SCRIPT_LOCK_GET = getLuaScript("META-INF/scripts/lock_get.lua");
            }
            RedisScript<Boolean> script = RedisScript.of(LUA_SCRIPT_LOCK_GET, Boolean.class);
            return (Boolean) redisTemplate.execute(script, singletonList(key), "1", expire.getSeconds());
        } catch (Exception ex) {
            BaseLogger baseLogger = BaseLoggerBuilder.create()
                    .withSystemNumber(SystemNumberHelper.getSystemNumber())
                    .withTraceId(UUIDUtils.randomSimpleUUID())
                    .withClientIp(RequestUtils.getClientIp())
                    .withServerIp(RequestUtils.getServerIp())
                    .withTriggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                    .withUrl("Redis")
                    .withRequestParams("key", key)
                    .withRequestParams("expire", expire.getSeconds())
                    .withBody(PrintExceptionInfo.printErrorInfo(ex.getCause()))
                    .build();
            logger.info(JsonUtils.toJSONString(baseLogger));
            return false;
        }
    }
```

##### 四、spring data redis代码实现调用解锁lua脚本

```java
    /**
     * 释放指定的锁，如果锁不存在，则忽略
     *
     * @param redisTemplate redis 模板工具类
     * @param key           键名
     * @return true-加锁成功 false-加锁失败
     */
    public static Boolean releaseLock(RedisTemplate redisTemplate, String key) {
        try {
            if (StringUtils.isEmpty(LUA_SCRIPT_LOCK_DEL)) {
                LUA_SCRIPT_LOCK_DEL = getLuaScript("META-INF/scripts/lock_del.lua");
            }
            RedisScript<Boolean> script = RedisScript.of(LUA_SCRIPT_LOCK_DEL, Boolean.class);
            return (Boolean) redisTemplate.execute(script, singletonList(key));
        } catch (Exception ex) {
            BaseLogger baseLogger = BaseLoggerBuilder.create()
                    .withSystemNumber(SystemNumberHelper.getSystemNumber())
                    .withTraceId(UUIDUtils.randomSimpleUUID())
                    .withClientIp(RequestUtils.getClientIp())
                    .withServerIp(RequestUtils.getServerIp())
                    .withTriggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                    .withUrl("Redis")
                    .withRequestParams("key", key)
                    .withBody(PrintExceptionInfo.printErrorInfo(ex.getCause()))
                    .build();
            logger.info(JsonUtils.toJSONString(baseLogger));
            return false;
        }
    }
```

##### 五、加锁和解锁控制器

```java
    @GetMapping("tryGetLock")
    public boolean tryGetLock() {
        return LuaScriptTools.tryGetLock(redisTemplate, "mykey", Duration.ofSeconds(60));
    }

    @GetMapping("releaseLock")
    public boolean releaseLock() {
        return LuaScriptTools.releaseLock(redisTemplate, "mykey");
    }
```

