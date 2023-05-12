package com.emily.infrastructure.core.exception;

/**
 * @program: spring-parent
 * @description: 业务异常
 * @author: Emily
 * @create: 2021/10/12
 */
public class BusinessException extends BasicException {
    public BusinessException() {
        super(HttpStatusType.EXCEPTION);
    }

    public BusinessException(HttpStatusType httpStatus) {
        super(httpStatus);
    }

    public BusinessException(int status, String errorMessage) {
        super(status, errorMessage);
    }

    public BusinessException(int status, String errorMessage, boolean error) {
        super(status, errorMessage, error);
    }
}
