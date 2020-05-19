package com.sgrain.boot.autoconfigure.aop.interceptor;

import com.sgrain.boot.autoconfigure.aop.annotation.RateLimit;
import com.sgrain.boot.common.enums.AppHttpStatus;
import com.sgrain.boot.common.exception.BusinessException;
import com.sgrain.boot.common.utils.CharacterUtils;
import com.sgrain.boot.common.utils.Md5Utils;
import com.sgrain.boot.common.utils.RequestUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

/**
 * 接口访问的频率控制
 */
public class RateLimitMethodInterceptor implements MethodInterceptor {
    /**
     * Redis客户端
     */
    private RedisTemplate<String, Object> redisTemplate;

    public RateLimitMethodInterceptor(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //获取限流注解对象
        RateLimit limit = invocation.getMethod().getAnnotation(RateLimit.class);
        //限流功能关闭
        if (!limit.enable()) {
            return invocation.proceed();
        }
        //获取Request请求对象
        HttpServletRequest request = RequestUtils.getRequest();
        //url 进行md5 hash作为键值
        String key = Md5Utils.computeMD5Hash(request.getRequestURL().toString());
        //执行lua脚本，将数据存储到redis缓存
        long data = redisTemplate.execute(
                                            RedisScript.of(new ClassPathResource("META-INF/scripts/rateLimit.lua"), Long.class),
                                            Collections.singletonList(StringUtils.join("ratelimt", CharacterUtils.COLON_EN, key)),
                                            limit.permits(),
                                            limit.time());
        if (data == 0L) {
            throw new BusinessException(AppHttpStatus.RATE_LIMIT_EXCEPTION);
        }
        return invocation.proceed();
    }
}
