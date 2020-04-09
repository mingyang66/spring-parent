package com.sgrain.boot.autoconfigure.aop.interceptor;

import com.sgrain.boot.autoconfigure.log.LogAopProperties;
import com.sgrain.boot.common.po.BaseRequest;
import com.sgrain.boot.common.utils.LoggerUtils;
import com.sgrain.boot.common.utils.ObjectSizeUtil;
import com.sgrain.boot.common.utils.RequestUtils;
import com.sgrain.boot.common.utils.json.JSONUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Version: 1.0
 */
@SuppressWarnings("all")
public class LogAopMethodInterceptor implements MethodInterceptor {

    private LogAopProperties properties;
    public LogAopMethodInterceptor(LogAopProperties properties){
        this.properties = properties;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        HttpServletRequest request = RequestUtils.getRequest();
        //获取请求参数，且该参数获取必须在proceed之前
        Map<String, Object> paramsMap = getRequestParam(invocation, request);
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
        String log = StringUtils.join(properties.getNameLine(), properties.getMsgController(), invocation.getThis().getClass(), ".", invocation.getMethod().getName(), properties.getNameLine());
        log = StringUtils.join(log, properties.getMsgAccessUrl(), request.getRequestURL(), properties.getNameLine());
        log = StringUtils.join(log, properties.getMsgMethod(), request.getMethod(), properties.getNameLine());
        log = StringUtils.join(log, properties.getMsgParams(), paramsMap, properties.getNameLine());
        log = StringUtils.join(log, properties.getMsgTime() , spentTime, properties.getMillSecond(), properties.getNameLine());
        if(ObjectUtils.isEmpty(result)){
            log = StringUtils.join(log, properties.getMsgReturnValue(), result, properties.getNameLine());
        } else if(result instanceof ResponseEntity){
            log = StringUtils.join(log, properties.getMsgReturnValue(), JSONUtils.toJSONString(((ResponseEntity)result).getBody()), properties.getNameLine());
        } else {
            log = StringUtils.join(log, properties.getMsgReturnValue(), JSONUtils.toJSONString(result), properties.getNameLine());
        }
        log = StringUtils.join(log, properties.getMsgDataSize(), ObjectSizeUtil.getObjectSizeUnit(result), properties.getNameLine());
        LoggerUtils.info(invocation.getThis().getClass(), log);
    }
    /**
     * @Description 异常日志
     * @Version  1.0
     */
    private void logError(MethodInvocation invocation, HttpServletRequest request, Map<String, Object> paramsMap, long spentTime, Throwable e){
        String log = StringUtils.join(properties.getNameLine(), properties.getMsgController(), invocation.getThis().getClass(), ".", invocation.getMethod().getName(), properties.getNameLine());
        log = StringUtils.join(log, properties.getMsgAccessUrl(), request.getRequestURL(), properties.getNameLine());
        log = StringUtils.join(log, properties.getMsgMethod(), request.getMethod(), properties.getNameLine());
        log = StringUtils.join(log, properties.getMsgParams(), paramsMap, properties.getNameLine());
        log = StringUtils.join(log, properties.getMsgTime() , spentTime, properties.getMillSecond(), properties.getNameLine());
        log = StringUtils.join(log, properties.getMsgException() , e.getStackTrace()[0], " ", e, properties.getNameLine());
        LoggerUtils.error(invocation.getThis().getClass(), log);

    }
    /**
     * @Description 获取请求参数
     * @Version  1.0
     */
    private Map<String, Object> getRequestParam(MethodInvocation invocation, HttpServletRequest request){
        Map<String, Object> paramMap = new LinkedHashMap<>();
        Object[] args = invocation.getArguments();
        Method method = invocation.getMethod();
        Parameter[] parameters = method.getParameters();
        if(ArrayUtils.isEmpty(parameters)){
            return null;
        }
        for(int i=0; i<parameters.length; i++){
            if(args[i] instanceof HttpServletRequest){
                Enumeration<String> params = request.getParameterNames();
                while (params.hasMoreElements()){
                    String key = params.nextElement();
                    paramMap.put(key, request.getParameter(key));
                }
            } else if(!(args[i] instanceof HttpServletResponse)){
                if(args[i] instanceof BaseRequest){
                    BaseRequest baseRequest = (BaseRequest) args[i];
                    //将用户信息设置如HttpServletRequest中
                    request.setAttribute(parameters[i].getName(), baseRequest);
                    paramMap.put(parameters[i].getName(), JSONUtils.toJSONString(baseRequest));
                } else {
                    paramMap.put(parameters[i].getName(), JSONUtils.toJSONString(args[i]));
                }
            }
        }
        return paramMap;
    }
}
