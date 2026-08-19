package com.emily.infrastructure.rateLimiter.interceptor;

import com.emily.infrastructure.common.ObjectUtils;
import com.emily.infrastructure.rateLimiter.annotation.RateLimiterOperation;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 限流默认拦截器
 *
 * @author :  Emily
 * @since :  2024/8/29 下午5:37
 */
public class DefaultRateLimiterMethodInterceptor implements RateLimiterCustomizer {
    private static final Cache<String, AtomicLong> CACHE = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        RateLimiterOperation rateLimiter = invocation.getMethod().getAnnotation(RateLimiterOperation.class);
        // 解析key，替换变量
        String key = resolveKey(invocation, rateLimiter.value());
        try {
            long count = countAccess(key);
            if (count >= rateLimiter.threshold()) {
                throw new IllegalAccessException(rateLimiter.message());
            }
            return invocation.proceed();
        } finally {
            // 当前访问次数+1
            recordAccess(key, rateLimiter.timeout(), rateLimiter.timeunit());
        }
    }

    /**
     * 解析缓存key
     */
    @Override
    public String resolveKey(MethodInvocation invocation, String key) {
        Assert.notNull(key, "key must not be null");
        int count = countPlaceholders(key);
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
        return MessageFormat.format(key, list.toArray());
    }

    /**
     * 获取当前key映射已访问次数
     *
     * @param key 缓存key
     * @return 访问次数
     */
    @Override
    public long countAccess(String key) {
        return Optional.ofNullable(CACHE.getIfPresent(key)).orElse(new AtomicLong(0L)).get();
    }

    /**
     * 访问结束，访问数量+1
     *
     * @param key      缓存key
     * @param timeout  超时时间
     * @param timeunit 单位
     */
    @Override
    public void recordAccess(String key, long timeout, TimeUnit timeunit) {
        AtomicLong atomicLong = Optional.ofNullable(CACHE.getIfPresent(key)).orElse(new AtomicLong(0L));
        atomicLong.incrementAndGet();
        CACHE.put(key, atomicLong);
    }

    int countPlaceholders(String format) {
        // 匹配非单引号包围的{数字}格式
        Pattern pattern = Pattern.compile("(?<!')\\{([0-9]+)(,[^}]*)?}(?!')");
        Matcher matcher = pattern.matcher(format);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}
