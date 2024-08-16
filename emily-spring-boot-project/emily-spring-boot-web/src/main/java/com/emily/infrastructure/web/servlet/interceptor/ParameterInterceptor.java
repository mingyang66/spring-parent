package com.emily.infrastructure.web.servlet.interceptor;

import com.emily.infrastructure.tracing.holder.LocalContextHolder;
import com.emily.infrastructure.tracing.holder.ServletStage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * 异常处理前置拦截器，用于初始化上下文、移除上下文
 *
 * @author :  Emily
 * @since :  2023/8/18 11:03 AM
 */
public class ParameterInterceptor implements HandlerInterceptor {
    /**
     * 初始化上下文，并标记当前阶段标识
     *
     * @param request  HttpServletRequest请求对象
     * @param response HttpServletResponse响应对象
     * @param handler  方法处理对象
     * @return true-永远向下一部执行
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //标记阶段标识
        LocalContextHolder.current().setServletStage(ServletStage.BEFORE_PARAMETER);
        return true;
    }

    /**
     * 移除当前上下文
     *
     * @param request  HttpServletRequest请求对象
     * @param response HttpServletResponse响应对象
     * @param handler  方法处理对象
     * @param ex       异常
     * @throws Exception 抛出异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //移除线程上下文数据
        LocalContextHolder.unbind(true);
    }
}
