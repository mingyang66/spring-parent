package com.emily.infrastructure.sample.web.config.interceptor;

import com.emily.infrastructure.aop.constant.AopOrderInfo;
import com.emily.infrastructure.datasource.interceptor.DataSourceCustomizer;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Role;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * @author Emily
 * @program: spring-parent
 * 数据库拦截器i
 * @since 2021/11/23
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
//@Component
public class DbMethodInterceptor implements DataSourceCustomizer {
    @Override
    public String before(Method method) {
        return null;
    }

    @Override
    public void after(Method method) {

    }

    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        System.out.println("自定义数据库拦截器");
        return "asdf";
    }


    @Override
    public int getOrder() {
        return AopOrderInfo.DATASOURCE_INTERCEPTOR + 1;
    }
}
