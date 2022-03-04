package com.emily.infrastructure.datasource.context;

/**
 * @Description: 线程持有数据源上线文
 * @Author Emily
 * @Version: 1.0
 */
public class DataSourceContextHolder {
    /**
     * 当前线程对应的数据源
     */
    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    /**
     * 设置当前线程持有的数据源
     */
    public static void set(String dataSource) {
        CONTEXT.set(dataSource);
    }

    /**
     * 获取当前线程持有的数据源
     */
    public static String get() {
        return CONTEXT.get();
    }

    /**
     * 删除当前线程持有的数据源
     */
    public static void remove() {
        CONTEXT.remove();
    }

}
