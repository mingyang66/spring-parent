package com.emily.infrastructure.datasource.interceptor;

import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.datasource.DataSourceProperties;
import com.emily.infrastructure.datasource.annotation.TargetDataSource;
import com.emily.infrastructure.datasource.context.DataSourceContextHolder;
import com.emily.infrastructure.datasource.exception.DataSourceNotFoundException;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.text.MessageFormat;

/**
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Author Emily
 * @Version: 1.0
 */
public class DataSourceMethodInterceptor implements DataSourceCustomizer {

    private DataSourceProperties dataSourceProperties;

    public DataSourceMethodInterceptor(DataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        //获取注解标注的数据源
        String dataSource = getTargetDataSource(method);
        //判断当前的数据源是否已经被加载进入到系统当中去
        if (!dataSourceProperties.getConfig().containsKey(dataSource)) {
            throw new DataSourceNotFoundException(MessageFormat.format("数据源配置【{0}】不存在", dataSource));
        }
        Logger logger = LoggerFactory.getLogger(method.getDeclaringClass());
        try {
            if (logger.isDebugEnabled()) {
                logger.debug(StringUtils.join("==> ", method.getDeclaringClass().getName(), ".", method.getName(), String.format("==> ========开始执行，切换数据源到【%s】========", dataSource)));
            }
            //切换到指定的数据源
            DataSourceContextHolder.setDataSourceLookup(dataSource);
            //调用TargetDataSource标记的切换数据源方法
            return invocation.proceed();
        } catch (Throwable ex) {
            LoggerFactory.getLogger(invocation.getThis().getClass()).error(String.format("<== ========异常执行，数据源【%s】 ========" + PrintExceptionInfo.printErrorInfo(ex), dataSource));
            throw ex;
        } finally {
            //移除当前线程对应的数据源
            DataSourceContextHolder.clearDataSource();
            if (logger.isDebugEnabled()) {
                logger.debug(StringUtils.join("<== ", method.getDeclaringClass().getName(), ".", method.getName(), String.format("<== ========结束执行，清除数据源【%s】========", dataSource)));
            }
        }
    }

    /**
     * 获取目标数据源标识
     *
     * @param method
     * @return
     */
    @Override
    public String getTargetDataSource(Method method) {
        //数据源切换开始
        TargetDataSource targetDataSource;
        if (method.isAnnotationPresent(TargetDataSource.class)) {
            targetDataSource = method.getAnnotation(TargetDataSource.class);
        } else {
            //  method.getDeclaringClass() 返回方法声明所在类或接口的Class对象
            targetDataSource = method.getDeclaringClass().getAnnotation(TargetDataSource.class);
        }
        //获取注解标注的数据源
        return targetDataSource.value();
    }
}
