package com.sgrain.boot.autoconfigure.aop.interceptor;

import com.google.common.collect.Maps;
import com.sgrain.boot.common.exception.BusinessException;
import com.sgrain.boot.common.utils.LoggerUtils;
import com.sgrain.boot.common.utils.calculation.ObjectSizeUtil;
import com.sgrain.boot.common.utils.RequestUtils;
import com.sgrain.boot.common.utils.json.JSONUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
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

    /**
     * 拦截接口日志
     * @param invocation 接口方法切面连接点
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //获取请求参数，且该参数获取必须在proceed之前
        Map<String, Object> paramsMap = RequestUtils.getRequestParamMap(invocation);
        //新建计时器并开始计时
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            //调用升级的action方法
            Object result = invocation.proceed();
            //暂停计时
            stopWatch.stop();
            //耗时
            long spentTime = (stopWatch.getTime() == 0) ? 1 : stopWatch.getTime();
            if (ObjectUtils.isNotEmpty(result) && (result instanceof ResponseEntity)) {
                Object resultBody = ((ResponseEntity) result).getBody();
                //打印INFO日志
                logInfo(invocation, paramsMap, resultBody, spentTime);
            } else {
                //打印INFO日志
                logInfo(invocation, paramsMap, result, spentTime);
            }
            return result;
        } catch (Throwable e) {
            //暂停计时
            if (stopWatch.isStarted() || stopWatch.isSuspended()) {
                stopWatch.stop();
            }
            //耗时
            long spentTime = (stopWatch.getTime() == 0) ? 1 : stopWatch.getTime();
            //打印ERROR日志
            logError(invocation, paramsMap, spentTime, e);
            throw e;
        }
    }

    /**
     * @Description 记录INFO日志
     * @Version 1.0
     */
    private void logInfo(MethodInvocation invocation, Map<String, Object> paramsMap, Object result, long spentTime) {
        HttpServletRequest request = RequestUtils.getRequest();
        Map<String, Object> logMap = Maps.newLinkedHashMap();
        logMap.put("Class|Method", StringUtils.join(invocation.getThis().getClass(), ".", invocation.getMethod().getName()));
        logMap.put("Request Url", request.getRequestURL());
        logMap.put("Request Method", request.getMethod());
        logMap.put("Request Params", CollectionUtils.isEmpty(paramsMap) ? Collections.emptyMap() : paramsMap);
        logMap.put("Spend Time", StringUtils.join(spentTime, "ms"));
        logMap.put("DataSize", ObjectSizeUtil.getObjectSizeUnit(result));
        logMap.put("Response body", result);
        if (LoggerUtils.isDebug()) {
            LoggerUtils.info(invocation.getThis().getClass(), JSONUtils.toJSONPrettyString(logMap));
        } else {
            LoggerUtils.info(invocation.getThis().getClass(), JSONUtils.toJSONString(logMap));
        }
    }

    /**
     * @Description 异常日志
     * @Version 1.0
     */
    private void logError(MethodInvocation invocation, Map<String, Object> paramsMap, long spentTime, Throwable e) {
        HttpServletRequest request = RequestUtils.getRequest();
        Map<String, Object> errorLogMap = Maps.newLinkedHashMap();
        errorLogMap.put("Class|Method", StringUtils.join(invocation.getThis().getClass(), ".", invocation.getMethod().getName()));
        errorLogMap.put("Request Url", request.getRequestURL());
        errorLogMap.put("Request Method", request.getMethod());
        errorLogMap.put("Reuqest Params", CollectionUtils.isEmpty(paramsMap) ? Collections.emptyMap() : paramsMap);
        errorLogMap.put("Spend Time", StringUtils.join(spentTime, "ms"));

        if (e instanceof BusinessException) {
            BusinessException exception = (BusinessException) e;
            errorLogMap.put("Exception", StringUtils.join(e, " 【statusCode】", exception.getStatus(), ", 【errorMessage】", exception.getErrorMessage()));
        } else {
            errorLogMap.put("Exception", StringUtils.join(e.getStackTrace()[0], " ", e));
        }
        if (LoggerUtils.isDebug()) {
            LoggerUtils.error(invocation.getThis().getClass(), JSONUtils.toJSONPrettyString(errorLogMap));
        } else {
            LoggerUtils.error(invocation.getThis().getClass(), JSONUtils.toJSONString(errorLogMap));
        }

    }
}
