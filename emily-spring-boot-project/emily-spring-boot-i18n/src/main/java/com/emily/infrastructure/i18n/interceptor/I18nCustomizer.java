package com.emily.infrastructure.i18n.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.core.Ordered;

/**
 * @author :  Emily
 * @since :  2024/10/31 上午10:19
 */
public interface I18nCustomizer extends MethodInterceptor, Ordered {
}
