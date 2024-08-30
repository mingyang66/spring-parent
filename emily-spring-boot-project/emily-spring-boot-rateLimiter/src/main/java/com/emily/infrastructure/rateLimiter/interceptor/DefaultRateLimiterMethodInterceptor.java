package com.emily.infrastructure.rateLimiter.interceptor;

import com.emily.infrastructure.rateLimiter.annotation.RateLimiter;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author :  姚明洋
 * @since :  2024/8/29 下午5:37
 */
public class DefaultRateLimiterMethodInterceptor implements RateLimiterCustomizer {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("------------限流");
        Method method = invocation.getMethod();
        if (method.isAnnotationPresent(RateLimiter.class)) {
            RateLimiter rateLimiter = method.getAnnotation(RateLimiter.class);
            String prefix = rateLimiter.prefix();
            long expired = rateLimiter.expired();
        }
        return invocation.proceed();
    }
}
