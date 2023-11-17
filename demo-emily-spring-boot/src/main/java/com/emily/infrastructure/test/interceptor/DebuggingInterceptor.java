package com.emily.infrastructure.test.interceptor;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.ConstructorInvocation;

import javax.annotation.Nonnull;

/**
 * @author Emily
 * @program: spring-parent
 * 构造函数拦截器
 * @since 2021/05/25
 */
public class DebuggingInterceptor implements ConstructorInterceptor {
    private Object instance = null;

    @Nonnull
    @Override
    public Object construct(ConstructorInvocation invocation) throws Throwable {
        if (instance == null) {
            return instance = invocation.proceed();
        } else {
            throw new Exception("singleton does not allow multiple instance");
        }
    }
}
