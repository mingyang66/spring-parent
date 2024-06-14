package com.emily.infrastructure.cloud.feign.logger;

import com.emily.infrastructure.logback.factory.LoggerFactory;
import feign.Logger;

/**
 * 自定义Feign日志
 *
 * @author Emily
 * @since 2021/04/10
 */
public class FeignLogger extends Logger {
    /**
     * 记录Feign调试日志
     *
     * @param configKey FeignClient 类名#方法名
     * @param format    日志格式化字符串 如：%s%s
     * @param args      格式化参数
     */
    @Override
    protected void log(String configKey, String format, Object... args) {
        LoggerFactory.getModuleLogger(FeignLogger.class, "/feign/", "feign").info(String.format(methodTag(configKey) + format, args));
    }
}
