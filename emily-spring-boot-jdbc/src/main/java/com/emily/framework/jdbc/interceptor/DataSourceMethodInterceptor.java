package com.emily.framework.jdbc.interceptor;

import com.emily.framework.jdbc.annotation.TargetDataSource;
import com.emily.framework.jdbc.datasource.DataSourceContextHolder;
import com.emily.framework.common.logger.LoggerUtils;
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
    /**
     * 换行符
     */
    private static final String NEW_LINE = "\n";
    /**
     * 控制器
     */
    private static final String MSG_CONTROLLER = "类|方法 ：";
    /**
     * START消息
     */
    private static final String MSG_DATASOURCE_START = "开始执行，切换数据源到【";
    /**
     * END消息
     */
    private static final String MSG_DATASOURCE_END = "执行结束，移除数据源【";
    /**
     * 中文右符号
     */
    private static final String MSG_RIGHT_SYMBOL = "】";


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        //数据源切换开始
        TargetDataSource targetDataSource = method.getAnnotation(TargetDataSource.class);
        //获取注解标注的数据源
        String dataSource = targetDataSource.value();
        //判断当前的数据源是否已经被加载进入到系统当中去
        if (!DataSourceContextHolder.isExist(dataSource)) {
            throw new NullPointerException(StringUtils.join("数据源查找键（Look up key）【", dataSource, "】不存在"));
        }
        try {
            LoggerUtils.info(invocation.getThis().getClass(), StringUtils.join(MSG_CONTROLLER, invocation.getThis().getClass(), ".", method.getName(), MSG_DATASOURCE_START, dataSource, MSG_RIGHT_SYMBOL, NEW_LINE));
            //切换到指定的数据源
            DataSourceContextHolder.setDataSource(dataSource);
            //调用TargetDataSource标记的切换数据源方法
            Object result = invocation.proceed();
            //移除当前线程对应的数据源
            DataSourceContextHolder.remove();
            LoggerUtils.info(invocation.getClass(), StringUtils.join(MSG_CONTROLLER, invocation.getThis().getClass(), ".", method.getName(), MSG_DATASOURCE_END, dataSource, MSG_RIGHT_SYMBOL, NEW_LINE));

            return result;
        } catch (Throwable e) {
            //移除当前线程对应的数据源
            DataSourceContextHolder.remove();
            String log = StringUtils.join(MSG_CONTROLLER, invocation.getThis().getClass(), ".", method.getName(), MSG_DATASOURCE_END, dataSource, MSG_RIGHT_SYMBOL, ",第", e.getStackTrace()[0].getLineNumber(), "行发生", e.toString(), NEW_LINE);
            LoggerUtils.error(invocation.getThis().getClass(), log);
            throw e;
        }
    }

}
