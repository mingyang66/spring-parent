package com.emily.infrastructure.tracing.holder;

import com.emily.infrastructure.common.StringUtils;
import com.emily.infrastructure.common.UUIDUtils;
import com.emily.infrastructure.common.constant.HeaderInfo;
import com.emily.infrastructure.tracing.helper.SystemNumberHelper;
import com.otter.infrastructure.servlet.RequestUtils;

import java.time.Instant;
import java.util.Objects;

/**
 * 上下文实体类
 *
 * @author Emily
 * @since Created in 2023/4/22 3:51 PM
 */
public class ContextHolder {
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
    private Instant startTime;
    /**
     * API接口耗时
     */
    private long spentTime;
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
    private boolean servlet;
    /**
     * 当前上下文所处阶段标识
     */
    private ServletStage servletStage;

    public static Builder newBuilder() {
        return new Builder();
    }

    public long getSpentTime() {
        return spentTime;
    }

    public void setSpentTime(long spentTime) {
        this.spentTime = spentTime;
    }

    public ServletStage getServletStage() {
        return servletStage;
    }

    public void setServletStage(ServletStage servletStage) {
        this.servletStage = servletStage;
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

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
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

    public boolean isServlet() {
        return servlet;
    }

    public void setServlet(boolean servlet) {
        this.servlet = servlet;
    }

    public static class Builder {
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
        private Instant startTime;
        /**
         * API接口耗时
         */
        private long spentTime;
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
        private boolean servlet;
        /**
         * 当前上下文所处阶段标识
         */
        private ServletStage servletStage;

        public Builder withTraceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder withSystemNumber(String systemNumber) {
            this.systemNumber = systemNumber;
            return this;
        }

        public Builder withLanguage(String language) {
            this.language = language;
            return this;
        }

        public Builder withStartTime(Instant startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder withClientIp(String clientIp) {
            this.clientIp = clientIp;
            return this;
        }

        public Builder withServerIp(String serverIp) {
            this.serverIp = serverIp;
            return this;
        }

        public Builder withAppType(String appType) {
            this.appType = appType;
            return this;
        }

        public Builder withAppVersion(String appVersion) {
            this.appVersion = appVersion;
            return this;
        }

        public Builder withServlet(boolean servlet) {
            this.servlet = servlet;
            return this;
        }

        public Builder withServletStage(ServletStage servletStage) {
            this.servletStage = servletStage;
            return this;
        }

        public Builder withSpentTime(long spentTime) {
            this.spentTime = spentTime;
            return this;
        }

        public ContextHolder build() {
            ContextHolder holder = new ContextHolder();
            //事务流水号
            if (RequestUtils.isServlet()) {
                this.traceId = RequestUtils.getHeader(HeaderInfo.TRACE_ID);
                this.servlet = true;
            }
            holder.setTraceId(StringUtils.isBlank(this.traceId) ? UUIDUtils.randomSimpleUUID() : this.traceId);
            //系统编号
            holder.setSystemNumber(StringUtils.isBlank(systemNumber) ? SystemNumberHelper.getSystemNumber() : systemNumber);
            //servlet上下文
            holder.setServlet(servlet);
            //语言
            holder.setLanguage(StringUtils.isBlank(language) ? RequestUtils.getHeader(HeaderInfo.LANGUAGE) : language);
            //版本类型，com.emily.android
            holder.setAppType(StringUtils.isBlank(appType) ? RequestUtils.getHeader(HeaderInfo.APP_TYPE) : appType);
            //版本号，4.1.4
            holder.setAppVersion(StringUtils.isBlank(appVersion) ? RequestUtils.getHeader(HeaderInfo.APP_VERSION) : appVersion);
            //servlet请求开始时间
            holder.setStartTime(Objects.isNull(startTime) ? Instant.now() : startTime);
            //API耗时
            holder.setSpentTime(spentTime);
            //客户端IP
            holder.setClientIp(RequestUtils.getClientIp());
            //服务端IP
            holder.setServerIp(RequestUtils.getServerIp());
            //设置当前请求阶段标识
            holder.setServletStage(Objects.isNull(servletStage) ? ServletStage.OTHER : servletStage);
            return holder;
        }
    }
}
