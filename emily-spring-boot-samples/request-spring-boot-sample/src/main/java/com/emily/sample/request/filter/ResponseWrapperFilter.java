package com.emily.sample.request.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

/**
 * @author :  Emily
 * @since :  2024/10/25 上午10:42
 */
@Component
public class ResponseWrapperFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(request, wrapper);
        byte[] content = wrapper.getContentAsByteArray();
        wrapper.setHeader("Content-Length", String.valueOf(content.length));
        wrapper.copyBodyToResponse();
    }
}
