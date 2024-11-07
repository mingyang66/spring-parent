package com.emily.infrastructure.transfer.rest.interceptor.client;

import org.springframework.core.Ordered;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * RestTemplate拦截器接口，新增Ordered实现，AOP切面会取优先级最高
 *
 * @author Emily
 * @since 4.0.7
 */
public interface RestTemplateCustomizer extends ClientHttpRequestInterceptor, Ordered {
    /**
     * RestTemplate拦截方法
     *
     * @param request   请求对象
     * @param body      请求体
     * @param execution 请求方法执行对象
     * @return 响应对象
     * @throws IOException 抛出异常
     */
    ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException;

    /**
     * 获取扩展点执行顺序
     *
     * @return 执行顺序
     */
    int getOrder();
}
