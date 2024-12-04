package com.emily.infrastructure.aop.utils;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.io.InputStreamSource;
import org.springframework.util.Assert;

import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 请求服务类
 *
 * @author Emily
 * @since 4.0.7
 */
public class MethodInvocationUtils {
    /**
     * 1. 支持参数为实体类的脱敏处理；
     * 2. 支持单个参数的脱敏处理；
     * 函数表达式代码示例：
     * <pre>{@code
     *             if (value instanceof String valueStr) {
     *                 if (parameter.isAnnotationPresent(JsonSimField.class)) {
     *                     paramMap.put(name, DataMaskUtils.doGetProperty(valueStr, parameter.getAnnotation(JsonSimField.class).value()));
     *                 } else {
     *                     paramMap.put(name, value);
     *                 }
     *             } else {
     *                 paramMap.put(name, SensitiveUtils.acquireElseGet(value));
     *             }
     * }</pre>
     *
     * @param invocation 方法切面对象
     * @param exclude    指定需要排除要解析的参数类型
     * @param analysis   对参数值记性解析，如脱敏处理
     * @return 返回调用方法的参数及参数值
     */
    public static Map<String, Object> getMethodArgs(MethodInvocation invocation, Function<Object, Boolean> exclude, BiFunction<Parameter, Object, Object> analysis) {
        Assert.notNull(invocation, () -> "MethodInvocation must not be null");
        Assert.notNull(exclude, () -> "Supplier must not be null");
        Assert.notNull(analysis, () -> "BiFunction must not be null");
        if (invocation.getArguments().length == 0) {
            return Collections.emptyMap();
        }
        Object[] args = invocation.getArguments();
        Parameter[] parameters = invocation.getMethod().getParameters();
        Map<String, Object> paramMap = new HashMap<>(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String name = parameter.getName();
            Object value = args[i];
            if (Objects.isNull(value)) {
                paramMap.put(name, null);
                continue;
            }
            if (exclude.apply(value) || value instanceof InputStreamSource) {
                continue;
            }
            paramMap.put(name, analysis.apply(parameter, value));
        }
        return paramMap;
    }

}
