package com.emily.infrastructure.desensitize.interceptor;

import com.emily.infrastructure.aop.constant.AopOrderInfo;
import com.emily.infrastructure.desensitize.annotation.DesensitizeOperation;
import com.emily.infrastructure.sensitize.DeSensitizeUtils;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 对请求响应结果进行脱敏处理
 * {@link DesensitizeOperation}注解可以标记在方法和类上，以方法上的标注优先级最高
 *
 * @author :  Emily
 * @since :  2024/12/7 下午3:49
 */
public class DefaultDesensitizeMethodInterceptor implements DesensitizeCustomizer {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        DesensitizeOperation annotation = null;
        if (method.isAnnotationPresent(DesensitizeOperation.class)) {
            annotation = method.getAnnotation(DesensitizeOperation.class);
        } else if (method.getDeclaringClass().isAnnotationPresent(DesensitizeOperation.class)) {
            annotation = method.getDeclaringClass().getAnnotation(DesensitizeOperation.class);
        }
        if (Objects.nonNull(annotation)) {
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
