package com.emily.infrastructure.web.exception.entity;

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
    }

    public BusinessException(ApplicationStatus httpStatus) {
        super(httpStatus);
    }

    public BusinessException(int status, String errorMessage) {
        super(status, errorMessage);
    }

    public BusinessException(int status, String errorMessage, boolean error) {
        super(status, errorMessage, error);
    }
}
