package com.emily.infrastructure.redis.tracing;

import io.lettuce.core.tracing.TraceContext;
import io.lettuce.core.tracing.TraceContextProvider;
import io.lettuce.core.tracing.TracerProvider;
import io.lettuce.core.tracing.Tracing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

public class LoggingTracing implements Tracing {

    private static final Logger logger = LoggerFactory.getLogger(LoggingTracing.class);

    private final TracerProvider tracerProvider = new LoggingTracerProvider();
    // 对于简单的日志追踪，我们提供一个不返回任何上下文的提供者，
    // 让 Lettuce 知道当前没有活跃的追踪上下文，从而开始一个新的追踪。
    private final TraceContextProvider initialTraceContextProvider = new NoOpTraceContextProvider();
    private final boolean includeCommandArgsInSpanTags;

    public LoggingTracing(boolean includeCommandArgsInSpanTags) {
        this.includeCommandArgsInSpanTags = includeCommandArgsInSpanTags;
    }

    public LoggingTracing() {
        this(false);
    }

    @Override
    public TracerProvider getTracerProvider() {
        return tracerProvider;
    }

    @Override
    public TraceContextProvider initialTraceContextProvider() {
        return initialTraceContextProvider;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean includeCommandArgsInSpanTags() {
        return includeCommandArgsInSpanTags;
    }

    @Override
    public Endpoint createEndpoint(SocketAddress socketAddress) {
        if (socketAddress instanceof java.net.InetSocketAddress) {
            java.net.InetSocketAddress inetAddr = (java.net.InetSocketAddress) socketAddress;
            return new LoggingEndpoint(inetAddr.getHostString(), inetAddr.getPort());
        }
        logger.info("Cannot create endpoint for unsupported SocketAddress type: {}", socketAddress.getClass().getName());
        return new LoggingEndpoint("unknown", 0);
    }

    // 简单的 Endpoint 实现
    static class LoggingEndpoint implements Endpoint {
        private final String host;
        private final int port;

        LoggingEndpoint(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public String host() {
            return host;
        }

        public int port() {
            return port;
        }
    }

    // 一个不返回任何上下文的 TraceContextProvider 实现
    static class NoOpTraceContextProvider implements TraceContextProvider {
        @Override
        public TraceContext getTraceContext() {
            // 返回 null 表示没有活跃的追踪上下文，Lettuce 会启动新的追踪。
            return null;
        }
    }
}