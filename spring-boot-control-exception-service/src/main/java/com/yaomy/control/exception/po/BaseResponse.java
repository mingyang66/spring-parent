package com.yaomy.control.exception.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yaomy.control.exception.enums.HttpStatus;
import lombok.Data;

import java.io.Serializable;


@Data
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

    public static BaseResponse createResponse(int status, String message, Object data){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(status);
        baseResponse.setMessage(message);
        baseResponse.setData(data);
        return baseResponse;
    }

    public static BaseResponse createResponse(HttpStatus httpStatusMsg, Object data){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(httpStatusMsg.getStatus());
        baseResponse.setMessage(httpStatusMsg.getMessage());
        baseResponse.setData(data);
        return baseResponse;
    }
}
