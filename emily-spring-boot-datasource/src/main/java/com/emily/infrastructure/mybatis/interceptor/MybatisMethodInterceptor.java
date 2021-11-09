package com.emily.infrastructure.mybatis.interceptor;

import com.emily.infrastructure.common.enums.DateFormat;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.core.entity.BaseLogger;
import com.emily.infrastructure.core.helper.ThreadPoolHelper;
import com.emily.infrastructure.core.holder.ContextHolder;
import com.google.common.collect.Maps;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.ibatis.annotations.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Author Emily
 * @Version: 1.0
 */
public class MybatisMethodInterceptor implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(MybatisMethodInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        boolean isMapper = method.getDeclaringClass().isAnnotationPresent(Mapper.class);
        long start = System.currentTimeMillis();
        String action = null;
        Map<String, Object> paramsMap = null;
        Object response = null;
        try {
            isMapper = method.getDeclaringClass().isAnnotationPresent(Mapper.class);
            if (isMapper) {
                action = method.getDeclaringClass().getCanonicalName() + "." + method.getName();
                paramsMap = getInParam(invocation);
            }
            response = invocation.proceed();
            return response;
        } catch (Throwable ex) {
            response = PrintExceptionInfo.printErrorInfo(ex);
            throw ex;
        } finally {
            if (isMapper) {
                recordLog(action, paramsMap, response, start);
            }
        }
    }

    public void recordLog(String action, Map<String, Object> requestParam, Object response, long start) {
        try {
            BaseLogger baseLogger = new BaseLogger();
            baseLogger.setTraceId(ContextHolder.get().getTraceId());
            baseLogger.setRequestParams(requestParam);
            baseLogger.setBody(response);
            baseLogger.setUrl(action);
            baseLogger.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormat.YYYY_MM_DDTHH_MM_SS_COLON_SSS.getFormat())));
            baseLogger.setTime(System.currentTimeMillis() - start);
            baseLogger.setMethod("DB");
            ThreadPoolHelper.threadPoolTaskExecutor().submit(() -> {
                logger.info(JSONUtils.toJSONString(baseLogger));
            });
        } catch (Exception exception) {
            logger.error(PrintExceptionInfo.printErrorInfo(exception));
        }
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
