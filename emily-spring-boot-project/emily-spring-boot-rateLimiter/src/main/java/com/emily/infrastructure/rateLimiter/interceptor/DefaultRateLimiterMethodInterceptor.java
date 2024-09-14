package com.emily.infrastructure.rateLimiter.interceptor;

import com.emily.infrastructure.common.ObjectUtils;
import com.emily.infrastructure.common.StringUtils;
import com.emily.infrastructure.rateLimiter.annotation.RateLimiter;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 限流默认拦截器
 *
 * @author :  Emily
 * @since :  2024/8/29 下午5:37
 */
public class DefaultRateLimiterMethodInterceptor implements RateLimiterCustomizer {

    /**
     * 解析缓存key
     */
    @Override
    public String resolveKey(MethodInvocation invocation, String key) {
        int count = StringUtils.countOfContains(key, "%s");
        if (count < 1) {
            return key;
        }
        Object[] args = invocation.getArguments();
        if (args.length < count) {
            throw new IllegalArgumentException("缺少限流入参");
        }
        List<String> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Object value = args[i];
            if (ObjectUtils.isEmpty(value)) {
                throw new IllegalArgumentException("非法限流入参");
            }
            if (!(value instanceof String)) {
                throw new IllegalArgumentException("非法限流入参");
            }
            list.add((String) value);
        }
        return String.format(key, list.toArray());
    }

    @Override
    public int getVisitedTimes(String key) {
        return 0;
    }

    @Override
    public void addVisitedTimes(String key, RateLimiter rateLimiter) {

    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if (method.isAnnotationPresent(RateLimiter.class)) {
            RateLimiter rateLimiter = method.getAnnotation(RateLimiter.class);
            // 解析key，替换变量
            String key = resolveKey(invocation, rateLimiter.key());
            // 获取已访问次数
            int count = getVisitedTimes(key);
            // 获取最大访问次数
            int maxPermits = rateLimiter.maxPermits();
            if (count >= maxPermits) {
                throw new IllegalAccessException(rateLimiter.message());
            }
            // 当前访问次数+1
            addVisitedTimes(key, rateLimiter);
        }
        return invocation.proceed();
    }
}
