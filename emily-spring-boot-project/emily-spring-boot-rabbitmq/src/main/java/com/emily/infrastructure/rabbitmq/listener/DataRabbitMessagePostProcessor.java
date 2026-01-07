package com.emily.infrastructure.rabbitmq.listener;

import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.logback.entity.BaseLogger;
import com.emily.infrastructure.logger.event.LogEventType;
import com.emily.infrastructure.logger.event.LogPrintApplicationEvent;
import com.emily.infrastructure.tracing.holder.LocalContextHolder;
import com.otter.infrastructure.servlet.RequestUtils;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Correlation;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.context.ApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息发送前对消息进行预处理
 *
 * @author :  Emily
 * @since :  2025/12/21 下午4:42
 */
public class DataRabbitMessagePostProcessor implements MessagePostProcessor {
    private final ApplicationContext context;

    public DataRabbitMessagePostProcessor(ApplicationContext context) {
        this.context = context;
    }

    @Override
    @NonNull
    public Message postProcessMessage(@NonNull Message message) throws AmqpException {
        return message;
    }

    @Override
    @NonNull
    public Message postProcessMessage(@NonNull Message message, @Nullable Correlation correlation, @NonNull String exchange, @NonNull String routingKey) {
        MessageProperties messageProperties = message.getMessageProperties();
        String returnCorrelation = messageProperties.getHeader("spring_listener_return_correlation");
        context.publishEvent(new LogPrintApplicationEvent(LogEventType.PLATFORM, new BaseLogger()
                .systemNumber(LocalContextHolder.current().getSystemNumber())
                .appType(LocalContextHolder.current().getAppType())
                .appVersion(LocalContextHolder.current().getAppVersion())
                .traceId(LocalContextHolder.current().getTraceId())
                .clientIp(LocalContextHolder.current().getClientIp())
                .serverIp(RequestUtils.getServerIp())
                .triggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                .url("RabbitMQ-Publish")
                .body(new HashMap<>(Map.ofEntries(
                        Map.entry("Message", JsonUtils.toJSONString(new String(message.getBody(), StandardCharsets.UTF_8))),
                        Map.entry("Exchange", exchange),
                        Map.entry("RoutingKey", routingKey),
                        Map.entry("spring_listener_return_correlation", returnCorrelation == null ? StringUtils.EMPTY : returnCorrelation)
                )))
        ));

        return MessagePostProcessor.super.postProcessMessage(message, correlation, exchange, routingKey);
    }
}
