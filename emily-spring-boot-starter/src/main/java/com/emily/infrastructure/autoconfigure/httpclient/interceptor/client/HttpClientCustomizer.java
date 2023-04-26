package com.emily.infrastructure.autoconfigure.httpclient.interceptor.client;

import org.springframework.core.Ordered;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * @Description: RestTemplate拦截器接口，新增Ordered实现，AOP切面会取优先级最高
 * @Author: Emily
 * @create: 2022/2/11
 * @since 4.0.7
 */
public interface HttpClientCustomizer extends ClientHttpRequestInterceptor, Ordered {
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException;

    public int getOrder();
}
