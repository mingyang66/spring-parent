package com.emily.infrastructure.redis.tracing;

import io.lettuce.core.tracing.Tracer;
import io.lettuce.core.tracing.TracerProvider;

public class LoggingTracerProvider implements TracerProvider {

    @Override
    public Tracer getTracer() {
        // 返回一个固定的 Tracer 实例
        // 这个 Tracer 将负责处理所有来自 Lettuce 的追踪请求
        return new LoggingTracer("lettuce.command"); // 我们在这里硬编码了操作名称
    }
}