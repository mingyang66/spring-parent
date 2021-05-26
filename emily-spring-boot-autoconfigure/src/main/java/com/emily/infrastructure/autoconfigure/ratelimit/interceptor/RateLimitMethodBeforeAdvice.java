package com.emily.infrastructure.autoconfigure.ratelimit.interceptor;

import com.emily.infrastructure.autoconfigure.ratelimit.annotation.ApiRateLimit;
import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BusinessException;
import com.emily.infrastructure.common.utils.RequestUtils;
import com.emily.infrastructure.common.utils.constant.CharacterUtils;
import com.emily.infrastructure.common.utils.hash.Md5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collections;

/**
 * 接口访问的频率控制
 */
public class RateLimitMethodBeforeAdvice implements MethodBeforeAdvice {
    /**
     * Redis客户端
     */
    private StringRedisTemplate stringRedisTemplate;

    public RateLimitMethodBeforeAdvice(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        //获取限流注解对象
        ApiRateLimit limit = method.getAnnotation(ApiRateLimit.class);
        //限流功能关闭
        if (!limit.enable()) {
            return;
        }
        //获取Request请求对象
        HttpServletRequest request = RequestUtils.getRequest();
        //url 进行md5 hash作为键值
        String key = Md5Utils.computeMd5Hash(request.getRequestURL().toString());
        //执行lua脚本，将数据存储到redis缓存
        long data = stringRedisTemplate.execute(RedisScript.of(new ClassPathResource("META-INF/scripts/rateLimit.lua"), Long.class),
                Collections.singletonList(StringUtils.join("ratelimt", CharacterUtils.COLON_EN, key)),
                String.valueOf(limit.permits()),
                String.valueOf(limit.time())
                );
        if (data == 0L) {
            throw new BusinessException(AppHttpStatus.API500_EXCEPTION);
        }
    }
}
