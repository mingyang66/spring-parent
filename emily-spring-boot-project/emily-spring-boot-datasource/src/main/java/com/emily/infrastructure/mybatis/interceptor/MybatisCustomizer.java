package com.emily.infrastructure.mybatis.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.core.Ordered;

/**
 * Mybatis埋点扩展点接口MybatisCustomizer，AOP切面会根据优先级选择优先级最高的拦截器
 *
 * @author Emily
 * @since : 2022/2/12
 */
public interface MybatisCustomizer extends MethodInterceptor, Ordered {
}
