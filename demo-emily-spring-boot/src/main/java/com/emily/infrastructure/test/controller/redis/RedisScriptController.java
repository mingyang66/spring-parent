package com.emily.infrastructure.test.controller.redis;

import com.emily.infrastructure.core.helper.RequestUtils;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.redis.common.LuaScriptTools;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Arrays;

/**
 * @author :  Emily
 * @since :  2023/11/7 11:08 PM
 */
@RestController
@RequestMapping("api/redis")
public class RedisScriptController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("scr")
    public void scr() {
        RedisScript<Boolean> script = RedisScript.of(new ClassPathResource("META-INF/scripts/checkandset.lua"), Boolean.class);
        redisTemplate.execute(script, Arrays.asList("test-script"), "1", "2");

    }

    @GetMapping("extest")
    public long extest() {
        String value = "list" + RandomUtils.nextInt();
        if (StringUtils.isNotBlank(RequestUtils.getHeader("value"))) {
            value = RequestUtils.getHeader("value");
        }
        long count = LuaScriptTools.circle(redisTemplate, "set_expire:test", value, 3, Duration.ofSeconds(20));
        System.out.println("结果：" + count);
        return count;
    }

    public static void main(String[] args) {
        System.out.println(JsonUtils.toJSONString("2332"));
    }
}
