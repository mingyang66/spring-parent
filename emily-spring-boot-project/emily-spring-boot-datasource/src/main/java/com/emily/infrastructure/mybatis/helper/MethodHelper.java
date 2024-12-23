package com.emily.infrastructure.mybatis.helper;

import com.emily.infrastructure.aop.utils.MethodInvocationUtils;
import com.emily.infrastructure.sensitize.DataMaskUtils;
import com.emily.infrastructure.sensitize.SensitizeUtils;
import com.emily.infrastructure.sensitize.annotation.DesensitizeProperty;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.ClassUtils;

import java.util.Map;
import java.util.Objects;

/**
 * 请求服务类
 *
 * @author Emily
 * @since 4.0.7
 */
public class MethodHelper {
    private static final boolean COMMONS_SENSITIZE_AVAILABLE = ClassUtils.isPresent("com.emily.infrastructure.sensitize.SensitizeUtils", MethodHelper.class.getClassLoader());

    /**
     * @param invocation 方法切面对象
     * @return 返回调用方法的参数及参数值
     * 获取方法参数，支持指定字段脱敏处理
     */
    public static Map<String, Object> getMethodArgs(MethodInvocation invocation) {
        return MethodInvocationUtils.getMethodArgs(invocation, o -> true,
                (parameter, value) -> {
                    if (COMMONS_SENSITIZE_AVAILABLE) {
                        if (value instanceof String str) {
                            if (parameter.isAnnotationPresent(DesensitizeProperty.class)) {
                                return DataMaskUtils.doGetProperty(str, parameter.getAnnotation(DesensitizeProperty.class).value());
                            } else {
                                return value;
                            }
                        } else {
                            return SensitizeUtils.acquireElseGet(value);
                        }
                    } else {
                        return value;
                    }
                });
    }

    /**
     * 判定是否对返回值进行脱敏处理
     */
    public static Object getResult(Object response) {
        if (Objects.isNull(response)) {
            return null;
        }
        return COMMONS_SENSITIZE_AVAILABLE ? SensitizeUtils.acquireElseGet(response) : response;
    }
}
