package com.emily.infrastructure.cloud.feign.interceptor;

import com.emily.infrastructure.cloud.feign.common.FeignLoggerUtils;
import com.emily.infrastructure.common.base.BaseLogger;
import com.emily.infrastructure.common.enums.DateFormatEnum;
import com.emily.infrastructure.common.exception.BusinessException;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.RequestUtils;
import com.emily.infrastructure.context.logger.LoggerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.ThrowsAdvice;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Emily
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Version: 1.0
 */
public class FeignLoggerThrowsAdvice implements ThrowsAdvice {

    private LoggerService loggerService;

    public FeignLoggerThrowsAdvice(LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    public void afterThrowing(Method method, Object[] args, Object target, Exception e) {
        //封装异步日志信息
        BaseLogger baseLogger = FeignLoggerUtils.getBaseLogger();
        //耗时
        baseLogger.setTime(System.currentTimeMillis() - Long.valueOf(RequestUtils.getRequest().getAttribute("start").toString()));
        //触发时间
        baseLogger.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
        if (e instanceof BusinessException) {
            BusinessException exception = (BusinessException) e;
            baseLogger.setResponseBody(StringUtils.join(e, " 【statusCode】", exception.getStatus(), ", 【errorMessage】", exception.getMessage()));
        } else {
            baseLogger.setResponseBody(PrintExceptionInfo.printErrorInfo(e));
        }
        //记录异常日志
        loggerService.traceResponse(baseLogger);
    }


}
