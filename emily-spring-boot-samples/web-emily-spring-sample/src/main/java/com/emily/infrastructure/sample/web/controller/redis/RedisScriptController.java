package com.emily.infrastructure.sample.web.controller.redis;

import com.emily.infrastructure.redis.common.LuaScriptTools;
import com.otter.infrastructure.servlet.RequestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;

/**
 * @author :  Emily
 * @since :  2023/11/7 11:08 PM
 */
@RestController
@RequestMapping("api/redis")
public class RedisScriptController {

    private final RedisTemplate<Object, Object> redisTemplate;

    public RedisScriptController(@Qualifier("redisTemplate") RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("scr")
    public void scr() {
        RedisScript<Boolean> script = RedisScript.of(new ClassPathResource("META-INF/scripts/checkandset.lua"), Boolean.class);
        redisTemplate.execute(script, List.of("test-script"), "1", "2");

    }

    @GetMapping("list")
    public boolean list() {
        String value = "list" + RandomUtils.nextInt();
        if (StringUtils.isNotBlank(RequestUtils.getHeader("value"))) {
            value = RequestUtils.getHeader("value");
        }
        boolean count = LuaScriptTools.listCircle(redisTemplate, "test-script-list", value, 3, Duration.ofSeconds(20));
        System.out.println("结果：" + count);
        return count;
    }

    @GetMapping("zset")
    public boolean zset() {
        String value = RequestUtils.getHeader("value");
        return LuaScriptTools.zSetCircle(redisTemplate, "test-script-zset", System.currentTimeMillis(), value, 3, Duration.ofSeconds(60));
    }

    @GetMapping("ttl")
    public List<String> ttl() {
        List<String> list = LuaScriptTools.ttlKeys(redisTemplate);
        return list;
    }

    @GetMapping("ttlBatch")
    public List<String> batch() {
        return LuaScriptTools.ttlScanKeys(redisTemplate, 100);
    }

    @GetMapping("tryGetLock")
    public boolean tryGetLock() {
        return LuaScriptTools.tryGetLock(redisTemplate, "mykey", "123", Duration.ofSeconds(60));
    }

    @GetMapping("releaseLock")
    public boolean releaseLock() {
        String value = RequestUtils.getHeader("value");
        return LuaScriptTools.releaseLock(redisTemplate, "mykey", value);
    }
}
