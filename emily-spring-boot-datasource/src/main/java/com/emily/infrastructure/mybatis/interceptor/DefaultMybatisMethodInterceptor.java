package com.emily.infrastructure.mybatis.interceptor;

import com.emily.infrastructure.core.constant.AopOrderInfo;
import com.emily.infrastructure.core.context.holder.LocalContextHolder;
import com.emily.infrastructure.core.entity.BaseLogger;
import com.emily.infrastructure.core.entity.BaseLoggerBuilder;
import com.emily.infrastructure.core.exception.PrintExceptionInfo;
import com.emily.infrastructure.core.helper.ServletHelper;
import com.emily.infrastructure.core.helper.ThreadPoolHelper;
import com.emily.infrastructure.date.DateComputeUtils;
import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.logger.LoggerFactory;
import com.emily.infrastructure.sensitive.SensitiveUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 *
 * @author Emily
 * @since 1.0
 */
public class DefaultMybatisMethodInterceptor implements MybatisCustomizer {

    private static final Logger logger = LoggerFactory.getModuleLogger(DefaultMybatisMethodInterceptor.class, "api", "request");

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //开始时间
        Instant start = Instant.now();

        BaseLoggerBuilder builder = BaseLogger.newBuilder();
        try {
            Object response = invocation.proceed();
            builder.withBody(SensitiveUtils.acquireElseGet(response));
            return response;
        } catch (Throwable ex) {
            builder.withBody(PrintExceptionInfo.printErrorInfo(ex));
            throw ex;
        } finally {
            builder.withSystemNumber(LocalContextHolder.current().getSystemNumber())
                    .withTraceId(LocalContextHolder.current().getTraceId())
                    .withClientIp(LocalContextHolder.current().getClientIp())
                    .withServerIp(LocalContextHolder.current().getServerIp())
                    .withRequestParams(ServletHelper.getMethodArgs(invocation))
                    .withUrl(MessageFormat.format("{0}.{1}", invocation.getMethod().getDeclaringClass().getCanonicalName(), invocation.getMethod().getName()))
                    .withTriggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                    .withSpentTime(DateComputeUtils.minusMillis(Instant.now(), start));
            //非servlet上下文移除数据
            LocalContextHolder.unbind();
            ThreadPoolHelper.defaultThreadPoolTaskExecutor().submit(() -> {
                logger.info(JsonUtils.toJSONString(builder.build()));
            });
        }
    }

    @Override
    public int getOrder() {
        return AopOrderInfo.MYBATIS_INTERCEPTOR;
    }
}
