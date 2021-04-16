package com.emily.framework.cloud.feign.logger;

import com.emily.framework.common.logger.LoggerUtils;
import feign.Logger;

/**
 * @program: spring-parent
 * @description: 自定义Feign日志
 * @create: 2021/04/10
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
        LoggerUtils.module(FeignLogger.class, "/feign/", "feign", String.format(methodTag(configKey) + format, args));
    }
}
