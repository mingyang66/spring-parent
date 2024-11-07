package com.emily.infrastructure.transfer.rest.interceptor.timeout;

import com.emily.infrastructure.aop.constant.AopOrderInfo;
import com.emily.infrastructure.transfer.rest.annotation.TargetHttpTimeout;
import com.emily.infrastructure.transfer.rest.context.RestTemplateContextHolder;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.hc.client5.http.config.RequestConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Http请求超时设置拦截器，即方法上标注@TargetHttpTimeout注解才会生效
 *
 * @author Emily
 * @since 4.1.3
 */
public class DefaultRestTemplateTimeoutMethodInterceptor implements RestTemplateTimeoutCustomizer {
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
                .setResponseTimeout(targetHttpTimeout.readTimeout(), TimeUnit.MILLISECONDS)
                .setConnectionRequestTimeout(targetHttpTimeout.connectTimeout(), TimeUnit.MILLISECONDS)
                .build();
        RestTemplateContextHolder.bind(requestConfig);
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
        RestTemplateContextHolder.unbind();
    }

    @Override
    public int getOrder() {
        return AopOrderInfo.HTTP_CLIENT;
    }
}
