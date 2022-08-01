package com.emily.infrastructure.datasource.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

/**
 * @Description: 多数据源埋点接口DataSourceCustomizer，AOP切面会根据优先级选择优先级最高的拦截器
 * @Author: Emily
 * @create: 2022/2/11
 * @since 4.0.7
 */
public interface DataSourceCustomizer extends MethodInterceptor, Ordered {
    /**
     * 获取要切换的目标数据源标识
     *
     * @param invocation 方法反射对象
     * @param dataSource 原数据库标识
     * @return 目标数据源标识
     * @since 4.0.9
     */
    default String resolveSpecifiedLookupKey(MethodInvocation invocation, String dataSource) {
        return dataSource;
    }

    /**
     * 拦截器执行之前执行方法
     *
     * @param method
     * @return 返回要调用数据的标识
     * @since 4.0.8
     */
    String before(Method method);

    /**
     * 拦截器执行之后执行方法
     *
     * @param method
     * @since 4.0.8
     */
    void after(Method method);
}
