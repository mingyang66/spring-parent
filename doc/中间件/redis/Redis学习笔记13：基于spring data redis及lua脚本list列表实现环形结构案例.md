#### Redis学习笔记13：基于spring data redis及lua脚本list列表实现环形结构案例

> 工作过程中需要用到环形结构，确保环上的各个节点数据唯一，如果有新的不同数据到来，则将最早入环的数据移除，每次访问环形结构都自动刷新有效期；可以基于lua 的列表list结构来实现这一功能，lua脚本可以节省网络开销、确保操作的原子性。

一个对springboot redis框架进行重写，支持lettuce、jedis、连接池、同时连接多个集群、多个redis数据库、开发自定义属性配置的开源SDK

```xml
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-redis</artifactId>
    <version>4.4.0</version>
</dependency>
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

##### 一、lua脚本实现环形结构代码

```lua
-- 判定列表中是否包含指定的value
local function contains_value(key, value)
    -- 获取列表指定范围内的所有元素
    local elements = redis.call('LRANGE', key, 0, -1)
    -- 泛型for迭代器
    for k, v in pairs(elements) do
        if  v == value then
            return true
        end
    end
    return false
end

-- 列表键名
local key = KEYS[1]
-- 列表值
local value = ARGV[1]
-- 列表限制长度阀值
local threshold = tonumber(ARGV[2])
-- 超时时间，单位：秒
local expire = tonumber(ARGV[3] or '0')

-- pcall函数捕获多条指令执行时的异常
local success, result = pcall(function(key, value, threshold, expire)
    -- 获取列表长度
    local len = tonumber(redis.call('LLEN', key))

    -- 判定列表中是否包含value
    if not contains_value(key, value) then
        -- 根据列表长度与阀值比较
        if len >= threshold then
            -- 移出并获取列表的第一个元素
            redis.call('LPOP', key)
        end
        -- 在列表中添加一个或多个值到列表尾部
        redis.call('RPUSH', key, value)
    end
    -- 超时时间必须大于0，否则永久有效
    if expire > 0 then
        -- 设置超时时间
        redis.call('EXPIRE', key, expire)
    end
    -- 返回列表长度
    return redis.call('LLEN', key)
end, key, value, threshold, expire)

-- 执行成功，直接返回列表长度
if success then
    return result
else
    -- 异常，则直接将异常信息返回
    return result
end
```

> 上述代码采用redis的pcall指令，在lua多条指令执行过程中如果有异常发生，则立马终端执行，返回异常；

##### 二、spring data redis实现脚本执行逻辑

```java
    /**
     * 基于列表（List）的环
     * 1. 支持一直有效，threshold 设置为<=0或null
     * 2. 支持设置有效时长，动态刷新，interval大于0
     *
     * @param redisTemplate redis 模板工具
     * @param key           环的键值
     * @param value         列表值
     * @param threshold     阀值，列表长度，即环上数据个数
     * @param expire        有效时长, 为null则永久有效
     * @return 当前环（列表）长度
     */
    public static long circle(RedisTemplate redisTemplate, String key, Object value, long threshold, Duration expire) {
        RedisScript<Long> script = RedisScript.of(new ClassPathResource("META-INF/scripts/list_circle.lua"), Long.class);
        if (expire == null) {
            expire = Duration.ZERO;
        }
        return (Long) redisTemplate.execute(script, singletonList(key), value, threshold, expire.getSeconds());
    }
```

> 上述代码首先将lua脚本加载到内存中，然后将脚本进行解析，并将key及相关参数一起通过eval指令发送给redis服务器；这里遗留两个问题，一、lua脚本是如何加载到内存中的；二、每次访问同一个脚本是否需要重复读取。

##### 三、lua脚本执行发生异常

```sh
@user_script: 44: Unknown Redis command called from Lua script
```

> 上述异常是通过redis pcall指令捕获lua脚本执行错误信息，这些错误信息会被抛出到java代码之中，可以根据这些异常信息排查脚本错误。

##### 四、lua脚本是如何加载到内存中的？

- 首先通过如下代码创建RedisScript对象，实际是一个DefaultRedisScript对象：

```java
RedisScript<Long> script = RedisScript.of(new ClassPathResource("META-INF/scripts/list_circle.lua"), Long.class);
```

- 进入RedisTemplate#execute方法，追踪发现会调用DefaultRedisScript的getSha1方法

```java
	protected <T> T eval(RedisConnection connection, RedisScript<T> script, ReturnType returnType, int numKeys,
			byte[][] keysAndArgs, RedisSerializer<T> resultSerializer) {
    ...
			result = connection.evalSha(script.getSha1(), returnType, numKeys, keysAndArgs);
		...
	}
```

- DefaultRedisScript#getSha1方法实现如下

```java
	public String getSha1() {

		synchronized (shaModifiedMonitor) {
			if (sha1 == null || scriptSource.isModified()) {
        // 计算SHA1哈希值并转换为十六进制字符串
				this.sha1 = DigestUtils.sha1DigestAsHex(getScriptAsString());
			}
			return sha1;
		}
	}

	public String getScriptAsString() {

		try {
      //获取lua脚本字符串，通过ResourceScriptSource实现类
			return scriptSource.getScriptAsString();
		} catch (IOException e) {
			throw new ScriptingException("Error reading script text", e);
		}
	}


```

- ResourceScriptSource#getScriptAsString读取方法实现

```java
    public String getScriptAsString() throws IOException {
        synchronized(this.lastModifiedMonitor) {
            this.lastModified = this.retrieveLastModifiedTime();
        }

        Reader reader = this.resource.getReader();
       //从lua脚本中读取出脚本，转换为字符串返回
        return FileCopyUtils.copyToString(reader);
    }
```

> 通过上述代码可以清除的理顺lua脚本加载到内存中的整个过程，但是每次访问时都需要重复读取脚本；

##### 五、如何实现读取一次脚本，以后直接从脚本中加载？

上述方法是通过RedisScript的of方法获取脚本对象：

```java
	static <T> RedisScript<T> of(Resource resource, Class<T> resultType) {

		Assert.notNull(resource, "Resource must not be null");
		Assert.notNull(resultType, "ResultType must not be null");

		DefaultRedisScript<T> script = new DefaultRedisScript<>();
		script.setResultType(resultType);
		script.setLocation(resource);

		return script;
	}
```

RedisScript类其实还有另外一个接受lua脚本字符串的of方法，如下：

```java
	static <T> RedisScript<T> of(String script, Class<T> resultType) {

		Assert.notNull(script, "Script must not be null");
		Assert.notNull(resultType, "ResultType must not be null");

		return new DefaultRedisScript<>(script, resultType);
	}
```

可以将脚本读取出来之后存到静态变量中，以后每次直接从变量中获取就可以了：

```java
     /**
     * 基于lua列表的环形结构实现脚本
     */
    public static String LUA_SCRIPT_CIRCLE;
    
    public static long circle(RedisTemplate redisTemplate, String key, Object value, long threshold, Duration expire) {
        try {
            if (StringUtils.isEmpty(LUA_SCRIPT_CIRCLE)) {
                LUA_SCRIPT_CIRCLE = getLuaScript("META-INF/scripts/list_circle.lua");
            }
            RedisScript<Long> script = RedisScript.of(LUA_SCRIPT_CIRCLE, Long.class);
     }
```

