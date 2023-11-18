#### Redis学习笔记14：基于spring data redis及lua脚本ZSET有序集合实现环形结构案例及lua脚本如何发送到redis服务器

> 案例实现目标，一、实现一个环形结构，环形结构上节点有一个阀值threshold,超过阀值则移除分数score最低的成员，不足则将当前成员添加进环中，且确保成员不可重复；二、每次访问环中的数据都需要刷新key的过期时间；

一个对springboot redis框架进行重写，支持lettuce、jedis、连接池、同时连接多个集群、多个redis数据库、开发自定义属性配置的开源SDK

```xml
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-redis</artifactId>
    <version>4.4.0</version>
</dependency>
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

##### 一、环形结构lua脚本

```lua
-- 键名
local key = KEYS[1]
-- 分数
local score = tonumber(ARGV[1])
-- 值
local value = ARGV[2]
-- 阀值
local threshold = tonumber(ARGV[3])
-- 超时时间
local expire = tonumber(ARGV[4])

local success, error = pcall(function(key, score, value, threshold, expire)
    -- 返回有序成员的分数
    local exists = redis.call('ZSCORE', key, value)
    if not exists then
        -- 获取有序集合的成员数
        local len = tonumber(redis.call('ZCARD', key))
        if (len >= threshold) then
            -- 移除有序集合中给定的排名区间的所有成员
            redis.call('ZREMRANGEBYRANK', key, 0, 0)
        end
        -- 向有序集合添加一个或多个成员，或者更新已存在成员的分数
        redis.call('ZADD', key, score, value)
    end
    -- 超时时间必须大于0，否则永久有效
    if expire > 0 then
        -- 设置超时时间
        redis.call('EXPIRE', key, expire)
    end
end, key, score, value, threshold, expire)

-- 判定脚本是否执行成功
if success then
    return 1
else
    return error
end
```

##### 二、spring data redis调用lua脚本工具方法

```java
    /**
     * 基于ZSET有序集合构建环形结构
     * 1. 环上有一个阀值上线；
     * 2. 环可以设置有效期；
     * 3. 环上节点达到上限后移除分数最低的成员
     *
     * @param redisTemplate redis模板工具类
     * @param key           键名
     * @param score         分数, 可以使用时间戳做为分值
     * @param value         成员值
     * @param threshold     阀值
     * @param expire        过期时间
     * @return true-执行成功 false-执行失败
     */
    public static boolean zSetCircle(RedisTemplate redisTemplate, String key, long score, Object value, long threshold, Duration expire) {
        try {
            if (StringUtils.isEmpty(LUA_SCRIPT_ZSET_CIRCLE)) {
                LUA_SCRIPT_ZSET_CIRCLE = getLuaScript("META-INF/scripts/zset_circle.lua");
            }
            RedisScript<Long> script = RedisScript.of(LUA_SCRIPT_ZSET_CIRCLE, Long.class);
            if (expire == null) {
                expire = Duration.ZERO;
            }
            redisTemplate.execute(script, singletonList(key), score, value, threshold, expire.getSeconds());
            return true;
        } catch (Throwable ex) {
            BaseLogger baseLogger = BaseLoggerBuilder.create()
                    .withSystemNumber(SystemNumberHelper.getSystemNumber())
                    .withTraceId(UUIDUtils.randomSimpleUUID())
                    .withClientIp(RequestUtils.getClientIp())
                    .withServerIp(RequestUtils.getServerIp())
                    .withTriggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                    .withUrl("Redis")
                    .withRequestParams(key, value)
                    .withRequestParams("score", score)
                    .withRequestParams("threshold", threshold)
                    .withRequestParams("expire", expire.getSeconds())
                    .withBody(PrintExceptionInfo.printErrorInfo(ex.getCause()))
                    .build();
            logger.info(JsonUtils.toJSONString(baseLogger));
            return false;
        }
    }
   /**
     * 获取lua脚本
     *
     * @param filePath 脚本路径
     * @return lua字符串脚本
     */
    public static String getLuaScript(String filePath) {
        try {
            return new ClassPathResource(filePath).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
```

##### 三、控制器方法

```java
    @GetMapping("zset")
    public boolean zset() {
        String value = RequestUtils.getHeader("value");
        return LuaScriptTools.zSetCircle(redisTemplate, "test-script-zset", System.currentTimeMillis(), value, 3, Duration.ofSeconds(60));
    }
```

##### 四、什么是SHA1摘要

SHA1摘要是对lua脚本的内容进行哈希计算的结果。它是一个40个字符的十六进制字符串。

在Redis中，使用SCRIPT LOAD命令可以将lua脚本加载到服务器，并返回一个SHA1摘要，这个摘要可以被用于后续的EVALSHA名利来执行脚本。

SHA1摘要的作用是将脚本内容映射为一个唯一的标识符，以便在多次执行脚本时，可以通过传输摘要而不是完整的脚本内容来提高效率。

示例如下：

```sh
127.0.0.1:6379> SCRIPT LOAD "return redis.call('GET', 'mykey')"
"8f1c7a686c72f6ddc6e3b0b9a7e1f4d8d89563a8"
127.0.0.1:6379> EVALSHA "8f1c7a686c72f6ddc6e3b0b9a7e1f4d8d89563a8" 1 mykey
"myvalue"

```

##### 五、spring data redis如何将lua脚本发送到服务器端？

如果将日志级别调整为debug级别，将会在日志中发现有这样一条信息：

```sh
io.lettuce.core.RedisNoScriptException: NOSCRIPT No matching script. Please use EVAL.
```

这条日志信息说明在redis服务器端通过客户端的SHA1字符串未找到对应的lua脚本，需要我们将lua脚本发送到服务器端并执行指令:

org.springframework.data.redis.core.script.DefaultScriptExecutor#eval方法是发送脚本及执行脚本的核心代码：

```java
	protected <T> T eval(RedisConnection connection, RedisScript<T> script, ReturnType returnType, int numKeys,
			byte[][] keysAndArgs, RedisSerializer<T> resultSerializer) {

		Object result;
		try {
      // 通过SHA1编码发送到redis服务器，并查询对应的脚本，如果存在，则执行，如果不存在抛出异常
			result = connection.evalSha(script.getSha1(), returnType, numKeys, keysAndArgs);
		} catch (Exception e) {
 			//异常信息会带有：io.lettuce.core.RedisNoScriptException: NOSCRIPT No matching script. Please use EVAL.
			if (!ScriptUtils.exceptionContainsNoScriptError(e)) {
				throw e instanceof RuntimeException ? (RuntimeException) e : new RedisSystemException(e.getMessage(), e);
			}
			//将lua脚本发送到redis服务器端，并根据参数执行脚本
			result = connection.eval(scriptBytes(script), returnType, numKeys, keysAndArgs);
		}

		if (script.getResultType() == null) {
			return null;
		}
		//解析执行脚本后返回的结果
		return deserializeResult(resultSerializer, result);
	}
```

根据上述分析，spring data redis执行lua脚本分为如下步骤：

1. 从lua文件中读取lua脚本，转换为字符串；
2. 对lua脚本字符串进行SHA1编码，得到一个40个字符串的十六进制字符；
3. 通过connection对象的evalSha方法将SHA1编码字符串及要执行的参数发送到redis服务器，redis服务器通过SHA1字符串查找对应的lua脚本，如果查找到则根据参数执行对应的脚本，并返回执行结果；如果未查询到脚本，则会抛出异常，执行如下步骤。
4. 通过connection对象的eval方法将lua脚本及执行脚本需要的参数信息发送到redis服务器，redis服务器会将lua脚本保存一份，并执行脚本，返回执行结果；