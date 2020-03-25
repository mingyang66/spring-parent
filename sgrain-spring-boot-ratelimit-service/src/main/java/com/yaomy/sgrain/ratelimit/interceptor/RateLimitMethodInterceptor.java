package com.yaomy.sgrain.ratelimit.interceptor;

import com.google.common.collect.Lists;
import com.yaomy.sgrain.common.enums.SgrainHttpStatus;
import com.yaomy.sgrain.common.utils.Md5Utils;
import com.yaomy.sgrain.common.utils.RequestUtil;
import com.yaomy.sgrain.common.utils.json.JSONUtils;
import com.yaomy.sgrain.exception.business.BusinessException;
import com.yaomy.sgrain.ratelimit.annotation.RateLimit;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @program: spring-parent
 * @description: 接口访问的频率控制
 * @author: 姚明洋
 * @create: 2020/03/23
 */
public class RateLimitMethodInterceptor implements MethodInterceptor {
    /**
     * 分号
     */
    private static final String SEMICOLON = ":";
    /**
     * Redis客户端
     */
    private RedisTemplate redisTemplate;
    /**
     * lua脚本
     */
    private RedisScript<Long> redisScript;

    public RateLimitMethodInterceptor(RedisTemplate redisTemplate, RedisScript<Long> redisScript){
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
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //lua脚本key值
        List<String> keys = Lists.newArrayList();
        //客户端IP
        String clientIp = RequestUtil.getClientIp(request);
        //接口url
        String url = request.getRequestURI();
        //拼接key值
        String key = StringUtils.join(clientIp, SEMICOLON, url);
        //获取限流注解对象
        RateLimit limiter = method.getAnnotation(RateLimit.class);
        if(ArrayUtils.isNotEmpty(limiter.name())){
            Map<String, Object> paramMap = RequestUtil.getRequestParam(request, invocation);
            System.out.println(JSONUtils.toJSONString(paramMap));
            for(String name:limiter.name()){
                key = StringUtils.join(key, SEMICOLON, paramMap.get(name));
            }
        }
        //clientIp+url+name 进行md5 hash作为键值
        keys.add(Md5Utils.computeMD5Hash(key));
        //执行lua脚本，将数据存储到redis缓存
        long result = NumberUtils.toLong(redisTemplate.execute(redisScript, keys, limiter.permits(), limiter.time()).toString());
        if(result == 0L){
            throw new BusinessException(SgrainHttpStatus.RATE_LIMIT_EXCEPTION);
        }
        return invocation.proceed();
    }
}
