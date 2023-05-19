package com.emily.infrastructure.mybatis.interceptor;

import com.emily.infrastructure.common.constant.AopOrderInfo;
import com.emily.infrastructure.core.entity.BaseLoggerBuilder;
import com.emily.infrastructure.core.exception.PrintExceptionInfo;
import com.emily.infrastructure.core.context.holder.ThreadContextHolder;
import com.emily.infrastructure.core.helper.RequestHelper;
import com.emily.infrastructure.core.helper.ThreadPoolHelper;
import com.emily.infrastructure.date.DatePatternType;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.logger.LoggerFactory;
import com.emily.infrastructure.sensitive.SensitiveUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;

import java.text.MessageFormat;
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
        long start = System.currentTimeMillis();

        BaseLoggerBuilder builder = new BaseLoggerBuilder();
        try {
            Object response = invocation.proceed();
            builder.body(SensitiveUtils.acquire(response));
            return response;
        } catch (Throwable ex) {
            builder.body(PrintExceptionInfo.printErrorInfo(ex));
            throw ex;
        } finally {
            builder.systemNumber(ThreadContextHolder.current().getSystemNumber())
                    .traceId(ThreadContextHolder.current().getTraceId())
                    .clientIp(ThreadContextHolder.current().getClientIp())
                    .serverIp(ThreadContextHolder.current().getServerIp())
                    .requestParams(RequestHelper.getMethodArgs(invocation))
                    .url(MessageFormat.format("{0}.{1}", invocation.getMethod().getDeclaringClass().getCanonicalName(), invocation.getMethod().getName()))
                    .triggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePatternType.YYYY_MM_DDTHH_MM_SS_COLON_SSS.getPattern())))
                    .spentTime(System.currentTimeMillis() - start);
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
