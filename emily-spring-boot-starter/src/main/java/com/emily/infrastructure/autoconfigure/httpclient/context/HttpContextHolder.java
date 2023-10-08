/*
package com.emily.infrastructure.autoconfigure.httpclient.context;

import org.apache.hc.client5.http.config.RequestConfig;
import org.springframework.core.NamedThreadLocal;

*/
/**
 * Http进程执行状态上下文对象
 *
 * @author Emily
 * @since 4.1.3
 *//*

public class HttpContextHolder {

    private static final ThreadLocal<RequestConfig> threadLocal = new NamedThreadLocal<>("HTTP进程执行状态上下文");

    public static void bind(RequestConfig requestConfig) {
        threadLocal.set(requestConfig);
    }

    public static RequestConfig current() {
        return threadLocal.get();
    }

    public static void unbind() {
        threadLocal.remove();
    }
}
*/
