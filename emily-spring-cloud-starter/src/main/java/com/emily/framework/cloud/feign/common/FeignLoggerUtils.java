package com.emily.framework.cloud.feign.common;

import com.emily.framework.common.base.BaseLogger;
import com.emily.framework.common.utils.RequestUtils;

import java.util.Objects;

/**
 * @program: spring-parent
 * @description: feigin日志记录工具类
 * @create: 2021/04/01
 */
public class FeignLoggerUtils {
    /**
     * 获取日志记录对象
     */
    public static BaseLogger getBaseLogger(){
        //封装异步日志信息
        BaseLogger baseLogger;
        Object feignLog = RequestUtils.getRequest().getAttribute("feignLog");
        if (Objects.isNull(feignLog)) {
            baseLogger = new BaseLogger();
        } else {
            baseLogger = (BaseLogger) feignLog;
        }
        return baseLogger;
    }
}
