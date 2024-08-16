package com.emily.infrastructure.web.exception.entity;


import com.emily.infrastructure.web.exception.type.AppStatusType;

/**
 * 业务异常
 *
 * @author Emily
 * @since 1.0
 */
public class BasicException extends RuntimeException {
    /**
     * 状态码
     */
    private int status;
    /**
     * 异常信息
     */
    private String message;
    /**
     * 是否是错误信息，默认：true
     */
    private boolean error = true;

    public BasicException() {
    }

    public BasicException(AppStatusType httpStatus) {
        super(httpStatus.getMessage());
        this.status = httpStatus.getStatus();
        this.message = httpStatus.getMessage();
    }

    public BasicException(int status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public BasicException(int status, String errorMessage, boolean error) {
        this(status, errorMessage);
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
