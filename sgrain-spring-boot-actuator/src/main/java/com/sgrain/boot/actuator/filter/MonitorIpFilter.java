package com.sgrain.boot.actuator.filter;

import com.sgrain.boot.common.enums.DateFormatEnum;
import com.sgrain.boot.common.utils.RequestUtils;
import com.sgrain.boot.common.utils.date.DateUtils;
import com.sgrain.boot.common.utils.log.LoggerUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * @description: actuator监控拦截器
 * @create: 2020/07/22
 */
public class MonitorIpFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(RequestUtils.isInternet(RequestUtils.getClientIp(request))){
            LoggerUtils.info(MonitorIpFilter.class, "健康检查："+request.getRequestURL()+ "--"+ DateUtils.formatDate(new Date(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()));
            filterChain.doFilter(request, response);
        } else {
            response.setHeader("Content-Type", "text/html; charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.println("非内网用户，拒绝访问");
            writer.close();
        }
    }
}
