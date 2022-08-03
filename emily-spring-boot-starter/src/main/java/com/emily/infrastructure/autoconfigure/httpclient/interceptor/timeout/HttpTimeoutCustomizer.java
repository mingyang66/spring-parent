package com.emily.infrastructure.autoconfigure.httpclient.interceptor.timeout;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.Ordered;

/**
 * @Description :  RestTemplate请求超时时间设置接口
 * @Author :  Emily
 * @CreateDate :  Created in 2022/8/3 2:42 下午
 * @since 4.1.3
 */
public interface HttpTimeoutCustomizer extends MethodInterceptor, Ordered {
    /**
     * 拦截器前置方法
     *
     * @param invocation
     */
    void before(MethodInvocation invocation);

    /**
     * 拦截器后置方法
     */
    void after();
}
