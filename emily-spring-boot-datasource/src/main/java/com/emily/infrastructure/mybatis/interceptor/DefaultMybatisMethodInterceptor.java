package com.emily.infrastructure.mybatis.interceptor;

import com.emily.infrastructure.core.constant.AopOrderInfo;
import com.emily.infrastructure.core.context.holder.LocalContextHolder;
import com.emily.infrastructure.core.entity.BaseLogger;
import com.emily.infrastructure.common.PrintExceptionUtils;
import com.emily.infrastructure.core.utils.PrintLoggerUtils;
import com.emily.infrastructure.core.helper.ServletHelper;
import com.emily.infrastructure.date.DateComputeUtils;
import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import com.emily.infrastructure.sensitive.SensitiveUtils;
import org.aopalliance.intercept.MethodInvocation;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 *
 * @author Emily
 * @since 1.0
 */
public class DefaultMybatisMethodInterceptor implements MybatisCustomizer {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //开始时间
        Instant start = Instant.now();
        //获取请求参数--请求入参必须在调用方法之前获取，避免被拦截器修改
        Map<String, Object> params = ServletHelper.getMethodArgs(invocation);
        //方法调用结果
        Object result = null;
        try {
            Object response = invocation.proceed();
            //获取脱敏后的数据结果
            result = SensitiveUtils.acquireElseGet(response);
            return response;
        } catch (Throwable ex) {
            result = PrintExceptionUtils.printErrorInfo(ex);
            throw ex;
        } finally {
            BaseLogger baseLogger = BaseLogger.newBuilder()
                    .withSystemNumber(LocalContextHolder.current().getSystemNumber())
                    .withTraceId(LocalContextHolder.current().getTraceId())
                    .withClientIp(LocalContextHolder.current().getClientIp())
                    .withServerIp(LocalContextHolder.current().getServerIp())
                    .withRequestParams(params)
                    .withBody(result)
                    .withUrl(MessageFormat.format("{0}.{1}", invocation.getMethod().getDeclaringClass().getCanonicalName(), invocation.getMethod().getName()))
                    .withTriggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                    .withSpentTime(DateComputeUtils.minusMillis(Instant.now(), start))
                    .build();
            //打印日志
            PrintLoggerUtils.printThirdParty(baseLogger);
            //非servlet上下文移除数据
            LocalContextHolder.unbind();
        }
    }

    @Override
    public int getOrder() {
        return AopOrderInfo.MYBATIS_INTERCEPTOR;
    }
}
