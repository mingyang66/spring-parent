package com.sgrain.boot.common.exception;


import com.sgrain.boot.common.enums.AppHttpStatus;

/**
 * @Description: 业务异常
 * @Version: 1.0
 */
public class BusinessException extends RuntimeException{
    /**
     * 状态码
     */
    private int status;
    /**
     * 异常信息
     */
    private String errorMessage;

    public BusinessException(AppHttpStatus httpStatus){
        this.status = httpStatus.getStatus();
        this.errorMessage = httpStatus.getMessage();
    }

    public BusinessException(int status, String errorMessage){
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
