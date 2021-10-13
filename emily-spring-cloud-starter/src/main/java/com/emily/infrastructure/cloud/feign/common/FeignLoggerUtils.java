package com.emily.infrastructure.cloud.feign.common;

import com.emily.infrastructure.common.utils.RequestUtils;
import com.emily.infrastructure.core.entity.BaseLogger;

import java.util.Objects;

/**
 * @author Emily
 * @program: spring-parent
 * @description: feigin日志记录工具类
 * @create: 2021/04/01
 */
public class FeignLoggerUtils {
    /**
     * 获取日志记录对象
     */
    public static BaseLogger getBaseLogger() {
        //封装异步日志信息
        BaseLogger baseLogger;
        Object feignLog = RequestUtils.isServletContext() ? RequestUtils.getRequest().getAttribute("feignLog") : null;
        if (Objects.isNull(feignLog)) {
            baseLogger = new BaseLogger();
        } else {
            baseLogger = (BaseLogger) feignLog;
        }
        return baseLogger;
    }
}
