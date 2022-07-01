package com.emily.infrastructure.mybatis.interceptor;

import com.emily.infrastructure.common.constant.AopOrderInfo;
import com.emily.infrastructure.common.enums.DateFormat;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.core.context.holder.ContextHolder;
import com.emily.infrastructure.core.entity.BaseLogger;
import com.emily.infrastructure.core.helper.RequestHelper;
import com.emily.infrastructure.core.helper.ThreadPoolHelper;
import com.emily.infrastructure.logger.LoggerFactory;
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

        BaseLogger baseLogger = new BaseLogger();
        try {
            Object response = invocation.proceed();
            baseLogger.setBody(response);
            return response;
        } catch (Throwable ex) {
            baseLogger.setBody(PrintExceptionInfo.printErrorInfo(ex));
            throw ex;
        } finally {
            baseLogger.setSystemNumber(ContextHolder.get().getSystemNumber());
            baseLogger.setTraceId(ContextHolder.get().getTraceId());
            baseLogger.setClientIp(ContextHolder.get().getClientIp());
            baseLogger.setServerIp(ContextHolder.get().getServerIp());
            baseLogger.setRequestParams(RequestHelper.getMethodArgs(invocation));
            baseLogger.setUrl(MessageFormat.format("{0}.{1}", invocation.getMethod().getDeclaringClass().getCanonicalName(), invocation.getMethod().getName()));
            baseLogger.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormat.YYYY_MM_DDTHH_MM_SS_COLON_SSS.getFormat())));
            baseLogger.setTime(System.currentTimeMillis() - start);
            //非servlet上下文移除数据
            ContextHolder.remove();
            ThreadPoolHelper.threadPoolTaskExecutor().submit(() -> {
                logger.info(JSONUtils.toJSONString(baseLogger));
            });
        }
    }

    @Override
    public int getOrder() {
        return AopOrderInfo.MYBATIS_INTERCEPTOR;
    }
}
