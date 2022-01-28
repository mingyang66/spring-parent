package com.emily.infrastructure.test.interceptor;

import com.emily.infrastructure.datasource.interceptor.DataSourceCustomizer;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * @program: spring-parent
 * @description: 数据库拦截器i
 * @author: Emily
 * @create: 2021/11/23
 */
//@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
//@Component
//@Order(value = 9090)
public class DbMethodInterceptor implements DataSourceCustomizer {
    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        System.out.println("自定义数据库拦截器");
        return "asdf";
    }

    @Override
    public String getTargetDataSource(Method method) {
        return null;
    }
}
