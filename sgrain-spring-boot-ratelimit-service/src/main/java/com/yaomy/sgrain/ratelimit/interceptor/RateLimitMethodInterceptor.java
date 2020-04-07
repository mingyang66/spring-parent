package com.yaomy.sgrain.ratelimit.interceptor;

import com.google.common.collect.Lists;
import com.yaomy.sgrain.common.enums.SgrainHttpStatus;
import com.yaomy.sgrain.common.utils.Md5Utils;
import com.yaomy.sgrain.common.utils.RequestUtils;
import com.yaomy.sgrain.exception.business.BusinessException;
import com.yaomy.sgrain.ratelimit.annotation.RateLimit;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 接口访问的频率控制
 */
public class RateLimitMethodInterceptor implements MethodInterceptor {
    /**
     * 分号
     */
    private static final String SEMICOLON = ":";
    /**
     * Redis客户端
     */
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * lua脚本
     */
    private RedisScript<Long> redisScript;

    public RateLimitMethodInterceptor(RedisTemplate<String, Object> redisTemplate, RedisScript<Long> redisScript){
        this.redisTemplate = redisTemplate;
        this.redisScript = redisScript;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if(!method.isAnnotationPresent(RateLimit.class)){
            return invocation.proceed();
        }
        //获取Request请求对象
        HttpServletRequest request = RequestUtils.getRequest();
        //lua脚本key值
        List<String> keys = Lists.newArrayList();
        //客户端IP
        String clientIp = RequestUtils.getClientIp(request);
        //接口url
        String url = request.getRequestURI();
        //拼接key值
        String key = StringUtils.join(clientIp, SEMICOLON, url);
        //获取限流注解对象
        RateLimit limit = method.getAnnotation(RateLimit.class);
        if(ArrayUtils.isNotEmpty(limit.name())){
            Map<String, Object> paramMap = RequestUtils.getRequestParam(request, invocation);
            for(String name:limit.name()){
                key = StringUtils.join(key, SEMICOLON, paramMap.get(name));
            }
        }
        //clientIp+url+name 进行md5 hash作为键值
        keys.add(Md5Utils.computeMD5Hash(key));
        //执行lua脚本，将数据存储到redis缓存
        Long data = redisTemplate.execute(redisScript, keys, limit.permits(), limit.time());
        if(data != null && data == 0L){
            throw new BusinessException(SgrainHttpStatus.RATE_LIMIT_EXCEPTION);
        }
        return invocation.proceed();
    }
}
