package com.emily.infrastructure.web.exception.entity;

import com.emily.infrastructure.web.exception.helper.MessageHelper;
import com.emily.infrastructure.web.response.enums.ApplicationStatus;

/**
 * 业务异常
 *
 * @author Emily
 * @since 2021/10/12
 */
public class BusinessException extends BasicException {
    public BusinessException() {
        super(ApplicationStatus.EXCEPTION);
        this.setMessage(MessageHelper.getMessage(ApplicationStatus.EXCEPTION.getMessage()));
    }

    public BusinessException(ApplicationStatus applicationStatus) {
        super(applicationStatus);
        this.setMessage(MessageHelper.getMessage(applicationStatus.getMessage()));
    }

    public BusinessException(int status, String message) {
        super(status, message);
        this.setMessage(MessageHelper.getMessage(message));
    }

    public BusinessException(int status, String message, boolean error, Object... args) {
        super(status, message, error);
        this.setMessage(MessageHelper.getMessage(message, args));
    }
}
