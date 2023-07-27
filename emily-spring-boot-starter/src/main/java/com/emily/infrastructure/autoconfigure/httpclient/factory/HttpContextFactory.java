package com.emily.infrastructure.autoconfigure.httpclient.factory;

import com.emily.infrastructure.autoconfigure.httpclient.context.HttpContextHolder;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * 自定义HttpContext HTTP进程执行状态，它是一种可用于将属性名称映射到属性值的结构
 *
 * @author Emily
 * @since 4.1.3
 */
public class HttpContextFactory implements BiFunction<HttpMethod, URI, HttpContext> {
    @Override
    public HttpContext apply(HttpMethod httpMethod, URI uri) {
        RequestConfig requestConfig = HttpContextHolder.current();
        if (Objects.nonNull(requestConfig)) {
            HttpContext context = HttpClientContext.create();
            context.setAttribute(HttpClientContext.REQUEST_CONFIG, requestConfig);
            return context;
        }
        return null;
    }
}
