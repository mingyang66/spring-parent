package com.emily.infrastructure.rabbitmq.listener;

import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import com.emily.infrastructure.logback.entity.BaseLogger;
import com.emily.infrastructure.logger.event.LogEventType;
import com.emily.infrastructure.logger.event.LogPrintApplicationEvent;
import com.emily.infrastructure.tracing.holder.LocalContextHolder;
import com.otter.infrastructure.servlet.RequestUtils;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * RabbitMQ消息发送未找到合适的队列消息退回回调类
 *
 * @author :  Emily
 * @since :  2025/12/21 下午2:05
 */
public class DataRabbitReturnsCallback implements RabbitTemplate.ReturnsCallback {
    private final ApplicationContext context;

    public DataRabbitReturnsCallback(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void returnedMessage(ReturnedMessage returned) {
        // 处理回退消息的逻辑
        context.publishEvent(new LogPrintApplicationEvent(LogEventType.PLATFORM, new BaseLogger()
                .systemNumber(LocalContextHolder.current().getSystemNumber())
                .appType(LocalContextHolder.current().getAppType())
                .appVersion(LocalContextHolder.current().getAppVersion())
                .traceId(LocalContextHolder.current().getTraceId())
                .clientIp(LocalContextHolder.current().getClientIp())
                .serverIp(RequestUtils.getServerIp())
                .triggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                .url("RabbitMQ-ReturnsCallback")
                .body("回退消息: " + new String(returned.getMessage().getBody(), StandardCharsets.UTF_8) +
                        ", 交换机: " + returned.getExchange() +
                        ", 路由键: " + returned.getRoutingKey() +
                        ", 原因: " + returned.getReplyText())));
    }
}
