package com.emily.infrastructure.desensitize.interceptor;

import com.emily.infrastructure.desensitize.annotation.DesensitizeOperation;
import com.emily.infrastructure.sensitive.DeSensitizeUtils;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author :  Emily
 * @since :  2024/12/7 下午3:49
 */
public class DefaultDesensitizeMethodInterceptor implements DesensitizeCustomizer {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if (method.isAnnotationPresent(DesensitizeOperation.class)) {
            DesensitizeOperation annotation = method.getAnnotation(DesensitizeOperation.class);
            Object target = invocation.proceed();
            if (target == null) {
                return null;
            }
            Class<?> packClass = annotation.removePackClass();
            return DeSensitizeUtils.acquireElseGet(target, packClass);
        }
        return invocation.proceed();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
