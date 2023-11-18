#### Redis学习笔记17：基于spring data redis及lua脚本批处理scan指令查询永久有效的key

> Redis的KEYS和SCAN指令都可以用于在数据库中搜索匹配指定模式的键。然而，它们之间有一些关键的区别；
>
> KEYS指令会在整个数据库中阻塞地执行匹配操作，并返回匹配的键列表。如果数据库很大，或者匹配的键很多，将会对性能产生负面影响。而SCAN指令通过游标的方式逐步迭代数据库，每次返回一小部分匹配的键，不会阻塞数据库，可以在不影响其它操作的情况下进行遍历。
>
> KEYS指令会返回匹配的键列表，这可能会导致返回的结果集很大，可能会占用大量的内存。而SCAN指令每次返回一小部分匹配的键，并通过游标来迭代，可以有效的处理大型结果集。

一个对springboot redis框架进行重写，支持lettuce、jedis、连接池、同时连接多个集群、多个redis数据库、开发自定义属性配置的开源SDK

```xml
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-redis</artifactId>
    <version>4.4.0</version>
</dependency>
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

##### 一、基于SCAN指令批量查询TTL永久有效的lua脚本

```lua
-- 游标位置
local cursor = tonumber(ARGV[1])
-- 一次查询出的数量
local count = tonumber(ARGV[2])
-- 匹配模式
local pattern = '*'
-- SCAN cursor [MATCH pattern] [COUNT count] 迭代数据库中的数据库键
local value = redis.call('SCAN', cursor, 'MATCH', pattern, 'COUNT', count)
-- 下次循环的游标
local nextCursor = value[1]
-- 当前批次的数据
local data = value[2]
-- 符合条件的数据集合
local result = {}
for i, key in ipairs(data) do
    -- 查询键对应过期时间
    local ttl = redis.call('TTL', key)
    if ttl == -1 then
        table.insert(result, key)
    end
end
return { nextCursor, result }

```

##### 二、基于spring data redis调用lua脚本通过游标批量获取数据

```java
    /**
     * @param redisTemplate redis 模板工具类
     * @return TTL为-1的键集合列表
     */
    public static List<String> ttlScanKeys(RedisTemplate redisTemplate, long count) {
        try {
            if (StringUtils.isEmpty(LUA_SCRIPT_TTL_SCAN_KEYS)) {
                LUA_SCRIPT_TTL_SCAN_KEYS = getLuaScript("META-INF/scripts/ttl_scan_keys.lua");
            }
            RedisScript<List> script = RedisScript.of(LUA_SCRIPT_TTL_SCAN_KEYS, List.class);
            List<String> result = new ArrayList<>();
            long cursor = 0;
            do {
                List<Object> list = (List<Object>) redisTemplate.execute(script, SerializationUtils.jackson2JsonRedisSerializer(), SerializationUtils.stringSerializer(), null, cursor, count);
                // 游标
                cursor = Long.valueOf(list.get(0).toString());
                // 符合条件的键值
                result.addAll(JsonUtils.toJavaBean(JsonUtils.toJSONString(list.get(1)), List.class, String.class));
            } while (cursor != 0);
            return result;
        } catch (Exception ex) {
            BaseLogger baseLogger = BaseLoggerBuilder.create()
                    .withSystemNumber(SystemNumberHelper.getSystemNumber())
                    .withTraceId(UUIDUtils.randomSimpleUUID())
                    .withClientIp(RequestUtils.getClientIp())
                    .withServerIp(RequestUtils.getServerIp())
                    .withTriggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                    .withUrl("Redis")
                    .withRequestParams("count", count)
                    .withBody(PrintExceptionInfo.printErrorInfo(ex.getCause()))
                    .build();
            logger.info(JsonUtils.toJSONString(baseLogger));
            return Collections.emptyList();
        }
    }
```

##### 三、调用lua脚本控制器

```java
    @GetMapping("ttlBatch")
    public List<String> batch() {
        return LuaScriptTools.ttlScanKeys(redisTemplate, 100);
    }
```

##### 四、redis scan指令批量获取指定数量数据集为啥有微小浮动

当使用Redis的SCAN指令进行迭代时，返回的数据量可能会有微小的浮动。这是因为SCAN指令的迭代器在每次迭代时会根据当前键的分布情况来确定返回的键的数据。

Redis使用一种称为游标（cursor）的概念来迭代键空间。游标是一个指示迭代状态的无符号64位整数，它标识了迭代器在键空间中的位置。在每次迭代时，Redis会根据游标的位置扫描一小部分键，并返回给客户端。

由于Redis是一个并发数据库，可能会有其他客户端在迭代过程中对数据库进行修改。这些修改可能会导致迭代器在下一次迭代时返回不同数量的键。例如，如果在迭代期间有新的键被添加到数据库，那么在下一次迭代时，迭代器可能会返回更多的键。相反，如果在迭代期间有键被删除，那么迭代器可能会返回更少的键。

Redis的`SCAN`指令每次返回的数据量可能会有微小的浮动，这是由键的分布和并发操作的影响造成的。