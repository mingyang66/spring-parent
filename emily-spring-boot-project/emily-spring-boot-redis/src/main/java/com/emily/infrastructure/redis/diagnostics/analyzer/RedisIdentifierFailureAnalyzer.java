package com.emily.infrastructure.redis.diagnostics.analyzer;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * Redis默认数据库配置故障分析
 *
 * @author :  Emily
 * @since :  2024/7/1 下午20:46
 */
public class RedisIdentifierFailureAnalyzer extends AbstractFailureAnalyzer<@NonNull NullPointerException> {
    @Override
    protected FailureAnalysis analyze(@NonNull Throwable rootFailure, NullPointerException cause) {
        return new FailureAnalysis(cause.getMessage(), "Redis默认数据库标识配置缺失", cause);
    }
}
