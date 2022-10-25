package com.emily.infrastructure.core.context.holder;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.emily.infrastructure.common.constant.AttributeInfo;
import com.emily.infrastructure.common.constant.HeaderInfo;
import com.emily.infrastructure.common.utils.RequestUtils;
import com.emily.infrastructure.common.utils.UUIDUtils;
import com.emily.infrastructure.core.helper.SystemNumberHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @Description: 全链路追踪上下文
 * @Author: Emily
 * @create: 2021/10/12
 */
public class ThreadContextHolder {

    private static final ThreadLocal<RequestHolder> CONTEXT = new TransmittableThreadLocal<>() {
        @Override
        protected RequestHolder initialValue() {
            return new RequestHolder();
        }
    };

    /**
     * 设置当前线程持有的数据源
     */
    public static void bind(RequestHolder requestHolder) {
        CONTEXT.set(requestHolder);
    }

    /**
     * 获取当前线程持有的数据源
     */
    public static RequestHolder peek() {
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
         * 版本类型，com.emily.android
         */
        private String appType;
        /**
         * 版本号，4.1.4
         */
        private String appVersion;
        /**
         * (逻辑)是否servlet容器上下文，默认：false
         */
        private boolean servletContext;

        public RequestHolder() {
            //servlet请求开始时间
            this.startTime = System.currentTimeMillis();
            //系统编号
            this.systemNumber = SystemNumberHelper.getSystemNumber();
            //客户端IP
            this.clientIp = RequestUtils.getClientIp();
            //服务端IP
            this.serverIp = RequestUtils.getServerIp();
            //判定是否是servlet请求上下文
            if (RequestUtils.isServletContext()) {
                HttpServletRequest request = RequestUtils.getRequest();
                this.traceId = request.getHeader(HeaderInfo.TRACE_ID);
                this.appType = request.getHeader(HeaderInfo.APP_TYPE);
                this.appVersion = request.getHeader(HeaderInfo.APP_VERSION);
                this.servletContext = true;
                //设置当前请求阶段标识
                request.setAttribute(AttributeInfo.STAGE, Stage.REQUEST);
            }
            //事务流水号
            this.traceId = (traceId == null) ? UUIDUtils.randomSimpleUUID() : traceId;
        }

        public String getAppType() {
            return appType;
        }

        public void setAppType(String appType) {
            this.appType = appType;
        }

        public String getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(String appVersion) {
            this.appVersion = appVersion;
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
