package com.emily.infrastructure.web.exception.entity;

import com.emily.infrastructure.common.StringUtils;
import com.emily.infrastructure.common.constant.HeaderInfo;
import com.emily.infrastructure.language.i18n.LanguageType;
import com.emily.infrastructure.language.i18n.registry.I18nSimpleRegistry;
import com.emily.infrastructure.web.response.enums.ApplicationStatus;
import com.otter.infrastructure.servlet.RequestUtils;

import java.text.MessageFormat;

/**
 * 业务异常
 *
 * @author Emily
 * @since 2021/10/12
 */
public class RemoteInvokeException extends BasicException {
    public RemoteInvokeException() {
        super(ApplicationStatus.EXCEPTION);
        this.setMessage(getMsg(ApplicationStatus.EXCEPTION.getMessage()));
    }

    public RemoteInvokeException(ApplicationStatus applicationStatus) {
        super(applicationStatus);
        this.setMessage(getMsg(applicationStatus.getMessage()));
    }

    public RemoteInvokeException(int status, String message) {
        super(status, message);
        this.setMessage(getMsg(message));
    }

    public RemoteInvokeException(int status, String message, boolean error, Object... args) {
        super(status, message, error);
        this.setMessage(getMsg(message, args));
    }

    private String getMsg(String message, Object... args) {
        String msg = I18nSimpleRegistry.acquire(message, RequestUtils.getHeaderOrDefault(HeaderInfo.LANGUAGE, LanguageType.ZH_CN.getCode()));
        if (StringUtils.isNotBlank(msg) && args.length > 0) {
            return MessageFormat.format(msg, args);
        }
        return msg;
    }
}
