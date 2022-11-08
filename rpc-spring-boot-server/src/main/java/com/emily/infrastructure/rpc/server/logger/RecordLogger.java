package com.emily.infrastructure.rpc.server.logger;

import com.emily.infrastructure.common.enums.DateFormat;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.common.entity.BaseLogger;
import com.emily.infrastructure.rpc.core.message.IRpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * @param request  请求协议
     * @param response 响应结果
     */
    public static void recordResponse(IRpcRequest request, Object response, long startTime) {
        try {
            BaseLogger baseLogger = new BaseLogger();
            baseLogger.setTraceId(request.getTraceId());
            baseLogger.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormat.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
            baseLogger.setUrl(MessageFormat.format("{0}.{1}", request.getClassName(), request.getMethodName()));
            baseLogger.setRequestParams(request.getParams());
            baseLogger.setBody(response);
            baseLogger.setSpentTime(System.currentTimeMillis() - startTime);
            logger.info(JSONUtils.toJSONString(baseLogger));
        } catch (Exception exception) {

        }
    }
}
