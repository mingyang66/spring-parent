package com.yaomy.sgrain.idempotent.api;

import com.yaomy.sgrain.common.utils.TokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @program: spring-parent
 * @description: 自动生成token令牌控制器
 * @author: 姚明洋
 * @create: 2020/03/31
 */
@RestController
@RequestMapping("token")
public class TokenApiController {

    private RedisTemplate redisTemplate;

    public TokenApiController(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    /**
     * 自动生成token令牌，并将令牌存入缓存，过期时间是30s
     * @return
     */
    @PostMapping("generation")
    public ResponseEntity<String> generationToken(){
        String token = TokenUtils.generation();
        redisTemplate.opsForValue().set(token, token, 30, TimeUnit.SECONDS);
        return ResponseEntity.ok(token);
    }
}
