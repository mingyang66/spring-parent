package com.emily.infrastructure.tracing.interceptor;

import org.aopalliance.intercept.MethodInterceptor;

/**
 * 非servlet上下文执行完成后移除拦截器，对servlet上下文场景同样适用
 *
 * @author :  Emily
 * @since :  2024/12/10 下午3:38
 */
public interface TracingCustomizer extends MethodInterceptor {
}
