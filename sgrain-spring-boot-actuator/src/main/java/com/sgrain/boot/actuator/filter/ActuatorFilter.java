package com.sgrain.boot.actuator.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * @program: spring-parent
 * @description: actuator监控拦截器
 * @author: 姚明洋
 * @create: 2020/07/22
 */
@Component
@WebFilter(urlPatterns = {"/**"}, description = "对actuator端点过滤")
public class ActuatorFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("---------------过滤器----------");
        chain.doFilter(request, response);
    }

    /**
     * 销毁方法
     */
    @Override
    public void destroy() {

    }
}
