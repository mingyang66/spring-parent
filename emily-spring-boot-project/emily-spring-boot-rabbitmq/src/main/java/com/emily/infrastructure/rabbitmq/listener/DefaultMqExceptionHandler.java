package com.emily.infrastructure.rabbitmq.listener;

import com.emily.infrastructure.common.UUIDUtils;
import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.logback.entity.BaseLogger;
import com.emily.infrastructure.logger.utils.PrintLogUtils;
import com.emily.infrastructure.tracing.helper.SystemNumberHelper;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.impl.StrictExceptionHandler;

import java.time.LocalDateTime;

/**
 * 默认异常处理
 *
 * @author :  Emily
 * @since :  2023/8/22 5:33 PM
 */
public class DefaultMqExceptionHandler extends StrictExceptionHandler {
    @Override
    public void handleUnexpectedConnectionDriverException(Connection conn, Throwable exception) {
        super.handleUnexpectedConnectionDriverException(conn, exception);
        BaseLogger baseLogger = new BaseLogger()
                .systemNumber(SystemNumberHelper.getSystemNumber())
                .traceId(UUIDUtils.randomSimpleUUID())
                //.withClientIp(RequestUtils.getClientIp())
                //.withServerIp(RequestUtils.getServerIp())
                .triggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                .url("RabbitMQ")
                .body("An unexpected connection driver error occurred" + " (Exception message: " + exception.getMessage() + ")");
        PrintLogUtils.printThirdParty(JsonUtils.toJSONString(baseLogger));
    }
}
