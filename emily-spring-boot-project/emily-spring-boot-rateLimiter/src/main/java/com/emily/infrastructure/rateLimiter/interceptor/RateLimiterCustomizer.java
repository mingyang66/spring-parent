package com.emily.infrastructure.rateLimiter.interceptor;

import com.emily.infrastructure.rateLimiter.annotation.RateLimiter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 限流拦截器接口
 *
 * @author :  Emily
 * @since :  2024/8/29 下午5:36
 */
public interface RateLimiterCustomizer extends MethodInterceptor {
    /**
     * 解析缓存key,对key中的变量进行替换
     */
    String resolveKey(MethodInvocation invocation, String key);

    /**
     * 获取当前用户已访问次数
     *
     * @param key 缓存key
     * @return 访问次数
     */
    int getVisitedTimes(String key);

    /**
     * 当前用户已访问次数+1
     *
     * @param key 缓存key
     */
    void addVisitedTimes(String key, RateLimiter rateLimiter);
}
