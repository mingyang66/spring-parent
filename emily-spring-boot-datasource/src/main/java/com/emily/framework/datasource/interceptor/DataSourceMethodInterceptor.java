package com.emily.framework.datasource.interceptor;

import com.emily.framework.common.exception.PrintExceptionInfo;
import com.emily.framework.datasource.DataSourceProperties;
import com.emily.framework.datasource.annotation.TargetDataSource;
import com.emily.framework.datasource.context.DataSourceContextHolder;
import com.emily.framework.autoconfigure.logger.common.LoggerUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Author Emily
 * @Version: 1.0
 */
public class DataSourceMethodInterceptor implements MethodInterceptor {

    private DataSourceProperties dataSourceProperties;

    public DataSourceMethodInterceptor(DataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        //数据源切换开始
        TargetDataSource targetDataSource = method.getAnnotation(TargetDataSource.class);
        //获取注解标注的数据源
        String dataSource = targetDataSource.value();
        //判断当前的数据源是否已经被加载进入到系统当中去
        if (!dataSourceProperties.getConfig().containsKey(dataSource)) {
            throw new NullPointerException(String.format("数据源配置【%s】不存在", dataSource));
        }
        try {
            LoggerUtils.info(method.getDeclaringClass(), StringUtils.join("==>", method.getDeclaringClass().getName(), ".", method.getName(), String.format("==> ========开始执行，切换数据源到【%s】========", dataSource)));
            //切换到指定的数据源
            DataSourceContextHolder.setDataSourceLookup(dataSource);
            //调用TargetDataSource标记的切换数据源方法
            Object result = invocation.proceed();
            return result;
        } catch (Throwable ex) {
            LoggerUtils.error(invocation.getThis().getClass(), String.format("==> ========异常执行，数据源【%s】 ========" + PrintExceptionInfo.printErrorInfo(ex), dataSource));
            throw ex;
        } finally {
            //移除当前线程对应的数据源
            DataSourceContextHolder.clearDataSource();
            LoggerUtils.info(method.getDeclaringClass(), StringUtils.join("<==", method.getDeclaringClass().getName(), ".", method.getName(), String.format("========结束执行，清除数据源【%s】========", dataSource)));

        }
    }

}
