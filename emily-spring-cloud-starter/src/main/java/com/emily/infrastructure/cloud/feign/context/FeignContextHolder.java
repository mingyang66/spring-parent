package com.emily.infrastructure.cloud.feign.context;


import com.emily.infrastructure.core.entity.BaseLoggerBuilder;
import org.springframework.core.NamedThreadLocal;

/**
 * @program: spring-parent
 * @description: Feign上下文持有对象
 * @author: Emily
 * @create: 2021/09/27
 */
public class FeignContextHolder {
    private static final ThreadLocal<BaseLoggerBuilder> CONTEXT = new NamedThreadLocal<>("Feign Logger Context");

    /**
     * 设置当前线程持有的数据源
     */
    public static void bind(BaseLoggerBuilder builder) {
        CONTEXT.set(builder);
    }

    /**
     * 获取当前线程持有的数据源
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
