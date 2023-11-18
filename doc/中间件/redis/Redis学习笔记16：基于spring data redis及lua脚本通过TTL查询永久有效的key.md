#### Redis学习笔记16：基于spring data redis及lua脚本通过TTL查询永久有效的key

> Redis做为一个缓存服务，个人觉得不应该存在有永久有效的数据，要检索一个存在很久的redis服务器中存在哪些永久有效的key，可以通过lua脚本的方式实现；

一个对springboot redis框架进行重写，支持lettuce、jedis、连接池、同时连接多个集群、多个redis数据库、开发自定义属性配置的开源SDK

```xml
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-redis</artifactId>
    <version>4.4.0</version>
</dependency>
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

##### 一、TTL过期时间为-1的lua脚本

```lua
local success, result = pcall(function()
    -- 获取所有键
    local keys = redis.call('KEYS','*')
    -- 初始化表
    local result = {}
    for i, key in ipairs(keys) do
        -- 获取键的过期时间
        local ttl = redis.call('TTL', key)
        if ttl == -1 then
            -- 在table的数组部分指定位置(pos)插入值为value的一个元素. pos参数可选, 默认为数组部分末尾.
            table.insert(result, key)
        end
    end
    return result
end)

if success then
    return result
else
    return result
end
```

> 首先通过KEYS指令查询所有的键值，然后循环列表，通过TTL指令获取键值的过期时间，如果过期时间为-1，则将键值添加进列表，最后返回给客户端。

##### 二、spring data redis实现脚本加载并执行

```java
    /**
     * @param redisTemplate redis 模板工具类
     * @return TTL为-1的键集合列表
     */
    public static List<String> ttlKeys(RedisTemplate redisTemplate) {
        if (StringUtils.isEmpty(LUA_SCRIPT_TTL_KEYS)) {
            LUA_SCRIPT_TTL_KEYS = getLuaScript("META-INF/scripts/ttl_keys.lua");
        }
        RedisScript<List> script = RedisScript.of(LUA_SCRIPT_TTL_KEYS, List.class);
        return (List<String>) redisTemplate.execute(script, SerializationUtils.stringSerializer(), SerializationUtils.stringSerializer(), null);
    }

   public static StringRedisSerializer stringSerializer() {
        return new StringRedisSerializer();
    }
```

> 通过lua脚本查询键值数据时需指定key-value的序列化方式为StringRedisSerializer，否则会采用默认的Jackson2JsonRedisSerializer序列化方式，查询结果返回给java端后会抛出序列化异常。

##### 三、控制器

```java
    @GetMapping("ttl")
    public List<String> ttl(){
        List<String> list = LuaScriptTools.ttlKeys(redisTemplate);
        return list;
    }
```

