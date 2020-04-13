package com.sgrain.boot.autoconfigure.aop.interceptor;

import com.google.common.collect.Maps;
import com.sgrain.boot.common.utils.LoggerUtils;
import com.sgrain.boot.common.utils.ObjectSizeUtil;
import com.sgrain.boot.common.utils.RequestUtils;
import com.sgrain.boot.common.utils.json.JSONUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.core.env.Environment;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Version: 1.0
 */
public class LogAopMethodInterceptor implements MethodInterceptor {

    private Environment environment;
    public LogAopMethodInterceptor(Environment environment){
        this.environment = environment;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        HttpServletRequest request = RequestUtils.getRequest();
        //获取请求参数，且该参数获取必须在proceed之前
        Map<String, Object> paramsMap = RequestUtils.getRequestParam(request, invocation);
        //新建计时器并开始计时
        StopWatch stopWatch = StopWatch.createStarted();
        try{
            //调用升级的action方法
            Object result = invocation.proceed();
            //暂停计时
            stopWatch.stop();
            //耗时
            long spentTime = (stopWatch.getTime() == 0) ? 1 : stopWatch.getTime();
            //打印INFO日志
            logInfo(invocation, request, paramsMap, result, spentTime);
            return result;
        } catch (Throwable e){
            //暂停计时
            if(stopWatch.isStarted() || stopWatch.isSuspended()){
                stopWatch.stop();
            }
            //耗时
            long spentTime = (stopWatch.getTime() == 0) ? 1 : stopWatch.getTime();
            //打印ERROR日志
            logError(invocation, request, paramsMap, spentTime, e);
            throw e;
        }
    }
    /**
     * @Description 记录INFO日志
     * @Version  1.0
     */
    private void logInfo(MethodInvocation invocation, HttpServletRequest request, Map<String, Object> paramsMap, Object result, long spentTime){
        Map<String, Object> logMap = Maps.newLinkedHashMap();
        logMap.put("Class：", invocation.getThis().getClass());
        logMap.put("Request Url：", request.getRequestURL());
        logMap.put("Request Method：", request.getMethod());
        logMap.put("Params：", paramsMap);
        logMap.put("Spend Time：", StringUtils.join(spentTime, "ms"));
        logMap.put("Data Size：", ObjectSizeUtil.getObjectSizeUnit(result));
        logMap.put("Response Data：", result);
        if(LoggerUtils.isDebug()){
            LoggerUtils.info(invocation.getThis().getClass(), JSONUtils.toJSONPrettyString(logMap));
        } else {
            LoggerUtils.info(invocation.getThis().getClass(), JSONUtils.toJSONString(logMap));
        }
    }
    /**
     * @Description 异常日志
     * @Version  1.0
     */
    private void logError(MethodInvocation invocation, HttpServletRequest request, Map<String, Object> paramsMap, long spentTime, Throwable e){
        Map<String, Object> logMap = Maps.newLinkedHashMap();
        logMap.put("Class: ", invocation.getThis().getClass());
        logMap.put("Request Url：", request.getRequestURL());
        logMap.put("Request Method：", request.getMethod());
        logMap.put("Params：", paramsMap);
        logMap.put("Spend Time：", StringUtils.join(spentTime, "ms"));
        StackTraceElement[] elements = e.getStackTrace();
        String errorMsg;
        if(elements.length > 0){
            StackTraceElement element = elements[0];
            errorMsg = StringUtils.join(element.getClassName(), ".", element.getMethodName(), "的第", element.getLineNumber(), "行发生", e.toString(), "异常");
        } else {
            errorMsg = e.toString();
        }
        logMap.put("Exception：", errorMsg);
        if(LoggerUtils.isDebug()){
            LoggerUtils.error(invocation.getThis().getClass(), JSONUtils.toJSONPrettyString(logMap));
        } else {
            LoggerUtils.error(invocation.getThis().getClass(), JSONUtils.toJSONString(logMap));
        }

    }
}
