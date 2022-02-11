package com.emily.infrastructure.datasource.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

/**
* @Description: 多数据源统一拦截器父接口，其实现了Ordered接口，AOP切面会根据优先级顺序启用优先级最高的拦截器
* @Author: Emily
* @create: 2022/2/11
*/
public interface DataSourceCustomizer extends MethodInterceptor, Ordered {
    /**
     * 获取目标数据源标识
     *
     * @param method 注解标注的方法对象
     * @return 数据源唯一标识
     * @since(4.0.5)
     */
    String getTargetDataSource(Method method);
}
