package com.emily.infrastructure.test.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @program: spring-parent
 *  响应数据
 * @author Emily
 * @since 2021/09/12
 */
public class ApiResponse {

    private String Status;
    private String Message;
    private String errorCode;

    @JsonProperty("Status")
    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    @JsonProperty("Message")
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
}
