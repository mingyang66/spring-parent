package com.emily.infrastructure.autoconfigure.httpclient.interceptor.timeout;

import com.emily.infrastructure.autoconfigure.httpclient.annotation.TargetHttpTimeout;
import com.emily.infrastructure.autoconfigure.httpclient.context.HttpContextHolder;
import com.emily.infrastructure.core.constant.AopOrderInfo;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.http.client.config.RequestConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * Http请求超时设置拦截器，即方法上标注@TargetHttpTimeout注解才会生效
 *
 * @author Emily
 * @since 4.1.3
 */
public class DefaultHttpTimeoutMethodInterceptor implements HttpTimeoutCustomizer {
    /**
     * 拦截器前置方法
     *
     * @param invocation 反射方法对象
     */
    @Override
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
     * @param invocation 反射方法对象
     * @return 方法调用结果
     * @throws Throwable 异常
     */
    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        try {
            this.before(invocation);
            return invocation.proceed();
        } finally {
            this.after();
        }
    }

    /**
     * 拦截器后置处理方法
     */
    @Override
    public void after() {
        HttpContextHolder.unbind();
    }

    @Override
    public int getOrder() {
        return AopOrderInfo.HTTP_CLIENT;
    }
}
