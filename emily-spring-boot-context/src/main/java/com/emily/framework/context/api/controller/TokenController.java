package com.emily.framework.context.api.controller;

import com.emily.framework.common.utils.UUIDUtils;
import com.emily.framework.common.utils.constant.CharacterUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @program: spring-parent
 * @description: token令牌生成器
 * @create: 2020/08/26
 */
@RestController
@ConditionalOnClass(StringRedisTemplate.class)
@RequestMapping("token")
public class TokenController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 自动生成token令牌，并将令牌存入缓存，过期时间是30s
     */
    @GetMapping("generation")
    public ResponseEntity<String> generationToken() {
        String token = UUIDUtils.generation();
        stringRedisTemplate.opsForValue().set(StringUtils.join("idempotent", CharacterUtils.COLON_EN, token), token, 30, TimeUnit.SECONDS);
        return ResponseEntity.ok(token);
    }
}
