package com.emily.boot.common.base;

import com.emily.boot.common.enums.AppHttpStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * @Description: 控制器返回结果
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.po.AjaxResponseBody
 * @Date: 2019/7/1 15:33
 * @Version: 1.0
 */
public class ResponseData<T> implements Serializable {
    private int status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ResponseData() {
        super();
    }

    private ResponseData(Builder<T> builder) {
        this.status = builder.status;
        this.message = builder.message;
        this.data = builder.data;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static class Builder<T> {
        private int status;
        private String message;
        private T data;

        public Builder<T> setStatus(int status) {
            this.status = status;
            return this;
        }

        public Builder<T> setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> setData(T data) {
            this.data = data;
            return this;
        }

        public ResponseData<T> builder() {
            return new ResponseData<>(this);
        }
    }

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> ResponseData<T> buildResponse(int status, String message) {
        return new Builder<T>()
                .setStatus(status)
                .setMessage(message)
                .builder();
    }

    /**
     * 创建响应对象
     *
     * @param data
     * @return
     */
    public static <T> ResponseData<T> buildResponse(T data) {
        return new Builder<T>()
                .setStatus(AppHttpStatus.OK.getStatus())
                .setMessage(AppHttpStatus.OK.getMessage())
                .setData(data)
                .builder();
    }

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> ResponseData<T> buildResponse(int status, String message, T data) {
        return new Builder<T>()
                .setStatus(status)
                .setMessage(message)
                .setData(data)
                .builder();
    }

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> ResponseData<T> buildResponse(AppHttpStatus appHttpMsg) {
        return new Builder<T>()
                .setStatus(appHttpMsg.getStatus())
                .setMessage(appHttpMsg.getMessage())
                .builder();
    }

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> ResponseData<T> buildResponse(AppHttpStatus appHttpMsg, T data) {
        return new Builder<T>()
                .setStatus(appHttpMsg.getStatus())
                .setMessage(appHttpMsg.getMessage())
                .setData(data)
                .builder();
    }
}
