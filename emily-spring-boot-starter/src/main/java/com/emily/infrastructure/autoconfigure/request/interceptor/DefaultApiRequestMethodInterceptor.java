package com.emily.infrastructure.autoconfigure.request.interceptor;

import com.emily.infrastructure.common.constant.AopOrderInfo;
import com.emily.infrastructure.common.constant.AttributeInfo;
import com.emily.infrastructure.common.constant.CharacterInfo;
import com.emily.infrastructure.common.enums.DateFormat;
import com.emily.infrastructure.common.exception.BasicException;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.RequestUtils;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.core.entity.BaseLogger;
import com.emily.infrastructure.core.helper.RequestHelper;
import com.emily.infrastructure.core.helper.ThreadPoolHelper;
import com.emily.infrastructure.core.context.holder.ContextHolder;
import com.emily.infrastructure.logger.LoggerFactory;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Emily
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Version: 1.0
 */
public class DefaultApiRequestMethodInterceptor implements ApiRequestCustomizer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultApiRequestMethodInterceptor.class);

    /**
     * 拦截接口日志
     *
     * @param invocation 接口方法切面连接点
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //封装异步日志信息
        BaseLogger baseLogger = new BaseLogger();
        try {
            //系统编号
            baseLogger.setSystemNumber(ContextHolder.get().getSystemNumber());
            //事务唯一编号
            baseLogger.setTraceId(ContextHolder.get().getTraceId());
            //时间
            baseLogger.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormat.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
            //请求url
            baseLogger.setUrl(StringUtils.substringBefore(String.valueOf(RequestUtils.getRequest().getRequestURL()), CharacterInfo.ASK_SIGN_EN));
            //请求参数
            baseLogger.setRequestParams(RequestHelper.getApiParamsMap());
            //调用真实的action方法
            Object response = invocation.proceed();
            //设置响应结果
            baseLogger.setBody(response);
            return response;
        } catch (Exception ex) {
            if (ex instanceof BasicException) {
                BasicException exception = (BasicException) ex;
                baseLogger.setBody(StringUtils.join(ex, " 【statusCode】", exception.getStatus(), ", 【errorMessage】", exception.getMessage()));
            } else {
                baseLogger.setBody(PrintExceptionInfo.printErrorInfo(ex));
            }
            throw ex;
        } finally {
            //客户端IP
            baseLogger.setClientIp(ContextHolder.get().getClientIp());
            //服务端IP
            baseLogger.setServerIp(ContextHolder.get().getServerIp());
            //耗时
            baseLogger.setTime(System.currentTimeMillis() - ContextHolder.get().getStartTime());
            //时间
            baseLogger.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormat.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
            //异步记录接口响应信息
            ThreadPoolHelper.threadPoolTaskExecutor().submit(() -> logger.info(JSONUtils.toJSONString(baseLogger)));
            //移除线程上下文数据
            ContextHolder.remove();
            //设置耗时
            RequestUtils.getRequest().setAttribute(AttributeInfo.TIME, baseLogger.getTime());
        }

    }

    @Override
    public int getOrder() {
        return AopOrderInfo.REQUEST_INTERCEPTOR;
    }
}
