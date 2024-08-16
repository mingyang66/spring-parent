package com.emily.infrastructure.mybatis.helper;

import com.emily.infrastructure.sensitive.DataMaskUtils;
import com.emily.infrastructure.sensitive.SensitiveUtils;
import com.emily.infrastructure.sensitive.annotation.JsonSimField;
import com.google.common.collect.Maps;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.io.InputStreamSource;

import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * 请求服务类
 *
 * @author Emily
 * @since 4.0.7
 */
public class MethodHelper {
    /**
     * @param invocation 方法切面对象
     * @return 返回调用方法的参数及参数值
     * 获取方法参数，支持指定字段脱敏处理
     */
    public static Map<String, Object> getMethodArgs(MethodInvocation invocation) {
        if (invocation.getArguments().length == 0) {
            return Collections.emptyMap();
        }
        Object[] args = invocation.getArguments();
        Parameter[] parameters = invocation.getMethod().getParameters();
        Map<String, Object> paramMap = Maps.newLinkedHashMap();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String name = parameter.getName();
            Object value = args[i];
            if (Objects.isNull(value)) {
                paramMap.put(name, null);
                continue;
            }
            if (checkServletStream(value)) {
                continue;
            }
            if (value instanceof String valueStr) {
                if (parameter.isAnnotationPresent(JsonSimField.class)) {
                    paramMap.put(name, DataMaskUtils.doGetProperty(valueStr, parameter.getAnnotation(JsonSimField.class).value()));
                } else {
                    paramMap.put(name, value);
                }
            } else {
                paramMap.put(name, SensitiveUtils.acquireElseGet(value));
            }
        }
        return paramMap;
    }

    /**
     * 是否继续下一步
     *
     * @param value 对象值
     * @return 校验参数类型是否需要处理
     */
    protected static boolean checkServletStream(Object value) {
        return value instanceof InputStreamSource;
    }

}
