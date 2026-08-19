package com.emily.infrastructure.transfer.feign.interceptor;

import com.emily.infrastructure.json.JsonUtils;
import feign.InvocationContext;
import feign.ResponseInterceptor;

import java.io.InputStream;

/**
 * Feign请求响应拦截器
 *
 * @author :  Emily
 * @since :  2024/8/23 下午7:01
 */
public class FeignResponseInterceptor implements ResponseInterceptor {
    @Override
    public Object intercept(InvocationContext invocationContext, Chain chain) throws Exception {
        try (InputStream inputStream = invocationContext.response().body().asInputStream()) {
            Object response = JsonUtils.toObject(inputStream, Object.class);
        }
        return chain.next(invocationContext);
    }
}
