package com.sgrain.boot.autoconfigure.aop.log.event;

import ch.qos.logback.classic.Level;
import com.google.common.collect.Maps;
import com.sgrain.boot.common.exception.BusinessException;
import com.sgrain.boot.common.utils.LoggerUtils;
import com.sgrain.boot.common.utils.RequestUtils;
import com.sgrain.boot.common.utils.calculation.ObjectSizeUtil;
import com.sgrain.boot.common.utils.json.JSONUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Map;

/**
 * @program: spring-parent
 * @description: 日志监听器
 * @create: 2020/08/07
 */
@Component
public class LogApplicationListener implements ApplicationListener<LogApplicationEvent> {
    @Override
    public void onApplicationEvent(LogApplicationEvent event) {
        if (!(event.getSource() instanceof LogAop)) {
            return;
        }
        LogAop logAop = (LogAop) event.getSource();
        Map<String, Object> paramsMap = RequestUtils.getParameterMap(logAop.getRequest());
        if (StringUtils.equalsIgnoreCase(logAop.getLogLevel(), Level.INFO.levelStr)) {
            logInfo(logAop, paramsMap);
        } else if (StringUtils.equalsIgnoreCase(logAop.getLogLevel(), Level.ERROR.levelStr)) {
            logError(logAop, paramsMap);
        }
    }

    /**
     * @Description 记录INFO日志
     * @Version 1.0
     */
    private void logInfo(LogAop logAop, Map<String, Object> paramsMap) {
        Map<String, Object> logMap = Maps.newLinkedHashMap();
        logMap.put("Class|Method", StringUtils.join(logAop.getInvocation().getThis().getClass(), ".", logAop.getInvocation().getMethod().getName()));
        logMap.put("Request URL", logAop.getRequest().getRequestURL());
        logMap.put("Request Method", logAop.getRequest().getMethod());
        logMap.put("Request Params", CollectionUtils.isEmpty(paramsMap) ? Collections.emptyMap() : paramsMap);
        logMap.put("Content-Type", logAop.getRequest().getContentType());
        logMap.put("Spend Time", StringUtils.join(logAop.getSpendTime(), "ms"));
        logMap.put("DataSize", ObjectSizeUtil.getObjectSizeUnit(logAop.getResult()));
        logMap.put("Response Body", logAop.getResult());
        if (LoggerUtils.isDebug()) {
            LoggerUtils.info(logAop.getInvocation().getThis().getClass(), JSONUtils.toJSONPrettyString(logMap));
        } else {
            LoggerUtils.info(logAop.getInvocation().getThis().getClass(), JSONUtils.toJSONString(logMap));
        }
    }

    /**
     * @Description 异常日志
     * @Version 1.0
     */
    private void logError(LogAop logAop, Map<String, Object> paramsMap) {
        Map<String, Object> errorLogMap = Maps.newLinkedHashMap();
        errorLogMap.put("Class|Method", StringUtils.join(logAop.getInvocation().getThis().getClass(), ".", logAop.getInvocation().getMethod().getName()));
        errorLogMap.put("Request URL", logAop.getRequest().getRequestURL());
        errorLogMap.put("Request Method", logAop.getRequest().getMethod());
        errorLogMap.put("Reuqest Params", CollectionUtils.isEmpty(paramsMap) ? Collections.emptyMap() : paramsMap);
        errorLogMap.put("Content-Type", logAop.getRequest().getContentType());
        errorLogMap.put("Spend Time", StringUtils.join(logAop.getSpendTime(), "ms"));

        if (logAop.getThrowable() instanceof BusinessException) {
            BusinessException exception = (BusinessException) logAop.getThrowable();
            errorLogMap.put("Exception", StringUtils.join(logAop.getThrowable(), " 【statusCode】", exception.getStatus(), ", 【errorMessage】", exception.getErrorMessage()));
        } else {
            errorLogMap.put("Exception", StringUtils.join(logAop.getThrowable().getStackTrace()[0], " ", logAop.getThrowable()));
        }
        if (LoggerUtils.isDebug()) {
            LoggerUtils.error(logAop.getInvocation().getThis().getClass(), JSONUtils.toJSONPrettyString(errorLogMap));
        } else {
            LoggerUtils.error(logAop.getInvocation().getThis().getClass(), JSONUtils.toJSONString(errorLogMap));
        }
    }
}
