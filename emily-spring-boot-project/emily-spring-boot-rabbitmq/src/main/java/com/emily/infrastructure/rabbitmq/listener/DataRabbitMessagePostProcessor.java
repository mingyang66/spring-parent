package com.emily.infrastructure.rabbitmq.listener;

import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.logback.entity.BaseLogger;
import com.emily.infrastructure.logger.event.EventType;
import com.emily.infrastructure.logger.event.LoggerPrintApplicationEvent;
import com.emily.infrastructure.tracing.holder.LocalContextHolder;
import com.otter.infrastructure.servlet.RequestUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Correlation;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.context.ApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

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
        context.publishEvent(new LoggerPrintApplicationEvent(EventType.PLATFORM, new BaseLogger()
                .systemNumber(LocalContextHolder.current().getSystemNumber())
                .appType(LocalContextHolder.current().getAppType())
                .appVersion(LocalContextHolder.current().getAppVersion())
                .traceId(LocalContextHolder.current().getTraceId())
                .clientIp(LocalContextHolder.current().getClientIp())
                .serverIp(RequestUtils.getServerIp())
                .triggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                .url("RabbitMQ-SentMessage")
                .body("回退消息: " + new String(message.getBody(), StandardCharsets.UTF_8) +
                        ", 交换机: " + exchange +
                        ", 路由键: " + routingKey +
                        ", 消息属性: " + JsonUtils.toJSONString(message.getMessageProperties()))));

        return MessagePostProcessor.super.postProcessMessage(message, correlation, exchange, routingKey);
    }
}
