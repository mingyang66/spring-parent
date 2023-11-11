#### Redis学习笔记12：基于springboot的redis客户端执行lua脚本

> Redis客户端允许通过eval指令直接将lua脚本发送到服务器端执行，服务器会阻塞其它指令的执行，确保脚本的原子性；

一个对springboot redis框架进行重写，支持lettuce、jedis、连接池、同时连接多个集群、多个redis数据库、开发自定义属性配置的开源SDK

```xml
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-redis</artifactId>
    <version>4.3.9</version>
</dependency>
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

##### 一、eval指令的语法如下：

```sh
EVAL script numkeys key [key ...] arg [arg ...]
```

- script是要执行的lua脚本；
- numkeys是脚本中用到的key的数量；
- key [key ...]是脚本中用到的key的列表；
- arg [arg ...]是脚本中用到的参数列表；

> eval指令的作用是将脚本发送给Redis服务器，然后由服务器执行脚本。脚本可以访问和操作Redis中的数据，并返回执行结果。

##### 二、Redis常用的指令

- redis.call：这是执行redis命令的常规方法。它接受一个redis命令和对应的参数，并返回命令的结果，如果命令执行失败或出现错误，redis.call会抛出异常并终端脚本的执行。
- redis.pcall：这是执行redis命令的安全方法，它会捕获命令执行过程中的错误并返回错误信息。与redis.call不同，redis.pcall不会抛出异常，而是返回一个包含错误信息的特殊值。这使得脚本能够继续执行而不会中断。
- redis.log：这是redis提供的用于在redis服务器的日志中打印信息。通过打印日志，您可以记录调试信息、错误消息或其它相关信息，以便进行故障排除和监视。

##### 三、Redis的相关指令

- SET key value EX seconds： 指令设置键值对及过期时间

- SET key value [EX seconds] [PX milliseconds] [NX|XX]：

  > - `key`：要设置的键名。
  > - `value`：要设置的值。
  > - `EX seconds`：可选参数，指定键的过期时间，以秒为单位。
  > - `PX milliseconds`：可选参数，指定键的过期时间，以毫秒为单位。
  > - `NX`：可选参数，只在键不存在时设置键的值。
  > - `XX`：可选参数，只在键已存在时设置键的值。
  >
  > 如果键已存在，SET指令会覆盖原有的值。
  >
  > 可以使用EX参数或PX参数来设置键的过期时间，如果同时设置了两个参数，以EX参数为准。
  >
  > 可以使用NX参数或XX参数来控制只在键不存在或键已存在时才设置值的行为。默认情况下，不使用这两个参数时，SET指令会始终设置值。

- INCRBY key increment： 指令对指定键增加指定值

- INCR key：执行自增操作，将key的值加1。

- EXPIRE key seconds：设置key的过期时间，单位为秒。

##### 四、基于lua脚本的限流案例

限流lua脚本：

```lua
-- 要限流的键名
local key = KEYS[1]
-- 限流阀值
local limit = tonumber(ARGV[1])
-- 限流的时间窗口
local interval = tonumber(ARGV[2])
-- 当前访问数量
local current = tonumber(redis.call('GET', key) or "0")
-- 0：超过阀值 1：访问有效
if current + 1 > limit then
    return 0
elseif current == 0 then
    redis.call('SET', key, 1)
    redis.call('EXPIRE', key, interval)
    return 1;
else
    redis.call('INCR', key)
    return 1
end

```

基于spring data redis的客户端代码：

```java
    /**
     * 基于lua脚本的限流工具
     *
     * @param redisTemplate redis模板工具类
     * @param key           限流键名
     * @param limit         限流阀值
     * @param interval      限流的时间窗口
     * @return true-访问有效，false-超过阀值
     */
    public static boolean limit(RedisTemplate redisTemplate, String key, int limit, int interval) {
        RedisScript<Long> script = RedisScript.of(new ClassPathResource("META-INF/scripts/limit.lua"), Long.class);
        // 0：超过阀值 1：访问有效
        Long count = (Long) redisTemplate.execute(script, Arrays.asList(key), limit, interval);
        return count == 1 ? true : false;
    }
```

##### 五、spring data redis执行lua脚本支持的返回数据类型

```java
public enum ReturnType {

	/**
	 * Returned as Boolean
	 */
	BOOLEAN,

	/**
	 * Returned as {@link Long}
	 */
	INTEGER,

	/**
	 * Returned as {@link List<Object>}
	 */
	MULTI,

	/**
	 * Returned as {@literal byte[]}
	 */
	STATUS,

	/**
	 * Returned as {@literal byte[]}
	 */
	VALUE;
	}
```

> 支持long、list、byte[]三种数据类型，所以如果指定为其它数据类型时会抛出异常。
