package com.sgrain.boot.actuator.filter;

import com.sgrain.boot.common.utils.RequestUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description: actuator监控拦截器
 * @create: 2020/07/22
 */
public class ActuatorFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("----OncePerRequestFilter----");
        AntPathMatcher matcher = new AntPathMatcher();
        System.out.println(request.getRequestURL()+"-------"+ matcher.match("/actuator/**", request.getRequestURI()));
        /*if(RequestUtils.isInternet(RequestUtils.getClientIp(request)) && !matcher.match("/actuator/**", request.getRequestURI())){
            filterChain.doFilter(request, response);
        }*/
        filterChain.doFilter(request, response);
    }
}
