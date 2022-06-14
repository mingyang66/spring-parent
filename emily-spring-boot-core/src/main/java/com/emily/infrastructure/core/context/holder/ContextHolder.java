package com.emily.infrastructure.core.context.holder;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.emily.infrastructure.common.constant.HeaderInfo;
import com.emily.infrastructure.common.utils.RequestUtils;
import com.emily.infrastructure.core.helper.SystemNumberHelper;

import java.util.Objects;
import java.util.UUID;

/**
 * @Description: 全链路追踪上下文
 * @Author: Emily
 * @create: 2021/10/12
 */
public class ContextHolder {

    private static final ThreadLocal<RequestHolder> CONTEXT = new TransmittableThreadLocal<>() {
        @Override
        protected RequestHolder initialValue() {
            return new RequestHolder();
        }
    };

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
        return CONTEXT.get();
    }

    /**
     * 删除当前线程持有的数据源
     */
    public static void remove() {
        if (!CONTEXT.get().isServletContext()) {
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
        /**
         * (逻辑)是否servlet容器上下文，默认：false
         */
        private boolean servletContext;

        public RequestHolder() {
            this.startTime = System.currentTimeMillis();
            this.systemNumber = SystemNumberHelper.getSystemNumber();
            this.clientIp = RequestUtils.getClientIp();
            this.serverIp = RequestUtils.getServerIp();
            if (RequestUtils.isServletContext()) {
                this.traceId = RequestUtils.getRequest().getHeader(HeaderInfo.TRACE_ID);
                this.servletContext = true;
            }
            if (Objects.isNull(traceId)) {
                this.traceId = UUID.randomUUID().toString();
            }
        }

        public String getTraceId() {
            return traceId;
        }

        public void setTraceId(String traceId) {
            this.traceId = traceId;
        }

        public Long getStartTime() {
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

        public boolean isServletContext() {
            return servletContext;
        }

        public void setServletContext(boolean servletContext) {
            this.servletContext = servletContext;
        }
    }

    /**
     * API请求阶段
     */
    public enum Stage {
        //RequestMappingHandlerMapping校验转发阶段
        MAPPING,
        //Request请求AOP拦截阶段
        REQUEST,
        //Feign请求阶段
        FEIGN,
        //RestTemplate请求阶段
        HTTP,
        //Mybatis日志记录
        MYBATIS,
        //其它阶段
        OTHER;
    }
}
