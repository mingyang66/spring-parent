package com.emily.infrastructure.common.exception;

import com.emily.infrastructure.common.enums.AppHttpStatus;

/**
 * @program: spring-parent
 * @description: 业务异常
 * @author: Emily
 * @create: 2021/10/12
 */
public class BusinessException extends BasicException {
    public BusinessException() {
        super(AppHttpStatus.NETWORK_EXCEPTION);
    }

    public BusinessException(AppHttpStatus httpStatus) {
        super(httpStatus);
    }

    public BusinessException(int status, String errorMessage) {
        super(status, errorMessage);
    }

    public BusinessException(int status, String errorMessage, boolean error) {
        super(status, errorMessage, error);
    }
}
