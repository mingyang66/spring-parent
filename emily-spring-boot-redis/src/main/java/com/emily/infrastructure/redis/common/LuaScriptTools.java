package com.emily.infrastructure.redis.common;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.emily.infrastructure.redis.common.SerializationUtils.jackson2JsonRedisSerializer;
import static com.emily.infrastructure.redis.common.SerializationUtils.stringSerializer;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * 基于lua脚本的相关组件
 *
 * @author :  Emily
 * @since :  2023/11/9 11:07 PM
 */
public class LuaScriptTools {
    /**
     * 基于lua列表的环形结构实现脚本
     */
    public static String LUA_SCRIPT_LIST_CIRCLE;
    /**
     * 基于redis ZSET有序集合的环形结构脚本
     */
    public static String LUA_SCRIPT_ZSET_CIRCLE;
    /**
     * 查询永久有效的key
     */
    public static String LUA_SCRIPT_TTL_KEYS;
    /**
     * 批量查询永久有效的key
     */
    public static String LUA_SCRIPT_TTL_SCAN_KEYS;
    /**
     * 基于SET指令的锁脚本
     */
    public static String LUA_SCRIPT_LOCK_GET;
    /**
     * 释放锁脚本
     */
    public static String LUA_SCRIPT_LOCK_DEL;

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

    /**
     * 基于lua脚本的限流工具
     *
     * @param redisTemplate redis模板工具类
     * @param key           限流键名
     * @param threshold     限流阀值
     * @param expire        限流的时间窗口
     * @return true-访问有效，false-超过阀值
     */
    public static boolean limit(RedisTemplate<Object, Object> redisTemplate, String key, int threshold, int expire) {
        RedisScript<Long> script = RedisScript.of(new ClassPathResource("META-INF/scripts/limit.lua"), Long.class);
        // 0：超过阀值 1：访问有效
        Long count = redisTemplate.execute(script, singletonList(key), threshold, expire);
        return count != null && count == 1L;
    }

    /**
     * 基于列表（List）的环
     * <pre>
     * 1. 支持一直有效，threshold 设置为小于等于0或null
     * 2. 支持设置有效时长，动态刷新，interval大于0
     * </pre>
     *
     * @param redisTemplate redis 模板工具
     * @param key           环的键值
     * @param value         列表值
     * @param threshold     阀值，列表长度，即环上数据个数
     * @param expire        有效时长, 为null则永久有效
     * @return true-执行成功 false-执行失败
     */
    public static boolean listCircle(RedisTemplate<Object, Object> redisTemplate, String key, Object value, long threshold, Duration expire) {
        try {
            if (LUA_SCRIPT_LIST_CIRCLE == null || LUA_SCRIPT_LIST_CIRCLE.isBlank()) {
                LUA_SCRIPT_LIST_CIRCLE = getLuaScript("META-INF/scripts/circle_list.lua");
            }
            RedisScript<Long> script = RedisScript.of(LUA_SCRIPT_LIST_CIRCLE, Long.class);
            if (expire == null) {
                expire = Duration.ZERO;
            }
            redisTemplate.execute(script, singletonList(key), value, threshold, expire.getSeconds());
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    /**
     * 基于ZSET有序集合构建环形结构
     * <pre>
     * 1. 环上有一个阀值上线；
     * 2. 环可以设置有效期；
     * 3. 环上节点达到上限后移除分数最低的成员
     * </pre>
     *
     * @param redisTemplate redis模板工具类
     * @param key           键名
     * @param score         分数, 可以使用时间戳做为分值
     * @param value         成员值
     * @param threshold     阀值
     * @param expire        过期时间
     * @return true-执行成功 false-执行失败
     */
    public static boolean zSetCircle(RedisTemplate<Object, Object> redisTemplate, String key, long score, Object value, long threshold, Duration expire) {
        try {
            if (LUA_SCRIPT_ZSET_CIRCLE == null || LUA_SCRIPT_ZSET_CIRCLE.isBlank()) {
                LUA_SCRIPT_ZSET_CIRCLE = getLuaScript("META-INF/scripts/circle_zset.lua");
            }
            RedisScript<Long> script = RedisScript.of(LUA_SCRIPT_ZSET_CIRCLE, Long.class);
            if (expire == null) {
                expire = Duration.ZERO;
            }
            redisTemplate.execute(script, singletonList(key), score, value, threshold, expire.getSeconds());
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    /**
     * 通过redis的keys指令一次性获取所有TTL是永久有效的key列表
     * 如果数据量过大会阻塞redis服务器
     *
     * @param redisTemplate redis 模板工具类
     * @return TTL为-1的键集合列表
     */
    public static List<String> ttlKeys(RedisTemplate<Object, Object> redisTemplate) {
        if (LUA_SCRIPT_TTL_KEYS == null || LUA_SCRIPT_TTL_KEYS.isBlank()) {
            LUA_SCRIPT_TTL_KEYS = getLuaScript("META-INF/scripts/ttl_keys.lua");
        }
        RedisScript<List> script = RedisScript.of(LUA_SCRIPT_TTL_KEYS, List.class);
        return Objects.requireNonNull(redisTemplate.execute((RedisCallback<List<String>>) connection -> {
            List<byte[]> list = connection.scriptingCommands().evalSha(script.getSha1(), ReturnType.fromJavaType(script.getResultType()), 0);
            if (list == null) return emptyList();
            return list.stream().map(f -> stringSerializer().deserialize(f)).toList();
        }));
    }


    /**
     * 使用redis的scan指令批量从redis中获取TTL是永久有效的key列表
     *
     * @param redisTemplate redis 模板工具类
     * @return TTL为-1的键集合列表
     */
    public static List<String> ttlScanKeys(RedisTemplate<Object, Object> redisTemplate, long count) {
        try {
            if (LUA_SCRIPT_TTL_SCAN_KEYS == null || LUA_SCRIPT_TTL_SCAN_KEYS.isBlank()) {
                LUA_SCRIPT_TTL_SCAN_KEYS = getLuaScript("META-INF/scripts/ttl_scan_keys.lua");
            }
            RedisScript<List> script = RedisScript.of(LUA_SCRIPT_TTL_SCAN_KEYS, List.class);
            List<String> result = new ArrayList<>();
            long cursor = 0;
            do {
                byte[] cursorBytes = jackson2JsonRedisSerializer().serialize(cursor);
                List<byte[]> list = redisTemplate.execute((RedisCallback<List<byte[]>>) connection -> {
                    List<byte[]> list1 = connection.scriptingCommands().eval(script.getScriptAsString().getBytes(StandardCharsets.UTF_8), ReturnType.fromJavaType(script.getResultType()), 0, cursorBytes, jackson2JsonRedisSerializer().serialize(count));
                    if (list1 == null) return emptyList();
                    return list1;
                });
                // 游标
                cursor = Long.parseLong(Objects.requireNonNull(stringSerializer().deserialize(list.get(0))).toString());
                Object s11 = list.get(1);
                if (s11 instanceof List listResult) {
                    for (Object obj : listResult) {
                        result.add(stringSerializer().deserialize((byte[]) obj));
                    }
                }

            } while (cursor != 0);
            return result;
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    /**
     * 尝试获取锁
     * 只有在key不存在的时候才可以加锁成功
     * 解决问题：
     * 1. 死锁问题：try catch finally无论是否发生异常都执行释放锁操作，给锁设定一个过期时间；
     * 2. 锁被其它线程释放问题：A线程获取锁，锁过期时间5S, 由于某些原因5S结束A线程还未执行完成，锁自动过期了；B线程获取锁，此时A线程执行完成，释放锁，但是释放的是B线程的锁；
     * 可以给每个锁设置一个标识，如UUID
     * <p>
     * 无法解决问题：
     * 1. 锁续期问题：程序未执行完成，锁自动过期释放掉了。
     *
     * @param redisTemplate redis 模板工具类
     * @param key           键名
     * @param value         锁标识
     * @param expire        过期时间
     * @return true-加锁成功 false-加锁失败
     */
    public static Boolean tryGetLock(RedisTemplate<Object, Object> redisTemplate, String key, Object value, Duration expire) {
        try {
            if (LUA_SCRIPT_LOCK_GET == null || LUA_SCRIPT_LOCK_GET.isBlank()) {
                LUA_SCRIPT_LOCK_GET = getLuaScript("META-INF/scripts/lock_get.lua");
            }
            RedisScript<Boolean> script = RedisScript.of(LUA_SCRIPT_LOCK_GET, Boolean.class);
            return redisTemplate.execute(script, singletonList(key), value, expire.getSeconds());
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 释放指定的锁，如果锁不存在，则忽略
     *
     * @param redisTemplate redis 模板工具类
     * @param key           键名
     * @param value         锁标识
     * @return true-加锁成功 false-加锁失败
     */
    public static Boolean releaseLock(RedisTemplate<Object, Object> redisTemplate, String key, Object value) {
        try {
            if (LUA_SCRIPT_LOCK_DEL == null || LUA_SCRIPT_LOCK_DEL.isBlank()) {
                LUA_SCRIPT_LOCK_DEL = getLuaScript("META-INF/scripts/lock_del.lua");
            }
            RedisScript<Boolean> script = RedisScript.of(LUA_SCRIPT_LOCK_DEL, Boolean.class);
            return redisTemplate.execute(script, singletonList(key), value);
        } catch (Exception ex) {
            return false;
        }
    }
}
