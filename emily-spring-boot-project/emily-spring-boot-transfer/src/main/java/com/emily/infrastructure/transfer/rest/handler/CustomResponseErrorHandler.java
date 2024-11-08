package com.emily.infrastructure.transfer.rest.handler;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

/**
 * 自定义异常处理
 *
 * @author Emily
 * @since 2020/08/18
 */
public class CustomResponseErrorHandler extends DefaultResponseErrorHandler {
    /**
     * 判定响应是否有任何错误
     *
     * @return true :返回的响应有错误，false无错误
     */
    protected boolean hasError(HttpStatusCode statusCode) {
        return true;
    }

}
