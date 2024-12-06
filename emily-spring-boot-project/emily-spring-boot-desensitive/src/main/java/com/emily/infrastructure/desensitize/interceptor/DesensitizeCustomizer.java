package com.emily.infrastructure.desensitize.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.core.Ordered;

/**
 * @author :  Emily
 * @since :  2024/12/7 下午3:48
 */
public interface DesensitizeCustomizer extends MethodInterceptor, Ordered {
}
