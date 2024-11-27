package com.emily.infrastructure.logger.utils;

import com.emily.infrastructure.logback.factory.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Objects;


/**
 * 日志工具类
 *
 * @author :  Emily
 * @since :  2024/1/1 4:12 PM
 */
public class PrintLogUtils {
    /**
     * 记录请求日志
     *
     * @param message 日志信息
     */
    public static void printRequest(String message) {
        ThreadPoolLogHelper.defaultThreadPoolTaskExecutor().submit(() -> LogHolder.LOG.info(message));
    }

    /**
     * 记录三方请求日志
     *
     * @param message 日志信息
     */
    public static void printThirdParty(String message) {
        ThreadPoolLogHelper.defaultThreadPoolTaskExecutor().submit(() -> LogHolder.LOGTHIRDPARTY.info(message));
    }

    static class LogHolder {
        private static final Logger LOG = LoggerFactory.getModuleLogger(PrintLogUtils.class, "api", "request");
        private static final Logger LOGTHIRDPARTY = LoggerFactory.getModuleLogger(PrintLogUtils.class, "api", "thirdParty");
    }

    public static class ThreadPoolLogHelper {
        private static ThreadPoolTaskExecutor taskExecutor;
        private static ApplicationContext context;

        public static void init(ApplicationContext context) {
            ThreadPoolLogHelper.context = context;
        }

        /**
         * 获取线程池
         *
         * @since 20230811 新增获取默认线程池
         */
        static ThreadPoolTaskExecutor defaultThreadPoolTaskExecutor() {
            try {
                if (Objects.nonNull(taskExecutor)) {
                    return taskExecutor;
                }
                taskExecutor = context.getBean(ThreadPoolTaskExecutor.class);
            } catch (Exception exception) {
                int poolSize = 8;
                int maxPoolSize = 64;
                int queueCapacity = 10000;
                taskExecutor = new ThreadPoolTaskExecutor();
                taskExecutor.setCorePoolSize(poolSize);
                taskExecutor.setMaxPoolSize(maxPoolSize);
                taskExecutor.setQueueCapacity(queueCapacity);
                taskExecutor.initialize();
            }
            return taskExecutor;
        }
    }
}
