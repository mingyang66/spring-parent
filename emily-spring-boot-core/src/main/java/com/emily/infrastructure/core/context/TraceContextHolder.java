package com.emily.infrastructure.core.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.emily.infrastructure.common.constant.HeaderInfo;
import com.emily.infrastructure.common.utils.RequestUtils;

import java.util.Objects;
import java.util.UUID;

/**
 * @Description: 全链路追踪上下文
 * @Author: Emily
 * @create: 2021/10/12
 */
public class TraceContextHolder {

    private static final ThreadLocal<RequestHolder> CONTEXT = new TransmittableThreadLocal<>();

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

    /**
     * 非容器上下文移除
     */
    public static void removeNoServletContext() {
        if (!RequestUtils.isServletContext()) {
            CONTEXT.remove();
        }
    }

    public static class RequestHolder {
        /**
         * 事务唯一编号
         */
        private String traceId;
        /**
         * 系统编号|标识
         */
        private String systemNumber;
        /**
         * 语言
         */
        private String language;
        /**
         * 开启时间
         */
        private Long startTime;
        /**
         * 客户端IP
         */
        private String clientIp;
        /**
         * 服务端IP
         */
        private String serverIp;


        public RequestHolder() {
            if (RequestUtils.isServletContext()) {
                this.traceId = RequestUtils.getRequest().getHeader(HeaderInfo.TRACE_ID);
            }
            if (Objects.isNull(traceId)) {
                this.traceId = UUID.randomUUID().toString();
            }
            this.startTime = System.currentTimeMillis();
        }

        public String getTraceId() {
            return traceId;
        }

        public void setTraceId(String traceId) {
            this.traceId = traceId;
        }

        public Long getStartTime() {
            if (Objects.isNull(startTime)) {
                startTime = System.currentTimeMillis();
            }
            return startTime;
        }

        public void setStartTime(Long startTime) {
            this.startTime = startTime;
        }

        public String getClientIp() {
            return clientIp;
        }

        public void setClientIp(String clientIp) {
            this.clientIp = clientIp;
        }

        public String getServerIp() {
            return serverIp;
        }

        public void setServerIp(String serverIp) {
            this.serverIp = serverIp;
        }

        public String getSystemNumber() {
            return systemNumber;
        }

        public void setSystemNumber(String systemNumber) {
            this.systemNumber = systemNumber;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }
}
