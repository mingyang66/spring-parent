package com.emily.infrastructure.common.exception;


import com.emily.infrastructure.common.enums.AppHttpStatus;

/**
 * @author Emily
 * @Description: 业务异常
 * @Version: 1.0
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

    public BasicException(AppHttpStatus httpStatus) {
        super(httpStatus.getMessage());
        this.status = httpStatus.getStatus();
        this.message = httpStatus.getMessage();
    }

    public BasicException(int status, String errorMessage) {
        super(errorMessage);
        this.status = status;
        this.message = errorMessage;
    }

    public BasicException(int status, String errorMessage, boolean error) {
        super(errorMessage);
        this.status = status;
        this.message = errorMessage;
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
