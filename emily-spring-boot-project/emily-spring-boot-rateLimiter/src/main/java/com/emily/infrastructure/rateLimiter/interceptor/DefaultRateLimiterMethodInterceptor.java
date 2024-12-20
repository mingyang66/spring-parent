package com.emily.infrastructure.rateLimiter.interceptor;

import com.emily.infrastructure.common.ObjectUtils;
import com.emily.infrastructure.common.StringUtils;
import com.emily.infrastructure.rateLimiter.annotation.RateLimiterOperation;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 限流默认拦截器
 *
 * @author :  Emily
 * @since :  2024/8/29 下午5:37
 */
public class DefaultRateLimiterMethodInterceptor implements RateLimiterCustomizer {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        RateLimiterOperation rateLimiter = invocation.getMethod().getAnnotation(RateLimiterOperation.class);
        // 解析key，替换变量
        String key = resolveKey(invocation, rateLimiter.value());
        try {
            int count = before(key);
            if (count >= rateLimiter.threshold()) {
                throw new IllegalAccessException(rateLimiter.message());
            }
            return invocation.proceed();
        } finally {
            // 当前访问次数+1
            after(key, rateLimiter.timeout(), rateLimiter.timeunit());
        }
    }

    /**
     * 解析缓存key
     */
    @Override
    public String resolveKey(MethodInvocation invocation, String key) {
        Assert.notNull(key, "key must not be null");
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
            if (value instanceof String str) {
                list.add(str);
            } else {
                throw new IllegalArgumentException("非法限流入参");
            }
        }
        return String.format(key, list.toArray());
    }

    /**
     * 获取当前key映射已访问次数
     *
     * @param key 缓存key
     * @return 访问次数
     */
    @Override
    public int before(String key) {
        return 0;
    }

    /**
     * 访问结束，访问数量+1
     *
     * @param key      缓存key
     * @param timeout  超时时间
     * @param timeunit 单位
     */
    @Override
    public void after(String key, long timeout, TimeUnit timeunit) {

    }
}
