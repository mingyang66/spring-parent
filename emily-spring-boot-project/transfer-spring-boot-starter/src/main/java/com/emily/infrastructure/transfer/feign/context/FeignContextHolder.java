package com.emily.infrastructure.transfer.feign.context;


import com.emily.infrastructure.logback.entity.BaseLogger;
import org.springframework.core.NamedThreadLocal;

/**
 * Feign上下文持有对象
 *
 * @author Emily
 * @since 2021/09/27
 */
public class FeignContextHolder {
    private static final ThreadLocal<BaseLogger> CONTEXT = new NamedThreadLocal<>("Feign Logger Context");

    /**
     * 设置当前线程持有的数据源
     *
     * @param baseLogger 日志构建对象
     */
    public static void bind(BaseLogger baseLogger) {
        CONTEXT.set(baseLogger);
    }

    /**
     * 获取当前线程持有的数据源
     *
     * @return 日志对象
     */
    public static BaseLogger current() {
        return CONTEXT.get();
    }

    /**
     * 删除当前线程持有的数据源
     */
    public static void unbind() {
        CONTEXT.remove();
    }

}
