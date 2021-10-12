package com.emily.infrastructure.core.holder;

import com.emily.infrastructure.common.utils.RequestUtils;

import java.util.Objects;
import java.util.UUID;

/**
 * @Description: 线程上下文持有对象
 * @Author: Emily
 * @create: 2021/10/12
 */
public class ContextHolder {

    private static final ThreadLocal<RequestHolder> CONTEXT = new ThreadLocal<>();

    /**
     * 设置当前线程持有的数据源
     */
    public static void set(RequestHolder requestHolder) {
        CONTEXT.set(requestHolder);
    }

    /**
     * 获取当前线程持有的数据源
     */
    public static RequestHolder get() {
        RequestHolder holder = CONTEXT.get();
        if (Objects.isNull(holder)) {
            CONTEXT.set(new RequestHolder());
        }
        return CONTEXT.get();
    }

    /**
     * 删除当前线程持有的数据源
     */
    public static void remove() {
        CONTEXT.remove();
    }

    public static class RequestHolder {
        /**
         * 事务唯一编号
         */
        private String traceId;


        public RequestHolder() {
            if (RequestUtils.isServletContext()) {
                this.traceId = RequestUtils.getRequest().getHeader("traceId");
            }
            if (Objects.isNull(traceId)) {
                this.traceId = UUID.randomUUID().toString();
            }
        }

        public String getTraceId() {
            return traceId;
        }

    }
}
