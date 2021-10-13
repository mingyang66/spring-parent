package com.emily.infrastructure.cloud.feign.context;


import com.emily.infrastructure.core.entity.BaseLogger;

/**
 * @program: spring-parent
 * @description: Feign上下文持有对象
 * @author: Emily
 * @create: 2021/09/27
 */
public class FeignContextHolder {
    private static final ThreadLocal<BaseLogger> CONTEXT = new ThreadLocal<>();

    /**
     * 设置当前线程持有的数据源
     */
    public static void set(BaseLogger baseLogger) {
        CONTEXT.set(baseLogger);
    }

    /**
     * 获取当前线程持有的数据源
     */
    public static BaseLogger get() {
        return CONTEXT.get();
    }

    /**
     * 删除当前线程持有的数据源
     */
    public static void remove() {
        CONTEXT.remove();
    }

}
