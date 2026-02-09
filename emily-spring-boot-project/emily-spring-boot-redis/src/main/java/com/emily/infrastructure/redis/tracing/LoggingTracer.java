package com.emily.infrastructure.redis.tracing;

import io.lettuce.core.tracing.TraceContext;
import io.lettuce.core.tracing.Tracer;

public class LoggingTracer extends Tracer {

    private final String name;

    public LoggingTracer(String name) {
        this.name = name;
    }

    @Override
    public Span nextSpan() {
        // 创建一个新的根 Span
        return new LoggingSpan(name);
    }

    @Override
    public Span nextSpan(TraceContext traceContext) {
        // 如果有父上下文，创建子 Span
        // 对于日志追踪，我们可以简单地创建一个新 Span，或者基于 traceContext 添加信息
        // 这里简化处理，直接创建新 Span
        return new LoggingSpan(name);
    }
}