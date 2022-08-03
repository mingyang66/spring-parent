package com.emily.infrastructure.autoconfigure.httpclient.context;

import org.apache.http.client.config.RequestConfig;
import org.springframework.core.NamedThreadLocal;

/**
 * @Description :  Http进程执行状态上下文对象
 * @Author :  Emily
 * @CreateDate :  Created in 2022/8/2 5:11 下午
 * @since 4.1.3
 */
public class HttpContextHolder {

    private static final ThreadLocal<RequestConfig> threadLocal = new NamedThreadLocal<>("HTTP进程执行状态上下文");

    public static void bind(RequestConfig requestConfig) {
        threadLocal.set(requestConfig);
    }

    public static RequestConfig peek() {
        return threadLocal.get();
    }

    public static void unbind() {
        threadLocal.remove();
    }
}
