package com.emily.infrastructure.datasource.interceptor;

import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;

/**
 * @author Emily
 */
public interface DataSourceCustomizer extends MethodInterceptor {
    /**
     * 获取目标数据源标识
     *
     * @param method 注解标注的方法对象
     * @return 数据源唯一标识
     * @since(4.0.5)
     */
    String getTargetDataSource(Method method);
}
