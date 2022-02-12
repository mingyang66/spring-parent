package com.emily.infrastructure.autoconfigure.httpclient.handler;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 自定义异常处理
 * @create: 2020/08/18
 */
public class CustomResponseErrorHandler implements ResponseErrorHandler {
    /**
     * 判定响应是否有任何错误
     * true :返回的响应有错误，false无错误
     *
     * @param response
     * @return
     * @throws IOException
     */
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return true;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
    }
}
