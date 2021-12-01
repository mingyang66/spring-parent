package com.emily.infrastructure.datasource.interceptor;

import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;

/**
 * @author Emily
 */
public interface DataSourceCustomizer extends MethodInterceptor {
    /**
     * 获取数据源标识
     * @param method
     * @return
     */
    String getTargetDataSource(Method method);
}
