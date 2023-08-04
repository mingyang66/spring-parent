package com.emily.infrastructure.core.context.holder;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 全链路追踪上下文
 *
 * @author Emily
 * @since 2021/10/12
 */
public class LocalContextHolder {

    private static final ThreadLocal<ContextHolder> CONTEXT = new TransmittableThreadLocal<>() {
        @Override
        protected ContextHolder initialValue() {
            return new ContextHolder();
        }
    };

    /**
     * 设置当前线程持有的数据源
     *
     * @param ContextHolder 上下文对象
     */
    public static void bind(ContextHolder ContextHolder) {
        CONTEXT.set(ContextHolder);
    }

    /**
     * 获取当前线程持有的数据源
     *
     * @return 上下文对象
     */
    public static ContextHolder current() {
        return CONTEXT.get();
    }

    /**
     * 是否移除上下文中文存储的值
     *
     * @param servlet 是否servlet上下文
     */
    public static void unbind(boolean servlet) {
        if (servlet) {
            CONTEXT.remove();
        }
    }

    /**
     * 如果当前上下文是非servlet上下文场景才会移除上下文中存储的数据
     */
    public static void unbind() {
        if (!current().isServlet()) {
            CONTEXT.remove();
        }
    }
}
