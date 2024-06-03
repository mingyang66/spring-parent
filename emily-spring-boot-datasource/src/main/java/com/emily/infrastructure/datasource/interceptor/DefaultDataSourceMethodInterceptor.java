package com.emily.infrastructure.datasource.interceptor;

import com.emily.infrastructure.common.ObjectUtils;
import com.emily.infrastructure.core.constant.AopOrderInfo;
import com.emily.infrastructure.common.PrintExceptionUtils;
import com.emily.infrastructure.datasource.DataSourceProperties;
import com.emily.infrastructure.datasource.annotation.TargetDataSource;
import com.emily.infrastructure.datasource.context.DataSourceContextHolder;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;

/**
 * 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 *
 * @author Emily
 * @since 4.0.8
 */
public class DefaultDataSourceMethodInterceptor implements DataSourceCustomizer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDataSourceMethodInterceptor.class);

    private final DataSourceProperties properties;

    public DefaultDataSourceMethodInterceptor(DataSourceProperties properties) {
        this.properties = properties;
    }

    /**
     * 拦截器执行前置方法
     *
     * @return 调用数据的数据标识
     */
    @Override
    public String before(Method method) {
        //数据源切换开始
        TargetDataSource targetDataSource;
        if (method.isAnnotationPresent(TargetDataSource.class)) {
            //获取当前类的方法上标注的注解对象
            targetDataSource = method.getAnnotation(TargetDataSource.class);
        } else {
            //返回方法声明所在类或接口的Class对象
            targetDataSource = method.getDeclaringClass().getAnnotation(TargetDataSource.class);
        }
        if (targetDataSource == null && this.properties.isCheckInherited()) {
            //返回当前类或父类或接口方法上标注的注解对象
            targetDataSource = AnnotatedElementUtils.findMergedAnnotation(method, TargetDataSource.class);
        }
        if (targetDataSource == null && this.properties.isCheckInherited()) {
            //返回当前类或父类或接口上标注的注解对象
            targetDataSource = AnnotatedElementUtils.findMergedAnnotation(method.getDeclaringClass(), TargetDataSource.class);
        }
        if (ObjectUtils.isEmpty(targetDataSource.value())) {
            return properties.getDefaultConfig();
        }
        //获取注解标注的数据源
        return targetDataSource.value();
    }

    /**
     * 数据库连接池拦截方法
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        try {
            //获取数据源标识
            String dataSource = this.before(method);
            //解析查数据源标识为真实的查找键
            String lookupKey = this.resolveSpecifiedLookupKey(invocation, dataSource);
            //切换到指定的数据源
            DataSourceContextHolder.bind(lookupKey);
            //调用TargetDataSource标记的切换数据源方法
            return invocation.proceed();
        } catch (Throwable ex) {
            logger.error(PrintExceptionUtils.printErrorInfo(ex));
            throw ex;
        } finally {
            this.after(method);
        }
    }

    /**
     * 调用数据库操作完成后执行，移除当前线程值变量
     */
    @Override
    public void after(Method method) {
        //移除当前线程对应的数据源
        DataSourceContextHolder.unbind();
    }

    @Override
    public int getOrder() {
        return AopOrderInfo.DATASOURCE_INTERCEPTOR;
    }
}
