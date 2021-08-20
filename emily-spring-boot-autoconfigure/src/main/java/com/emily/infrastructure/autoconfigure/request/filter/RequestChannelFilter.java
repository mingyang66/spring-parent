package com.emily.infrastructure.autoconfigure.request.filter;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.SystemException;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.autoconfigure.request.servlet.DelegateRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Emily
 * @Description: 拦截所有请求过滤器，并将请求类型是HttpServletRequest类型的请求替换为自定义{@link DelegateRequestWrapper}
 * @create: 2020/8/19
 */
public class RequestChannelFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            ServletRequest requestWrapper;
            if (request instanceof HttpServletRequest) {
                requestWrapper = new DelegateRequestWrapper((HttpServletRequest) request);
                chain.doFilter(requestWrapper, response);
            } else {
                chain.doFilter(request, response);
            }
        } catch (IOException ex) {
            throw new SystemException(AppHttpStatus.IO_EXCEPTION.getStatus(), PrintExceptionInfo.printErrorInfo(ex));
        } catch (ServletException ex) {
            throw new SystemException(AppHttpStatus.EXCEPTION.getStatus(), PrintExceptionInfo.printErrorInfo(ex));
        }


    }

    @Override
    public void destroy() {
    }

}
