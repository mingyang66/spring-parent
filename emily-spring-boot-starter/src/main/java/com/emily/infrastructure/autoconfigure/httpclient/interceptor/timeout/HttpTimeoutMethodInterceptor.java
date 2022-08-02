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
 * @Description :  方法调用拦截器
 * @Author :  Emily
 * @CreateDate :  Created in 2022/8/2 5:59 下午
 */
public class HttpTimeoutMethodInterceptor implements MethodInterceptor {
    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        try {
            Method method = invocation.getMethod();
            if (method.isAnnotationPresent(TargetHttpTimeout.class)) {
                TargetHttpTimeout targetHttpTimeout = method.getAnnotation(TargetHttpTimeout.class);
                RequestConfig requestConfig = RequestConfig.custom()
                        .setSocketTimeout(targetHttpTimeout.readTimeout())
                        .setConnectTimeout(targetHttpTimeout.connectTimeout())
                        .build();
                HttpContextHolder.bind(requestConfig);
            }
            return invocation.proceed();
        } finally {
            HttpContextHolder.unbind();
        }
    }
}
