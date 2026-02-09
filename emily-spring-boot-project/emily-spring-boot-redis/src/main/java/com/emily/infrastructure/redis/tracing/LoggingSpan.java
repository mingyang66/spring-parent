package com.emily.infrastructure.redis.tracing;

import io.lettuce.core.protocol.CommandArgs;
import io.lettuce.core.protocol.ProtocolKeyword;
import io.lettuce.core.protocol.RedisCommand;
import io.lettuce.core.tracing.Tracer;
import io.lettuce.core.tracing.Tracing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingSpan extends Tracer.Span {

    private static final Logger logger = LoggerFactory.getLogger(LoggingSpan.class);

    private final String operationName;
    private volatile boolean started = false;
    private volatile boolean finished = false;
    private volatile long startTimeNanos = 0;
    private volatile String commandString = "Unknown Command";
    private volatile String errorDescription = null;
    private volatile LoggingTracing.LoggingEndpoint remoteEndpoint = null;

    public LoggingSpan(String operationName) {
        this.operationName = operationName;
    }

    @Override
    public Tracer.Span start(RedisCommand<?, ?, ?> command) {
        if (!started && !finished) {
            this.started = true;
            this.startTimeNanos = System.nanoTime();

            ProtocolKeyword type = command.getType();
            CommandArgs<?, ?> args = command.getArgs();

            if (type != null) {
                StringBuilder sb = new StringBuilder(type.toString());
                if (args != null) {
                    sb.append(" ").append(args.toCommandString());
                }
                this.commandString = sb.toString();
            } else {
                this.commandString = "UNKNOWN_COMMAND";
            }

            logger.info("Started tracing span for command: {}", commandString);
        }
        return this;
    }

    @Override
    public Tracer.Span name(String name) {
        return this;
    }

    @Override
    public Tracer.Span annotate(String value) {
        if (!finished) {
            logger.info("Annotation added to span [{}]: {}", commandString, value);
        }
        return this;
    }

    @Override
    public Tracer.Span tag(String key, String value) {
        if (!finished) {
            logger.info("Tag added to span [{}] - {}: {}", commandString, key, value);
            if ("peer.address".equals(key)) {
                String[] parts = value.split(":");
                if (parts.length == 2) {
                    this.remoteEndpoint = new LoggingTracing.LoggingEndpoint(parts[0], Integer.parseInt(parts[1]));
                }
            }
        }
        return this;
    }

    @Override
    public Tracer.Span error(Throwable throwable) {
        if (!finished) {
            this.errorDescription = throwable.getMessage();
            logger.warn("Error recorded on span [{}]: {}", commandString, errorDescription, throwable);
        }
        return this;
    }

    @Override
    public Tracer.Span remoteEndpoint(Tracing.Endpoint endpoint) {
        if (!finished) {
            if (endpoint instanceof LoggingTracing.LoggingEndpoint) {
                this.remoteEndpoint = (LoggingTracing.LoggingEndpoint) endpoint;
                logger.info("Remote endpoint set for span [{}]: {}:{}", commandString, remoteEndpoint.host(), remoteEndpoint.port());
            }
        }
        return this;
    }

    @Override
    public void finish() {
        if (!finished) {
            this.finished = true;
            long endTimeNanos = System.nanoTime();
            double durationMs = (endTimeNanos - startTimeNanos) / 1_000_000.0;

            String hostInfo = (remoteEndpoint != null) ? remoteEndpoint.host() + ":" + remoteEndpoint.port() : "unknown";
            if (errorDescription != null) {
                logger.warn("REDIS TRACE [{}] FAILED on {} in {:.3f}ms. Command: '{}'. Error: '{}'",
                        operationName, hostInfo, durationMs, commandString, errorDescription);
            } else {
                logger.info("REDIS TRACE [{}] SUCCEEDED on {} in {:.3f}ms. Command: '{}'",
                        operationName, hostInfo, durationMs, commandString);
            }
        }
    }
}