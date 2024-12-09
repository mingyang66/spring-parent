package com.emily.infrastructure.desensitize.interceptor;

import com.emily.infrastructure.aop.constant.AopOrderInfo;
import com.emily.infrastructure.desensitize.annotation.DesensitizeOperation;
import com.emily.infrastructure.sensitive.DeSensitizeUtils;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * 对请求响应结果进行脱敏处理
 *
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
            return DeSensitizeUtils.acquireElseGet(target, annotation.removePackClass());
        }
        return invocation.proceed();
    }


    @Override
    public int getOrder() {
        return AopOrderInfo.DESENSITIZE_INTERCEPTOR;
    }
}
