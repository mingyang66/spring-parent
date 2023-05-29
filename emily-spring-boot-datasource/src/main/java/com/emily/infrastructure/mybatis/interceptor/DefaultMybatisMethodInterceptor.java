package com.emily.infrastructure.mybatis.interceptor;

import com.emily.infrastructure.core.constant.AopOrderInfo;
import com.emily.infrastructure.core.context.holder.ThreadContextHolder;
import com.emily.infrastructure.core.entity.BaseLoggerBuilder;
import com.emily.infrastructure.core.exception.PrintExceptionInfo;
import com.emily.infrastructure.core.helper.RequestHelper;
import com.emily.infrastructure.core.helper.ThreadPoolHelper;
import com.emily.infrastructure.date.DateComputeUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.logger.LoggerFactory;
import com.emily.infrastructure.sensitive.SensitiveUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Author Emily
 * @Version: 1.0
 */
public class DefaultMybatisMethodInterceptor implements MybatisCustomizer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMybatisMethodInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //开始时间
        Instant start = Instant.now();

        BaseLoggerBuilder builder = new BaseLoggerBuilder();
        try {
            Object response = invocation.proceed();
            builder.withBody(SensitiveUtils.acquire(response));
            return response;
        } catch (Throwable ex) {
            builder.withBody(PrintExceptionInfo.printErrorInfo(ex));
            throw ex;
        } finally {
            builder.withSystemNumber(ThreadContextHolder.current().getSystemNumber())
                    .withTraceId(ThreadContextHolder.current().getTraceId())
                    .withClientIp(ThreadContextHolder.current().getClientIp())
                    .withServerIp(ThreadContextHolder.current().getServerIp())
                    .withRequestParams(RequestHelper.getMethodArgs(invocation))
                    .withUrl(MessageFormat.format("{0}.{1}", invocation.getMethod().getDeclaringClass().getCanonicalName(), invocation.getMethod().getName()))
                    .withTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePatternInfo.YYYY_MM_DDTHH_MM_SS_COLON_SSS)))
                    .withSpentTime(DateComputeUtils.minusMillis(Instant.now(), start));
            //非servlet上下文移除数据
            ThreadContextHolder.unbind();
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
