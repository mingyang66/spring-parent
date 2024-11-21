package com.emily.infrastructure.web.filter.filter;

import jakarta.annotation.Nonnull;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;


/**
 * 拦截所有请求过滤器，并将请求类型是HttpServletRequest类型的请求替换为自定义{@link ContentCachingRequestWrapper}
 *
 * @author Emily
 * @since 2020/8/19
 */
public class ContentCachingWrapper extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //创建ContentCachingRequestWrapper对象用于缓存请求体
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        //继续执行过滤器链，并传递包装后的请求对象
        filterChain.doFilter(requestWrapper, response);
        System.out.println("---");
    }
}
