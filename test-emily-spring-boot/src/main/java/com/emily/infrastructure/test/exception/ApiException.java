package com.emily.infrastructure.test.exception;

import com.emily.infrastructure.common.exception.CustomException;

/**
 * @program: spring-parent
 * @description: 自定义异常
 * @author: Emily
 * @create: 2021/09/12
 */
public class ApiException extends CustomException {
    private String Status;
    private String Message;
    private String errorCode;

    public ApiException(String status, String message, String errorCode) {
        this.Status = status;
        this.Message = message;
        this.errorCode = errorCode;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    @Override
    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public Object getBean() {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(this.getStatus());
        apiResponse.setMessage(this.getMessage());
        apiResponse.setErrorCode(this.getErrorCode());
        return apiResponse;
    }
}
