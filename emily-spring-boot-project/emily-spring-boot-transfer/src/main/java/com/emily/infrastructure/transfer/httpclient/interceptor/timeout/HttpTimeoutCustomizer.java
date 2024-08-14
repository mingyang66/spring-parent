package com.emily.infrastructure.transfer.httpclient.interceptor.timeout;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.Ordered;

/**
 * RestTemplate请求超时时间设置接口
 *
 * @author Emily
 * @since 4.1.3
 */
public interface HttpTimeoutCustomizer extends MethodInterceptor, Ordered {
    /**
     * 拦截器前置方法
     *
     * @param invocation 反射方法对象
     */
    void before(MethodInvocation invocation);

    /**
     * 拦截器后置方法
     */
    void after();
}
