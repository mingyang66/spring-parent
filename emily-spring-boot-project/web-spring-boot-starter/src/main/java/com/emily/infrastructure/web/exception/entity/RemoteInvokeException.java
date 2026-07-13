package com.emily.infrastructure.web.exception.entity;

import com.emily.infrastructure.web.exception.helper.MessageHelper;
import com.emily.infrastructure.web.response.enums.ApplicationStatus;

/**
 * 业务异常
 *
 * @author Emily
 * @since 2021/10/12
 */
public class RemoteInvokeException extends BasicException {
    public RemoteInvokeException() {
        super(ApplicationStatus.EXCEPTION);
        this.setMessage(MessageHelper.getMessage(ApplicationStatus.EXCEPTION.getMessage()));
    }

    public RemoteInvokeException(ApplicationStatus applicationStatus) {
        super(applicationStatus);
        this.setMessage(MessageHelper.getMessage(applicationStatus.getMessage()));
    }

    public RemoteInvokeException(int status, String message) {
        super(status, message);
        this.setMessage(MessageHelper.getMessage(message));
    }

    public RemoteInvokeException(int status, String message, boolean error, Object... args) {
        super(status, message, error);
        this.setMessage(MessageHelper.getMessage(message, args));
    }
}
