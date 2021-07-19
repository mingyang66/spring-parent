package com.emily.infrastructure.common.exception;


import com.emily.infrastructure.common.enums.AppHttpStatus;

/**
 * @author Emily
 * @Description: 业务异常
 * @Version: 1.0
 */
public class SystemException extends RuntimeException{
    /**
     * 状态码
     */
    private int status;
    /**
     * 异常信息
     */
    private String errorMessage;

    public SystemException(AppHttpStatus httpStatus){
        super(httpStatus.getMessage());
        this.status = httpStatus.getStatus();
        this.errorMessage = httpStatus.getMessage();
    }

    public SystemException(int status, String errorMessage){
        super(errorMessage);
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
