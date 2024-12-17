package com.emily.infrastructure.desensitize.interceptor;

import com.emily.infrastructure.aop.constant.AopOrderInfo;
import com.emily.infrastructure.desensitize.annotation.DesensitizeOperation;
import com.emily.infrastructure.sensitize.DeSensitizeUtils;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Objects;

/**
 * 对请求响应结果进行脱敏处理
 * {@link DesensitizeOperation}注解可以标记在方法上
 *
 * @author :  Emily
 * @since :  2024/12/7 下午3:49
 */
public class DefaultDesensitizeMethodInterceptor implements DesensitizeCustomizer {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object response = invocation.proceed();
        if (Objects.isNull(response)) {
            return null;
        }
        DesensitizeOperation annotation = invocation.getMethod().getAnnotation(DesensitizeOperation.class);
        return DeSensitizeUtils.acquireElseGet(invocation.proceed(), annotation.removePackClass());

    }


    @Override
    public int getOrder() {
        return AopOrderInfo.DESENSITIZE + 1;
    }
}
