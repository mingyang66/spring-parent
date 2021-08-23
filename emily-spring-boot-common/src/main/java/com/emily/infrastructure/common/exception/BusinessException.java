package com.emily.infrastructure.common.exception;


import com.emily.infrastructure.common.enums.AppHttpStatus;

/**
 * @author Emily
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
    private String message;

    public BusinessException(AppHttpStatus httpStatus){
        super(httpStatus.getMessage());
        this.status = httpStatus.getStatus();
        this.message = httpStatus.getMessage();
    }

    public BusinessException(int status, String errorMessage){
        super(errorMessage);
        this.status = status;
        this.message = errorMessage;
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
}
