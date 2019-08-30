package com.yaomy.control.aop.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaomy.control.common.control.utils.ObjectSizeUtil;
import com.yaomy.control.logback.utils.LoggerUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: 拦截URL请求
 * @Version: 1.0
 */
public class ControllerInterceptor implements MethodInterceptor {
    /**
     * 换行符
     */
    public static final String NEW_LINE = "\n";

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = invocation.proceed();
        stopWatch.stop();

        ObjectMapper objectMapper = new ObjectMapper();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String log = StringUtils.join(NEW_LINE, "控制器  ：", invocation.getThis().getClass(), ".", invocation.getMethod().getName(), NEW_LINE);
        log = StringUtils.join(log, "访问URL ：", request.getRequestURL(), NEW_LINE);
        log = StringUtils.join(log, "Method  ：", request.getMethod(), NEW_LINE);
        log = StringUtils.join(log, "请求参数：", getReqestParam(invocation), NEW_LINE);
        log = StringUtils.join(log,"耗  时  ：" , stopWatch.getTime(), "ms", NEW_LINE);
        if(ObjectUtils.isEmpty(result)){
            log = StringUtils.join(log, "返回结果：", result, NEW_LINE);
        } else if(result instanceof ResponseEntity){
            log = StringUtils.join(log, "返回结果：", objectMapper.writeValueAsString(((ResponseEntity)result).getBody()), NEW_LINE);
        } else {
            log = StringUtils.join(log, "返回结果：", objectMapper.writeValueAsString(result), NEW_LINE);
        }
        log = StringUtils.join(log, "数据大小：", ObjectSizeUtil.humanReadableUnits(result), NEW_LINE);
        LoggerUtil.info(invocation.getThis().getClass(), log);
        return result;
    }
    /**
     * @Description 获取请求参数
     * @Version  1.0
     */
    private Map<String, Object> getReqestParam(MethodInvocation invocation){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> paramMap = new LinkedHashMap<>();
            Object[] args = invocation.getArguments();
            Method method = invocation.getMethod();
            Parameter[] parameters = method.getParameters();
            if(ArrayUtils.isEmpty(parameters)){
                return null;
            }
            for(int i=0; i<parameters.length; i++){
                if(args[i] instanceof HttpServletRequest){
                    HttpServletRequest request = (HttpServletRequest) args[i];
                    Enumeration<String> params = request.getParameterNames();
                    while (params.hasMoreElements()){
                        String key = params.nextElement();
                        paramMap.put(key, request.getParameter(key));
                    }
                } else if(!(args[i] instanceof HttpServletResponse)){
                    paramMap.put(parameters[i].getName(), objectMapper.writeValueAsString(args[i]));
                }
            }
            return paramMap;
        } catch (JsonProcessingException e){
            return null;
        }
    }
}
