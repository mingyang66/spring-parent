package com.sgrain.boot.autoconfigure.httpclient.interceptor;

import com.google.common.collect.Maps;
import com.sgrain.boot.autoconfigure.aop.log.event.LogAop;
import com.sgrain.boot.common.exception.BusinessException;
import com.sgrain.boot.common.utils.LoggerUtils;
import com.sgrain.boot.common.utils.calculation.ObjectSizeUtil;
import com.sgrain.boot.common.utils.constant.CharsetUtils;
import com.sgrain.boot.common.utils.json.JSONUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * @program: spring-parent
 * @description: RestTemplate拦截器
 * @create: 2020/08/17
 */
public class HttpClientInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        logInfo(request, body);
        return response;
    }
    /**
     * @Description 记录INFO日志
     * @Version 1.0
     */
    private void logInfo(HttpRequest request, byte[] body) throws IOException{
        Map<String, Object> logMap = Maps.newLinkedHashMap();
        logMap.put("Class|Method", null);
        logMap.put("Request URL", request.getURI());
        logMap.put("Request Method", request.getMethod());
        logMap.put("Request Body", new String(body, CharsetUtils.UTF_8));
        logMap.put("Request Params", null);
        logMap.put("Spend Time", null);
        logMap.put("DataSize", null);
        logMap.put("Response Body", null);
        if (LoggerUtils.isDebug()) {
            //LoggerUtils.info(logAop.getInvocation().getThis().getClass(), JSONUtils.toJSONPrettyString(logMap));
        } else {
            //LoggerUtils.info(logAop.getInvocation().getThis().getClass(), JSONUtils.toJSONString(logMap));
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
