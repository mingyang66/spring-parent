package com.emily.infrastructure.rabbitmq.listener;

import com.emily.infrastructure.common.PrintExceptionUtils;
import com.emily.infrastructure.common.UUIDUtils;
import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import com.emily.infrastructure.logback.entity.BaseLogger;
import com.emily.infrastructure.logger.utils.PrintLoggerUtils;
import com.emily.infrastructure.tracing.helper.SystemNumberHelper;
import com.otter.infrastructure.servlet.RequestUtils;
import com.rabbitmq.client.ShutdownSignalException;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;

import java.time.LocalDateTime;

/**
 * rabbit连接监听器
 *
 * @author :  Emily
 * @since :  2023/8/23 9:45 AM
 */
public class DefaultMqConnectionListener implements ConnectionListener {

    private final CachingConnectionFactory connectionFactory;

    public DefaultMqConnectionListener(CachingConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**
     * Called when a new connection is established.
     *
     * @param connection the connection.
     */
    @Override
    public void onCreate(Connection connection) {
        BaseLogger baseLogger = new BaseLogger()
                .systemNumber(SystemNumberHelper.getSystemNumber())
                .traceId(UUIDUtils.randomSimpleUUID())
                .clientIp(RequestUtils.getClientIp())
                .serverIp(RequestUtils.getServerIp())
                .triggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                .url("RabbitMQ")
                .body("Created new connection [Called when a new connection is established]: " + connectionFactory.toString() + "/" + connection);
        PrintLoggerUtils.printThirdParty(baseLogger);
    }

    /**
     * Called when a connection is closed.
     *
     * @param connection the connection.
     */
    @Override
    public void onClose(Connection connection) {
        ConnectionListener.super.onClose(connection);
        BaseLogger baseLogger = new BaseLogger()
                .systemNumber(SystemNumberHelper.getSystemNumber())
                .traceId(UUIDUtils.randomSimpleUUID())
                .clientIp(RequestUtils.getClientIp())
                .serverIp(RequestUtils.getServerIp())
                .triggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                .url("RabbitMQ")
                .body("Close [Called when a connection is closed]: " + connection);
        PrintLoggerUtils.printThirdParty(baseLogger);
    }

    /**
     * Called when a connection is force closed.
     *
     * @param signal the shut down signal.
     */
    @Override
    public void onShutDown(ShutdownSignalException signal) {
        ConnectionListener.super.onShutDown(signal);
        BaseLogger baseLogger = new BaseLogger()
                .systemNumber(SystemNumberHelper.getSystemNumber())
                .traceId(UUIDUtils.randomSimpleUUID())
                .clientIp(RequestUtils.getClientIp())
                .serverIp(RequestUtils.getServerIp())
                .triggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                .url("RabbitMQ")
                .body("ShutDown [Called when a connection is force closed] " + signal.getMessage());
        PrintLoggerUtils.printThirdParty(baseLogger);
    }

    /**
     * Called when a connection couldn't be established.
     *
     * @param exception the exception thrown.
     */
    @Override
    public void onFailed(Exception exception) {
        ConnectionListener.super.onFailed(exception);
        BaseLogger baseLogger = new BaseLogger()
                .systemNumber(SystemNumberHelper.getSystemNumber())
                .traceId(UUIDUtils.randomSimpleUUID())
                .clientIp(RequestUtils.getClientIp())
                .serverIp(RequestUtils.getServerIp())
                .triggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                .url("RabbitMQ")
                .body("Failed [Called when a connection couldn't be established] " + PrintExceptionUtils.printErrorInfo(exception));
        PrintLoggerUtils.printThirdParty(baseLogger);
    }
}
