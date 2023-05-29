package com.emily.infrastructure.core.context.holder;

import com.emily.infrastructure.core.constant.AttributeInfo;
import com.emily.infrastructure.core.constant.HeaderInfo;
import com.emily.infrastructure.core.helper.UUIDUtils;
import com.emily.infrastructure.core.helper.RequestUtils;
import com.emily.infrastructure.core.helper.SystemNumberHelper;
import com.emily.infrastructure.language.convert.LanguageType;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

/**
 * @Description :  上下文实体类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/4/22 3:51 PM
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

    public ContextHolder() {
        //servlet请求开始时间
        this.startTime = Instant.now();
        //系统编号
        this.systemNumber = SystemNumberHelper.getSystemNumber();
        //客户端IP
        this.clientIp = RequestUtils.getClientIp();
        //服务端IP
        this.serverIp = RequestUtils.getServerIp();
        //servlet上下文
        this.servlet = RequestUtils.isServlet();
        //判定是否是servlet请求上下文
        if (servlet) {
            HttpServletRequest request = RequestUtils.getRequest();
            this.traceId = request.getHeader(HeaderInfo.TRACE_ID);
            this.appType = request.getHeader(HeaderInfo.APP_TYPE);
            this.appVersion = request.getHeader(HeaderInfo.APP_VERSION);
            this.languageType = LanguageType.getByCode(request.getHeader(HeaderInfo.LANGUAGE));
            //设置当前请求阶段标识
            request.setAttribute(AttributeInfo.STAGE, StageType.REQUEST);
        }
        //事务流水号
        this.traceId = StringUtils.isEmpty(traceId) ? UUIDUtils.randomSimpleUUID() : traceId;
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

    public LanguageType getLanguageType() {
        return languageType;
    }

    public void setLanguageType(LanguageType languageType) {
        this.languageType = languageType;
    }

    public boolean isServlet() {
        return servlet;
    }

    public void setServlet(boolean servlet) {
        this.servlet = servlet;
    }
}
