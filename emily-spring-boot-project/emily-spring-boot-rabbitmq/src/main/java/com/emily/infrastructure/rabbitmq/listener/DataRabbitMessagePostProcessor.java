package com.emily.infrastructure.rabbitmq.listener;

import com.emily.infrastructure.common.constant.AttributeInfo;
import com.emily.infrastructure.common.constant.HeaderInfo;
import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.logback.entity.BaseLogger;
import com.emily.infrastructure.logger.event.LogEventType;
import com.emily.infrastructure.logger.event.LogPrintApplicationEvent;
import com.emily.infrastructure.rabbitmq.common.DataRabbitInfo;
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
import java.util.Objects;

/**
 * RabbitMQ生产端发送消息前对消息进行预处理，拦截发送消息内容
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
        MessageProperties properties = message.getMessageProperties();
        //消息请求头添加请求上下文唯一标识
        properties.setHeader(HeaderInfo.TRACE_ID, LocalContextHolder.current().getTraceId());
        //链路追踪标记
        properties.setHeader(HeaderInfo.TRACE_TAG, Objects.requireNonNullElse(properties.getHeader(HeaderInfo.TRACE_TAG), StringUtils.EMPTY));
        context.publishEvent(new LogPrintApplicationEvent(context, LogEventType.PLATFORM, new BaseLogger()
                .systemNumber(LocalContextHolder.current().getSystemNumber())
                .appType(LocalContextHolder.current().getAppType())
                .appVersion(LocalContextHolder.current().getAppVersion())
                .traceId(LocalContextHolder.current().getTraceId())
                .traceTag(Objects.requireNonNullElse(properties.getHeader(HeaderInfo.TRACE_TAG), StringUtils.EMPTY))
                .traceTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                .clientIp(LocalContextHolder.current().getClientIp())
                .serverIp(RequestUtils.getServerIp())
                .url("RabbitMQ-Publish")
                .outParams(AttributeInfo.OUT_PARAMS, new HashMap<>(Map.ofEntries(
                        Map.entry("Message", JsonUtils.toJSONString(new String(message.getBody(), StandardCharsets.UTF_8))),
                        Map.entry("Exchange", exchange),
                        Map.entry("RoutingKey", routingKey),
                        Map.entry(DataRabbitInfo.RETURN_CORRELATION_KEY, Objects.requireNonNullElse(properties.getHeader(DataRabbitInfo.RETURN_CORRELATION_KEY), StringUtils.EMPTY))
                )))
        ));

        return MessagePostProcessor.super.postProcessMessage(message, correlation, exchange, routingKey);
    }
}
