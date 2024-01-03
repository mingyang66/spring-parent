package com.emily.infrastructure.cloud.feign.interceptor;

import com.emily.infrastructure.cloud.feign.context.FeignContextHolder;
import com.emily.infrastructure.core.constant.AopOrderInfo;
import com.emily.infrastructure.core.constant.AttributeInfo;
import com.emily.infrastructure.core.context.holder.LocalContextHolder;
import com.emily.infrastructure.core.entity.BaseLogger;
import com.emily.infrastructure.core.exception.BasicException;
import com.emily.infrastructure.core.exception.PrintExceptionInfo;
import com.emily.infrastructure.core.helper.PrintLoggerUtils;
import com.emily.infrastructure.core.helper.ServletHelper;
import com.emily.infrastructure.date.DateComputeUtils;
import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import com.emily.infrastructure.sensitive.SensitiveUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 *
 * @author Emily
 * @since 1.0
 */
public class DefaultFeignMethodInterceptor implements FeignCustomizer {

    /**
     * 拦截接口日志
     *
     * @param invocation 接口方法切面连接点
     * @return 接口返回值
     * @throws Throwable 异常
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 开始时间
        Instant start = Instant.now();
        // 响应结果
        Object response = null;
        try {
            //调用真实的action方法
            response = invocation.proceed();
            return response;
        } catch (Exception e) {
            if (e instanceof BasicException) {
                BasicException exception = (BasicException) e;
                response = StringUtils.join(e, " 【statusCode】", exception.getStatus(), ", 【errorMessage】", exception.getMessage());
            } else {
                response = PrintExceptionInfo.printErrorInfo(e);
            }
            throw e;
        } finally {
            //封装异步日志信息
            BaseLogger baseLogger = FeignContextHolder.current()
                    //客户端IP
                    .withClientIp(LocalContextHolder.current().getClientIp())
                    //服务端IP
                    .withServerIp(LocalContextHolder.current().getServerIp())
                    //版本类型
                    .withAppType(LocalContextHolder.current().getAppType())
                    //版本号
                    .withAppVersion(LocalContextHolder.current().getAppVersion())
                    //耗时
                    .withSpentTime(DateComputeUtils.minusMillis(Instant.now(), start))
                    //触发时间
                    .withTriggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                    //响应结果
                    .withBody(SensitiveUtils.acquireElseGet(response))
                    //请求参数
                    .withRequestParams(AttributeInfo.PARAMS, ServletHelper.getMethodArgs(invocation))
                    .build();
            //异步记录接口响应信息
            PrintLoggerUtils.printThirdParty(baseLogger);
            //删除线程上下文中的数据，防止内存溢出
            FeignContextHolder.unbind();
            //非servlet上下文移除数据
            LocalContextHolder.unbind();
        }
    }

    @Override
    public int getOrder() {
        return AopOrderInfo.FEIGN_INTERCEPTOR;
    }
}
