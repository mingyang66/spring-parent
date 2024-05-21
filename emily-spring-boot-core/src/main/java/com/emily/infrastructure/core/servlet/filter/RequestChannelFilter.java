package com.emily.infrastructure.core.servlet.filter;

import com.emily.infrastructure.common.PrintExceptionUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;


/**
 * 拦截所有请求过滤器，并将请求类型是HttpServletRequest类型的请求替换为自定义{@link DelegateRequestWrapper}
 *
 * @author Emily
 * @since 2020/8/19
 */
public class RequestChannelFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            ServletRequest requestWrapper;
            if (request instanceof HttpServletRequest httpServletRequest) {
                requestWrapper = new DelegateRequestWrapper(httpServletRequest);
                chain.doFilter(requestWrapper, response);
            } else {
                chain.doFilter(request, response);
            }
        } catch (Exception ex) {
            throw new RuntimeException(PrintExceptionUtils.printErrorInfo(ex));
        }
    }

    @Override
    public void destroy() {
    }

}
