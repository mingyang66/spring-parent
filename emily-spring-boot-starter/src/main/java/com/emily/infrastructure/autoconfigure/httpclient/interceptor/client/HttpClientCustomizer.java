package com.emily.infrastructure.autoconfigure.httpclient.interceptor.client;

import org.springframework.core.Ordered;
import org.springframework.http.client.ClientHttpRequestInterceptor;

/**
 * @Description: RestTemplate拦截器接口，新增Ordered实现，AOP切面会取优先级最高
 * @Author: Emily
 * @create: 2022/2/11
 * @since 4.0.7
 */
public interface HttpClientCustomizer extends ClientHttpRequestInterceptor, Ordered {
}
