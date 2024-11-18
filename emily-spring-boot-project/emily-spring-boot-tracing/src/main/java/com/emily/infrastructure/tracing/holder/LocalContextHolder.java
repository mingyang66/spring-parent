package com.emily.infrastructure.tracing.holder;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 全链路追踪上下文
 *
 * @author Emily
 * @since 2021/10/12
 */
public class LocalContextHolder {

    private static final ThreadLocal<TracingHolder> CONTEXT = new TransmittableThreadLocal<>() {
        @Override
        protected TracingHolder initialValue() {
            return TracingHolder.newBuilder().build();
        }

        /**
         * 将子线程的初始上下文值设置为null
         * ----------------------------------------------------------------------
         * 关闭父子线程之间的继承关系，为什么要关闭继承关系？
         * 1. 在线程池的场景下会触发父线程已经remove掉上下文值，子线程还持有从父线程继承的上下文值，子线程结束后会将线程归还给线程池，归还后线程有可能会被复用，
         * 这样就可能会导致一部分值一直无法被GC收回，如果复用的数量过多可能导致OOM，而且还有可能导致其它线程拿到了本部署当前线程的数据；
         * ----------------------------------------------------------------------
         * @param parentValue 父线程的值对象
         * @return 子线程的初始值对象
         * @see <a href="https://github.com/alibaba/transmittable-thread-local/issues/521">...</a>
         */
        @Override
        protected TracingHolder childValue(TracingHolder parentValue) {
            //调用父类的初始化方法可以确保子类初始化为null
            return super.initialValue();
        }
    };

    /**
     * 设置当前线程持有的数据源
     *
     * @param holder 上下文对象
     */
    public static void bind(TracingHolder holder) {
        CONTEXT.set(holder);
    }

    /**
     * 获取当前线程持有的数据源
     *
     * @return 上下文对象
     */
    public static TracingHolder current() {
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
