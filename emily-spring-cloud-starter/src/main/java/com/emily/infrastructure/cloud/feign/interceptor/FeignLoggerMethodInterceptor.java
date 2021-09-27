package com.emily.infrastructure.cloud.feign.interceptor;

import com.emily.infrastructure.cloud.feign.context.FeignContextHolder;
import com.emily.infrastructure.common.base.BaseLogger;
import com.emily.infrastructure.common.enums.DateFormatEnum;
import com.emily.infrastructure.common.exception.BusinessException;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.core.helper.ThreadPoolHelper;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author Emily
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Version: 1.0
 */
public class FeignLoggerMethodInterceptor implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(FeignLoggerMethodInterceptor.class);

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
        // 响应结果
        Object responseBody = null;
        try {
            //调用真实的action方法
            Object result = invocation.proceed();
            //响应结果
            responseBody = result;
            return result;
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                BusinessException exception = (BusinessException) e;
                responseBody = StringUtils.join(e, " 【statusCode】", exception.getStatus(), ", 【errorMessage】", exception.getMessage());
            } else {
                responseBody = PrintExceptionInfo.printErrorInfo(e);
            }
            throw e;
        } finally {
            //封装异步日志信息
            BaseLogger baseLogger = FeignContextHolder.get();
            //删除线程上下文中的数据，防止内存溢出
            Optional.ofNullable(baseLogger).ifPresent(logger -> FeignContextHolder.remove());
            //耗时
            baseLogger.setTime(System.currentTimeMillis() - start);
            //触发时间
            baseLogger.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
            //响应结果
            baseLogger.setResponseBody(responseBody);
            //异步记录接口响应信息
            ThreadPoolHelper.threadPoolTaskExecutor().submit(() -> logger.info(JSONUtils.toJSONString(baseLogger)));
        }
    }

}
