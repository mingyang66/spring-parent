package com.emily.infrastructure.rateLimiter.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.concurrent.TimeUnit;

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
    int before(String key);

    /**
     * 当前用户已访问次数+1
     *
     * @param key      缓存key
     * @param timeout  超时时间
     * @param timeunit 单位
     */
    void after(String key, long timeout, TimeUnit timeunit);
}
