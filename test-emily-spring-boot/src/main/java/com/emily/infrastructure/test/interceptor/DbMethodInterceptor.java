package com.emily.infrastructure.test.interceptor;

import com.emily.infrastructure.datasource.interceptor.DataSourceCustomizer;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @program: spring-parent
 * @description: 数据库拦截器i
 * @author: Emily
 * @create: 2021/11/23
 */
@Component
@Order(value = 9090)
public class DbMethodInterceptor implements DataSourceCustomizer {
    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        System.out.println("自定义数据库拦截器");
        return "asdf";
    }
}
