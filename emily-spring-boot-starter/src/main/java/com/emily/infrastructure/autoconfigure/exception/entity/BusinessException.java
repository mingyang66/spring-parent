package com.emily.infrastructure.autoconfigure.exception.entity;

import com.emily.infrastructure.autoconfigure.exception.type.AppStatusType;

/**
 * 业务异常
 *
 * @author Emily
 * @since 2021/10/12
 */
public class BusinessException extends BasicException {
    public BusinessException() {
        super(AppStatusType.EXCEPTION);
    }

    public BusinessException(AppStatusType httpStatus) {
        super(httpStatus);
    }

    public BusinessException(int status, String errorMessage) {
        super(status, errorMessage);
    }

    public BusinessException(int status, String errorMessage, boolean error) {
        super(status, errorMessage, error);
    }
}
