package com.emily.infrastructure.web.request.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.core.Ordered;

/**
 * API请求日志拦截器扩展接口，其实现Ordered接口，AOP切面会根据优先级顺序取优先级最高的拦截器
 *
 * @author Emily
 * @since 2021/11/25
 */
public interface RequestCustomizer extends MethodInterceptor, Ordered {
}
