package com.sgrain.boot.autoconfigure.aop.interceptor;

import ch.qos.logback.classic.Level;
import com.google.common.collect.Maps;
import com.sgrain.boot.autoconfigure.aop.log.event.LogAop;
import com.sgrain.boot.autoconfigure.aop.log.event.LogApplicationEvent;
import com.sgrain.boot.common.exception.BusinessException;
import com.sgrain.boot.common.utils.RequestUtils;
import com.sgrain.boot.common.utils.calculation.ObjectSizeUtil;
import com.sgrain.boot.common.utils.constant.CharacterUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

/**
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Version: 1.0
 */
public class LogAopMethodInterceptor implements MethodInterceptor {

    private ApplicationEventPublisher publisher;

    public LogAopMethodInterceptor(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
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
        HttpServletRequest request = RequestUtils.getRequest();
        Map<String, Object> paramsMap = RequestUtils.getParameterMap(request);
        //新建计时器并开始计时
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            //调用真实的action方法
            Object result = invocation.proceed();
            //暂停计时
            stopWatch.stop();
            //发布事件
            publisher.publishEvent(new LogApplicationEvent(traceInfo(invocation, request, paramsMap, result, stopWatch.getTime())));
            return result;
        } catch (Throwable e) {
            //暂停计时
            if (stopWatch.isStarted() || stopWatch.isSuspended()) {
                stopWatch.stop();
            }
            //发布事件
            publisher.publishEvent(new LogApplicationEvent(traceError(invocation, request, paramsMap, stopWatch.getTime(), e)));

            throw e;
        }
    }

    /**
     * @Description 记录INFO日志
     * @Version 1.0
     */
    private LogAop traceInfo(MethodInvocation invocation, HttpServletRequest request, Map<String, Object> paramsMap, Object result, long spentTime) {
        Object resultBody = result;
        if (ObjectUtils.isNotEmpty(result) && (result instanceof ResponseEntity)) {
            resultBody = ((ResponseEntity) result).getBody();
        }
        Map<String, Object> logMap = Maps.newLinkedHashMap();
        logMap.put("Class|Method", StringUtils.join(invocation.getThis().getClass(), CharacterUtils.POINT_SYMBOL, invocation.getMethod().getName()));
        logMap.put("Request URL", request.getRequestURL());
        logMap.put("Request Method", request.getMethod());
        logMap.put("Request Params", CollectionUtils.isEmpty(paramsMap) ? Collections.emptyMap() : paramsMap);
        logMap.put("Content-Type", request.getContentType());
        logMap.put("Spend Time", StringUtils.join((spentTime == 0) ? 1 : spentTime, "ms"));
        logMap.put("DataSize", ObjectSizeUtil.getObjectSizeUnit(resultBody));
        logMap.put("Response Body", resultBody);
        return new LogAop(Level.INFO.levelStr, invocation.getThis().getClass(), logMap);
    }

    /**
     * @Description 异常日志
     * @Version 1.0
     */
    private LogAop traceError(MethodInvocation invocation, HttpServletRequest request, Map<String, Object> paramsMap, long spentTime, Throwable e) {
        Map<String, Object> logMap = Maps.newLinkedHashMap();
        logMap.put("Class|Method", StringUtils.join(invocation.getThis().getClass(), CharacterUtils.POINT_SYMBOL, invocation.getMethod().getName()));
        logMap.put("Request URL", request.getRequestURL());
        logMap.put("Request Method", request.getMethod());
        logMap.put("Reuqest Params", CollectionUtils.isEmpty(paramsMap) ? Collections.emptyMap() : paramsMap);
        logMap.put("Content-Type", request.getContentType());
        logMap.put("Spend Time", StringUtils.join((spentTime == 0) ? 1 : spentTime, "ms"));

        if (e instanceof BusinessException) {
            BusinessException exception = (BusinessException) e;
            logMap.put("Exception", StringUtils.join(e, " 【statusCode】", exception.getStatus(), ", 【errorMessage】", exception.getErrorMessage()));
        } else {
            logMap.put("Exception", StringUtils.join(e.getStackTrace()[0], " ", e));
        }
        return new LogAop(Level.ERROR.levelStr, invocation.getThis().getClass(), logMap);
    }
}
