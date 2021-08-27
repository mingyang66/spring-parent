package com.emily.infrastructure.autoconfigure.request.interceptor;

import com.emily.infrastructure.autoconfigure.request.helper.RequestHelper;
import com.emily.infrastructure.common.base.BaseLogger;
import com.emily.infrastructure.common.enums.DateFormatEnum;
import com.emily.infrastructure.common.utils.RequestUtils;
import com.emily.infrastructure.context.logger.LoggerService;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Version: 1.0
 */
public class RequestLoggerMethodInterceptor implements MethodInterceptor {

    private LoggerService loggerService;

    public RequestLoggerMethodInterceptor(LoggerService loggerService) {
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
        //获取HttpServletRequest对象
        HttpServletRequest request = RequestUtils.getRequest();
        //封装异步日志信息
        BaseLogger baseLogger = new BaseLogger();
        //事务唯一编号
        baseLogger.setTraceId(RequestUtils.getTraceId());
        //时间
        baseLogger.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
        //控制器Class
        baseLogger.setClazz(invocation.getThis().getClass());
        //控制器方法名
        baseLogger.setMethod(invocation.getMethod().getName());
        //请求url
        baseLogger.setRequestUrl(request.getRequestURL().toString());
        //请求方法
        baseLogger.setMethod(request.getMethod());
        //请求参数
        baseLogger.setRequestParams(RequestHelper.getParameterMap(request));

        //新建计时器并开始计时
        RequestUtils.startRequest();
        //调用真实的action方法
        Object result = invocation.proceed();

        //耗时
        baseLogger.setTime(RequestUtils.getTime());
        //响应结果
        baseLogger.setResponseBody(result);
        //时间
        baseLogger.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
        //异步记录接口响应信息
        loggerService.traceResponse(baseLogger);
        return result;

    }

}
