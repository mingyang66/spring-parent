package com.emily.infrastructure.datasource.context;

import org.springframework.core.NamedThreadLocal;

/**
 * 线程持有数据源上线文
 *
 * @author Emily
 * @since 1.0
 */
public class DataSourceContextHolder {
    /**
     * 当前线程对应的数据源
     */
    private static final ThreadLocal<String> CONTEXT = new NamedThreadLocal<>("Multi data source switching context");

    /**
     * 设置当前线程持有的数据源
     *
     * @param dataSource 数据源标识
     */
    public static void bind(String dataSource) {
        CONTEXT.set(dataSource);
    }

    /**
     * 获取当前线程持有的数据源
     *
     * @return 数据源标识
     */
    public static String current() {
        return CONTEXT.get();
    }

    /**
     * 删除当前线程持有的数据源
     */
    public static void unbind() {
        CONTEXT.remove();
    }

}
