package com.emily.infrastructure.core.utils;

import com.emily.infrastructure.core.helper.ThreadPoolHelper;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.logger.LoggerFactory;
import org.slf4j.Logger;

/**
 * 日志工具类
 *
 * @author :  Emily
 * @since :  2024/1/1 4:12 PM
 */
public class PrintLoggerUtils {
    private static final Logger logger = LoggerFactory.getModuleLogger(PrintLoggerUtils.class, "api", "request");
    private static final Logger loggerThirdParty = LoggerFactory.getModuleLogger(PrintLoggerUtils.class, "api", "thirdParty");

    /**
     * 记录请求日志
     *
     * @param message 日志信息
     */
    public static void printRequest(Object message) {
        ThreadPoolHelper.defaultThreadPoolTaskExecutor().submit(() -> {
            logger.info(JsonUtils.toJSONString(message));
        });
    }

    /**
     * 记录三方请求日志
     *
     * @param message 日志信息
     */
    public static void printThirdParty(Object message) {
        ThreadPoolHelper.defaultThreadPoolTaskExecutor().submit(() -> {
            loggerThirdParty.info(JsonUtils.toJSONString(message));
        });
    }
}
