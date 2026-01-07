package com.emily.infrastructure.mybatis.interceptor;

import com.emily.infrastructure.aop.constant.AopOrderInfo;
import com.emily.infrastructure.common.PrintExceptionUtils;
import com.emily.infrastructure.date.DateComputeUtils;
import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import com.emily.infrastructure.logback.entity.BaseLogger;
import com.emily.infrastructure.logger.event.LogEventType;
import com.emily.infrastructure.logger.event.LogPrintApplicationEvent;
import com.emily.infrastructure.mybatis.helper.MethodHelper;
import com.emily.infrastructure.tracing.holder.LocalContextHolder;
import jakarta.annotation.Nonnull;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;

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
    private final ApplicationContext context;

    public DefaultMybatisMethodInterceptor(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        //开始时间
        Instant start = Instant.now();
        //获取请求参数--请求入参必须在调用方法之前获取，避免被拦截器修改
        Map<String, Object> params = MethodHelper.getMethodArgs(invocation);
        //方法调用结果
        Object result = null;
        try {
            Object response = invocation.proceed();
            //获取脱敏后的数据结果
            result = MethodHelper.getResult(response);
            return response;
        } catch (Throwable ex) {
            result = PrintExceptionUtils.printErrorInfo(ex);
            throw ex;
        } finally {
            BaseLogger baseLogger = new BaseLogger()
                    .systemNumber(LocalContextHolder.current().getSystemNumber())
                    .traceId(LocalContextHolder.current().getTraceId())
                    .clientIp(LocalContextHolder.current().getClientIp())
                    .serverIp(LocalContextHolder.current().getServerIp())
                    .requestParams(params)
                    .body(result)
                    .url(MessageFormat.format("{0}.{1}", invocation.getMethod().getDeclaringClass().getCanonicalName(), invocation.getMethod().getName()))
                    .triggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                    .spentTime(DateComputeUtils.minusMillis(Instant.now(), start));
            //打印日志
            context.publishEvent(new LogPrintApplicationEvent(LogEventType.THIRD_PARTY, baseLogger));
            //非servlet上下文移除数据
            LocalContextHolder.unbind();
        }
    }

    @Override
    public int getOrder() {
        return AopOrderInfo.MYBATIS + 1;
    }
}
