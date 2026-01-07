package com.emily.infrastructure.rabbitmq.listener;

import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.logback.entity.BaseLogger;
import com.emily.infrastructure.logger.event.LogEventType;
import com.emily.infrastructure.logger.event.LogPrintApplicationEvent;
import com.emily.infrastructure.tracing.holder.LocalContextHolder;
import com.otter.infrastructure.servlet.RequestUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.context.ApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author :  Emily
 * @since :  2025/12/21 下午4:17
 */
public class DataRabbitListenerMethodInterceptor implements MethodInterceptor {
    private final ApplicationContext context;

    public DataRabbitListenerMethodInterceptor(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public @Nullable Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            Object[] args = invocation.getArguments();
            Message message = argToMessage(args);
            MessageProperties messageProperties = message.getMessageProperties();
            String returnCorrelation = messageProperties.getHeader("spring_listener_return_correlation");
            // 处理回退消息的逻辑
            context.publishEvent(new LogPrintApplicationEvent(LogEventType.PLATFORM, new BaseLogger()
                    .systemNumber(LocalContextHolder.current().getSystemNumber())
                    .appType(LocalContextHolder.current().getAppType())
                    .appVersion(LocalContextHolder.current().getAppVersion())
                    .traceId(LocalContextHolder.current().getTraceId())
                    .clientIp(LocalContextHolder.current().getClientIp())
                    .serverIp(RequestUtils.getServerIp())
                    .triggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                    .url("RabbitMQ-Subscribe")
                    .body(new HashMap<>(Map.ofEntries(
                            Map.entry("Message", JsonUtils.toJSONString(new String(message.getBody(), StandardCharsets.UTF_8))),
                            Map.entry("ReceivedExchange", Objects.requireNonNull(message.getMessageProperties().getReceivedExchange())),
                            Map.entry("ReceivedRoutingKey", Objects.requireNonNull(message.getMessageProperties().getReceivedRoutingKey())),
                            Map.entry("ConsumerQueue", Objects.requireNonNull(message.getMessageProperties().getConsumerQueue())),
                            Map.entry("ContentType", Objects.requireNonNull(message.getMessageProperties().getContentType())),
                            Map.entry("spring_listener_return_correlation", returnCorrelation == null ? StringUtils.EMPTY : returnCorrelation)
                    )))
            ));
            return invocation.proceed();
        } finally {
            LocalContextHolder.unbind();
        }
    }

    /**
     * org.springframework.amqp.rabbit.config.StatefulRetryOperationsInterceptor#argToMessage(java.lang.Object[])
     */
    private static Message argToMessage(@Nullable Object[] args) {
        Object arg = args.length > 1 ? args[1] : null;
        if (arg instanceof Message msg) {
            return msg;
        } else if (arg instanceof List<?> list) {
            return (Message) list.getFirst();
        } else {
            throw new IllegalArgumentException("Expected 2nd arguments to be a message, got " + Arrays.toString(args));
        }
    }
}
