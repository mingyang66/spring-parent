package com.emily.infrastructure.rpc.server.logger;

import com.emily.infrastructure.common.enums.DateFormatEnum;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.core.entity.BaseLogger;
import com.emily.infrastructure.rpc.core.entity.message.IRHead;
import com.emily.infrastructure.rpc.core.entity.protocol.IRProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @program: spring-parent
 * @description: 日志记录
 * @author: Emily
 * @create: 2021/10/21
 */
public class RecordLogger {

    private static final Logger logger = LoggerFactory.getLogger(RecordLogger.class);

    /**
     * 记录请求响应日志
     *
     * @param head     请求头
     * @param protocol 请求协议
     * @param response 响应结果
     */
    public static void recordResponse(IRHead head, IRProtocol protocol, Object response, long startTime) {
        try {
            BaseLogger baseLogger = new BaseLogger();
            baseLogger.setTraceId(new String(head.getTraceId(), StandardCharsets.UTF_8));
            baseLogger.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
            baseLogger.setMethod("RPC");
            baseLogger.setUrl(MessageFormat.format("{0}.{1}", protocol.getClassName(), protocol.getMethodName()));
            baseLogger.setRequestParams(protocol.getParams());
            baseLogger.setBody(response);
            baseLogger.setTime(System.currentTimeMillis() - startTime);
            logger.info(JSONUtils.toJSONString(baseLogger));
        } catch (Exception exception) {

        }
    }
}
