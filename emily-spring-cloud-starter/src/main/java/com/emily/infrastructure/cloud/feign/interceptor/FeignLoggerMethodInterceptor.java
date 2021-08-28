package com.emily.infrastructure.cloud.feign.interceptor;

import com.emily.infrastructure.cloud.feign.common.FeignLoggerUtils;
import com.emily.infrastructure.common.base.BaseLogger;
import com.emily.infrastructure.common.enums.DateFormatEnum;
import com.emily.infrastructure.common.exception.BusinessException;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.context.logger.LoggerService;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Emily
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Version: 1.0
 */
public class FeignLoggerMethodInterceptor implements MethodInterceptor {

    private LoggerService loggerService;

    public FeignLoggerMethodInterceptor(LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    /**
     * 拦截接口日志
     *
     * @param invocation 接口方法切面连接点
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 开始时间
        long start = System.currentTimeMillis();
        //封装异步日志信息
        BaseLogger baseLogger = FeignLoggerUtils.getBaseLogger();
        try {
            //调用真实的action方法
            Object result = invocation.proceed();
            //响应结果
            baseLogger.setResponseBody(result);
            return result;
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                BusinessException exception = (BusinessException) e;
                baseLogger.setResponseBody(StringUtils.join(e, " 【statusCode】", exception.getStatus(), ", 【errorMessage】", exception.getMessage()));
            } else {
                baseLogger.setResponseBody(PrintExceptionInfo.printErrorInfo(e));
            }
            throw e;
        } finally {
            //耗时
            baseLogger.setTime(System.currentTimeMillis() - start);
            //触发时间
            baseLogger.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
            //异步记录接口响应信息
            loggerService.traceResponse(baseLogger);
        }

    }

}
