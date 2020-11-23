package com.sgrain.boot.context.filter;

import com.sgrain.boot.context.servlet.RequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Description: 拦截所有请求过滤器，并将请求类型是HttpServletRequest类型的请求替换为自定义{@link RequestWrapper}
 * @create: 2020/8/19
 */
public class RequestChannelFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            ServletRequest requestWrapper;
            if (request instanceof HttpServletRequest) {
                requestWrapper = new RequestWrapper((HttpServletRequest) request);
                chain.doFilter(requestWrapper, response);
            } else {
                chain.doFilter(request, response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void destroy() {
    }

}
