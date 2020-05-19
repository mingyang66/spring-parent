package com.sgrain.boot.web.api;

import com.sgrain.boot.common.utils.CharacterUtils;
import com.sgrain.boot.common.utils.TokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 自动生成token令牌控制器
 */
@RestController
@RequestMapping("/api/token")
public class TokenApiController {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 自动生成token令牌，并将令牌存入缓存，过期时间是30s
     */
    @PostMapping("generation")
    public ResponseEntity<String> generationToken(){
        String token = TokenUtils.generation();
        redisTemplate.opsForValue().set(StringUtils.join("idempotent", CharacterUtils.COLON_EN, token), token, 30, TimeUnit.SECONDS);
        return ResponseEntity.ok(token);
    }
}
