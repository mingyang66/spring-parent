package com.sgrain.boot.actuator.filter;

import com.sgrain.boot.common.utils.LoggerUtils;
import com.sgrain.boot.common.utils.RequestUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @description: actuator监控拦截器
 * @create: 2020/07/22
 */
public class ActuatorFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        AntPathMatcher matcher = new AntPathMatcher();
        LoggerUtils.info(ActuatorFilter.class, "访问地址是："+request.getRequestURL()+"，是否允许访问："+ matcher.match("/actuator/**", request.getRequestURI()));
        if(RequestUtils.isInternet(RequestUtils.getClientIp(request))){
            filterChain.doFilter(request, response);
        } else {
            response.setHeader("Content-Type", "text/html; charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.println("非内网用户，拒绝访问");
            writer.close();
        }
    }

}
