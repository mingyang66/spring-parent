package com.yaomy.control.common.control.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yaomy.control.common.control.enums.HttpStatus;

import java.io.Serializable;

/**
 * @Description 返回值工具类
 * @Version  1.0
 */
public class BaseResponse implements Serializable {
    private int status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;

    public static BaseResponse createResponse(int status, String message){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(status);
        baseResponse.setMessage(message);
        return baseResponse;
    }

    public static BaseResponse createResponse(HttpStatus httpStatusMsg){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(httpStatusMsg.getStatus());
        baseResponse.setMessage(httpStatusMsg.getMessage());
        return baseResponse;
    }
    @SuppressWarnings("all")
    public static BaseResponse createResponse(int status, String message, Object data){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(status);
        baseResponse.setMessage(message);
        baseResponse.setData(data);
        return baseResponse;
    }
    @SuppressWarnings("all")
    public static BaseResponse createResponse(HttpStatus httpStatusMsg, Object data){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(httpStatusMsg.getStatus());
        baseResponse.setMessage(httpStatusMsg.getMessage());
        baseResponse.setData(data);
        return baseResponse;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
