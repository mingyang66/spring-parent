package com.emily.infrastructure.rabbitmq.diagnostics.analyzer;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * Redis默认数据库配置故障分析
 *
 * @author :  Emily
 * @since :  2024/7/1 下午20:46
 */
public class DataRabbitIdentifierFailureAnalyzer extends AbstractFailureAnalyzer<@NonNull IllegalArgumentException> {
    @Override
    protected FailureAnalysis analyze(@NonNull Throwable rootFailure, IllegalArgumentException cause) {
        return new FailureAnalysis(cause.getMessage(), rootFailure.getMessage(), cause);
    }
}
