package com.emily.infrastructure.cloud.feign.context;


import com.emily.infrastructure.core.entity.BaseLoggerBuilder;
import org.springframework.core.NamedThreadLocal;

/**
 * Feign上下文持有对象
 *
 * @author Emily
 * @since 2021/09/27
 */
public class FeignContextHolder {
    private static final ThreadLocal<BaseLoggerBuilder> CONTEXT = new NamedThreadLocal<>("Feign Logger Context");

    /**
     * 设置当前线程持有的数据源
     *
     * @param builder 日志构建对象
     */
    public static void bind(BaseLoggerBuilder builder) {
        CONTEXT.set(builder);
    }

    /**
     * 获取当前线程持有的数据源
     *
     * @return 日志对象
     */
    public static BaseLoggerBuilder current() {
        return CONTEXT.get();
    }

    /**
     * 删除当前线程持有的数据源
     */
    public static void unbind() {
        CONTEXT.remove();
    }

}
