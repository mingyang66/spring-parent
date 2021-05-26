package com.emily.infrastructure.common.base;

import com.emily.infrastructure.common.enums.AppHttpStatus;

import java.io.Serializable;

/**
 * @Description: 控制器返回结果
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.po.AjaxResponseBody
 * @Date: 2019/7/1 15:33
 * @Version: 1.0
 */
public class SimpleResponse<T> implements Serializable {
    private int status;
    private String message;

    public SimpleResponse() {
        super();
    }

    private SimpleResponse(Builder<T> builder) {
        this.status = builder.status;
        this.message = builder.message;
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

    public static class Builder<T> {
        private int status;
        private String message;

        public Builder<T> setStatus(int status) {
            this.status = status;
            return this;
        }

        public Builder<T> setMessage(String message) {
            this.message = message;
            return this;
        }

        public SimpleResponse<T> builder() {
            return new SimpleResponse<>(this);
        }
    }

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> SimpleResponse<T> buildResponse(int status, String message) {
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
    public static <T> SimpleResponse<T> buildResponse(T data) {
        return new Builder<T>()
                .setStatus(AppHttpStatus.OK.getStatus())
                .setMessage(AppHttpStatus.OK.getMessage())
                .builder();
    }

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> SimpleResponse<T> buildResponse(int status, String message, T data) {
        return new Builder<T>()
                .setStatus(status)
                .setMessage(message)
                .builder();
    }

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> SimpleResponse<T> buildResponse(AppHttpStatus appHttpMsg) {
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
    public static <T> SimpleResponse<T> buildResponse(AppHttpStatus appHttpMsg, T data) {
        return new Builder<T>()
                .setStatus(appHttpMsg.getStatus())
                .setMessage(appHttpMsg.getMessage())
                .builder();
    }
}
