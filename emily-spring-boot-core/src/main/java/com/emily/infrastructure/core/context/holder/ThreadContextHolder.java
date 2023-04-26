package com.emily.infrastructure.core.context.holder;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * @Description: 全链路追踪上下文
 * @Author: Emily
 * @create: 2021/10/12
 */
public class ThreadContextHolder {

    private static final ThreadLocal<ContextHolder> CONTEXT = new TransmittableThreadLocal<>() {
        @Override
        protected ContextHolder initialValue() {
            return new ContextHolder();
        }
    };

    /**
     * 设置当前线程持有的数据源
     */
    public static void bind(ContextHolder ContextHolder) {
        CONTEXT.set(ContextHolder);
    }

    /**
     * 获取当前线程持有的数据源
     */
    public static ContextHolder current() {
        return CONTEXT.get();
    }

    /**
     * 是否移除上下文
     */
    public static void unbind(boolean flag) {
        if (flag) {
            CONTEXT.remove();
        }
    }

    /**
     * 删除当前线程持有的数据源
     */
    public static void unbind() {
        if (!CONTEXT.get().isServlet()) {
            CONTEXT.remove();
        }
    }
}
