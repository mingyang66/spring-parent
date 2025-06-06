package com.emily.sample.request.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

/**
 * @author :  Emily
 * @since :  2024/10/25 上午20:36
 */
//@Component
public class RequestWrapperFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //创建ContentCachingRequestWrapper对象用于缓存请求体
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        //创建ContentCachingResponseWrapper对象用于缓存响应体
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        //继续执行过滤器链，并传递包装后的请求对象
        filterChain.doFilter(requestWrapper, responseWrapper);
        //过滤器链执行完毕，请求体被消费 读取缓存的请求体
        byte[] reqBody = requestWrapper.getContentAsByteArray();
        //将请求体转换为字符串
        String reqBodyStr = new String(reqBody, requestWrapper.getCharacterEncoding());
        //打印请求体
        System.out.println("Request Param:" + reqBodyStr);

        //----------------response----------------
        //过滤器执行完毕，读取响应体
        byte[] resBody = responseWrapper.getContentAsByteArray();
        //将响应体转换为字符串
        String resBodyStr = new String(resBody, responseWrapper.getCharacterEncoding());
        //打印响应体信息
        System.out.println("Response Body:" + resBodyStr);
        //将缓存的响应体数据回写到响应流中
        responseWrapper.copyBodyToResponse();
    }
}
