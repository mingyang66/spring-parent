package com.emily.infrastructure.logger.utils;

import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.logback.factory.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 日志工具类
 *
 * @author :  Emily
 * @since :  2024/1/1 4:12 PM
 */
public class PrintLoggerUtils {
    /**
     * 记录请求日志
     *
     * @param message 日志信息
     */
    public static void printRequest(Object message) {
        defaultThreadPoolTaskExecutor().submit(() -> {
            LogHolder.LOG.info(JsonUtils.toJSONString(message));
        });
    }

    /**
     * 记录三方请求日志
     *
     * @param message 日志信息
     */
    public static void printThirdParty(Object message) {
        defaultThreadPoolTaskExecutor().submit(() -> {
            LogHolder.LOGTHIRDPARTY.info(JsonUtils.toJSONString(message));
        });
    }

    /**
     * 获取线程池
     *
     * @return ThreadPoolTaskExecutor
     */
    public static ThreadPoolTaskExecutor defaultThreadPoolTaskExecutor() {
        int poolSize = 8;
        int maxPoolSize = 64;
        int queueCapacity = 10000;
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(poolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    public static class LogHolder {
        private static final Logger LOG = LoggerFactory.getModuleLogger(PrintLoggerUtils.class, "api", "request");
        private static final Logger LOGTHIRDPARTY = LoggerFactory.getModuleLogger(PrintLoggerUtils.class, "api", "thirdParty");
    }
}
