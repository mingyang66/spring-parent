package com.emily.infrastructure.logback.factory;

import org.slf4j.Logger;

import java.util.function.Supplier;


/**
 * 日志工具类
 *
 * @author :  Emily
 * @since :  2024/1/1 4:12 PM
 */
public class LogPrintFactory {
    /**
     * 记录请求日志
     *
     * @param message 日志信息
     */
    public static void printRequest(String message) {
        LogHolder.REQUEST.info(message);
    }

    /**
     * 记录请求日志
     *
     * @param supplier 日志信息
     */
    public static void printRequest(Supplier<String> supplier) {
        Logger logger = LogHolder.REQUEST;
        if (logger.isInfoEnabled()) {
            logger.info(supplier.get());
        }
    }

    /**
     * 记录三方请求日志
     *
     * @param message 日志信息
     */
    public static void printThirdParty(String message) {
        LogHolder.THIRD_PARTY.info(message);
    }

    /**
     * 记录三方请求日志
     *
     * @param supplier 日志信息
     */
    public static void printThirdParty(Supplier<String> supplier) {
        Logger logger = LogHolder.THIRD_PARTY;
        if (logger.isInfoEnabled()) {
            logger.info(supplier.get());
        }
    }

    /**
     * 记录应用程序请求日志
     *
     * @param message 日志信息
     */
    public static void printPlatform(String message) {
        LogHolder.PLATFORM.info(message);
    }

    /**
     * 记录应用程序请求日志
     *
     * @param supplier 日志信息
     */
    public static void printPlatform(Supplier<String> supplier) {
        Logger logger = LogHolder.PLATFORM;
        if (logger.isInfoEnabled()) {
            logger.info(supplier.get());
        }
    }

    static class LogHolder {
        private static final Logger REQUEST = LoggerFactory.getModuleLogger(LogPrintFactory.class, "request", "request");
        private static final Logger THIRD_PARTY = LoggerFactory.getModuleLogger(LogPrintFactory.class, "thirdParty", "thirdParty");
        private static final Logger PLATFORM = LoggerFactory.getModuleLogger(LogPrintFactory.class, "platform", "platform");
    }

}
