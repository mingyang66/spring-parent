package com.emily.infrastructure.core.context.holder;

import com.emily.infrastructure.common.StringUtils;
import com.emily.infrastructure.common.UUIDUtils;
import com.emily.infrastructure.core.constant.AttributeInfo;
import com.emily.infrastructure.core.constant.HeaderInfo;
import com.emily.infrastructure.core.helper.RequestUtils;
import com.emily.infrastructure.core.helper.SystemNumberHelper;
import com.emily.infrastructure.language.convert.LanguageType;

import java.time.Instant;
import java.util.Objects;

/**
 * 上下文实体类建造器
 *
 * @author Emily
 * @since Created in 2023/4/22 3:51 PM
 */
public class ContextHolderBuilder {
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
    private LanguageType languageType;
    /**
     * 开启时间
     */
    private Instant startTime;
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

    public ContextHolderBuilder withTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public ContextHolderBuilder withSystemNumber(String systemNumber) {
        this.systemNumber = systemNumber;
        return this;
    }

    public ContextHolderBuilder withLanguageType(LanguageType languageType) {
        this.languageType = languageType;
        return this;
    }

    public ContextHolderBuilder withStartTime(Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    public ContextHolderBuilder withClientIp(String clientIp) {
        this.clientIp = clientIp;
        return this;
    }

    public ContextHolderBuilder withServerIp(String serverIp) {
        this.serverIp = serverIp;
        return this;
    }

    public ContextHolderBuilder withAppType(String appType) {
        this.appType = appType;
        return this;
    }

    public ContextHolderBuilder withAppVersion(String appVersion) {
        this.appVersion = appVersion;
        return this;
    }

    public ContextHolderBuilder withServlet(boolean servlet) {
        this.servlet = servlet;
        return this;
    }

    public ContextHolder build() {
        ContextHolder holder = new ContextHolder();
        //事务流水号
        holder.setTraceId(Objects.isNull(traceId) ? (RequestUtils.isServlet() ? StringUtils.defaultString(RequestUtils.getHeader(HeaderInfo.TRACE_ID), UUIDUtils.randomSimpleUUID()) : UUIDUtils.randomSimpleUUID()) : traceId);
        //系统编号
        holder.setSystemNumber(Objects.isNull(systemNumber) ? SystemNumberHelper.getSystemNumber() : systemNumber);
        //servlet上下文
        holder.setServlet(servlet ? true : RequestUtils.isServlet());
        //语言
        holder.setLanguageType(Objects.isNull(languageType) ? (RequestUtils.isServlet() ? LanguageType.getByCode(RequestUtils.getHeader(HeaderInfo.LANGUAGE)) : LanguageType.ZH_CN) : languageType);
        //版本类型，com.emily.android
        holder.setAppType(Objects.isNull(appType) ? (RequestUtils.isServlet() ? StringUtils.defaultString(RequestUtils.getHeader(HeaderInfo.APP_TYPE), appType) : appType) : appType);
        //版本号，4.1.4
        holder.setAppVersion(Objects.isNull(appVersion) ? (RequestUtils.isServlet() ? StringUtils.defaultString(RequestUtils.getHeader(HeaderInfo.APP_VERSION), appVersion) : appVersion) : appVersion);
        //servlet请求开始时间
        holder.setStartTime(Objects.isNull(startTime) ? Instant.now() : startTime);
        //客户端IP
        holder.setClientIp(Objects.isNull(clientIp) ? RequestUtils.getClientIp() : clientIp);
        //服务端IP
        holder.setServerIp(Objects.isNull(serverIp) ? RequestUtils.getServerIp() : serverIp);
        //设置当前请求阶段标识
        if (RequestUtils.isServlet()) {
            RequestUtils.getRequest().setAttribute(AttributeInfo.STAGE, StageType.REQUEST);
        }
        return holder;
    }

    public static ContextHolderBuilder create() {
        return new ContextHolderBuilder();
    }
}
