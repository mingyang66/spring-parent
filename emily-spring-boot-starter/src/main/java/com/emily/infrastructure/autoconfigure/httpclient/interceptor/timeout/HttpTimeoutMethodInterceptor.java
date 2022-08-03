package com.emily.infrastructure.autoconfigure.httpclient.interceptor.timeout;

import com.emily.infrastructure.autoconfigure.httpclient.annotation.TargetHttpTimeout;
import com.emily.infrastructure.autoconfigure.httpclient.context.HttpContextHolder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.http.client.config.RequestConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * @Description :  Http请求超时设置拦截器，即方法上标注@TargetHttpTimeout注解才会生效
 * @Author :  Emily
 * @CreateDate :  Created in 2022/8/2 5:59 下午
 */
public class HttpTimeoutMethodInterceptor implements MethodInterceptor {
    /**
     * 拦截器前置方法
     *
     * @param invocation
     */
    public void before(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        if (!method.isAnnotationPresent(TargetHttpTimeout.class)) {
            return;
        }
        TargetHttpTimeout targetHttpTimeout = method.getAnnotation(TargetHttpTimeout.class);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(targetHttpTimeout.readTimeout())
                .setConnectTimeout(targetHttpTimeout.connectTimeout())
                .build();
        HttpContextHolder.bind(requestConfig);
    }

    /**
     * 拦截器调用方法
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        try {
            this.before(invocation);
            return invocation.proceed();
        } finally {
            HttpContextHolder.unbind();
        }
    }
}
