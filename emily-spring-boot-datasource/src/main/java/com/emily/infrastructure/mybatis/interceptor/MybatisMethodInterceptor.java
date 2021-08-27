package com.emily.infrastructure.mybatis.interceptor;

import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.google.common.collect.Maps;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.ibatis.annotations.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Author Emily
 * @Version: 1.0
 */
public class MybatisMethodInterceptor implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(MybatisMethodInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            Method method = invocation.getMethod();
            boolean isMapper = method.getDeclaringClass().isAnnotationPresent(Mapper.class);
            String action = null;
            Map<String, Object> paramsMap = null;
            if (isMapper) {
                action = method.getDeclaringClass().getCanonicalName()+"."+method.getName();
                paramsMap = getInParam(invocation);
            }
            Object response = invocation.proceed();
            if (isMapper) {
                recordLog(action, paramsMap, response);
            }
            return response;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    public void recordLog(String action, Map<String, Object> requestParam, Object response) {

    }

    public Map<String, Object> getOutParam(Object outParams) {
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("outParams", outParams);
        return resMap;
    }

    public Map<String, Object> getInParam(MethodInvocation invocation) {
        Map<String, Object> paramMap = Maps.newHashMap();
        try {
            Parameter[] parameters = invocation.getMethod().getParameters();
            Object[] obj = invocation.getArguments();
            for (int i = 0; i < parameters.length; i++) {
                String name = parameters[i].getName();
                Object value = obj[i];
                paramMap.put(name, value);
            }
        } catch (Exception e) {
            logger.error(PrintExceptionInfo.printErrorInfo(e));
        }
        return paramMap;
    }
}
