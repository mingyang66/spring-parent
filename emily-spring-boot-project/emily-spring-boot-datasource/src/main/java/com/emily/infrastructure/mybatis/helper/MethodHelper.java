package com.emily.infrastructure.mybatis.helper;

import com.emily.infrastructure.aop.utils.MethodInvocationUtils;
import com.emily.infrastructure.sensitive.DataMaskUtils;
import com.emily.infrastructure.sensitive.SensitiveUtils;
import com.emily.infrastructure.sensitive.annotation.JsonSimField;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Map;

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
        return MethodInvocationUtils.getMethodArgs(invocation, o -> true,
                (parameter, value) -> {
                    if (value instanceof String str) {
                        if (parameter.isAnnotationPresent(JsonSimField.class)) {
                            return DataMaskUtils.doGetProperty(str, parameter.getAnnotation(JsonSimField.class).value());
                        } else {
                            return value;
                        }
                    } else {
                        return SensitiveUtils.acquireElseGet(value);
                    }
                });
    }

}
