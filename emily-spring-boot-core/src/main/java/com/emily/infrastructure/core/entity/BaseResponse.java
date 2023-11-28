package com.emily.infrastructure.core.entity;

import java.io.Serializable;

/**
 * 控制器返回结果
 *
 * @author Emily
 * @since 1.0
 */
public class BaseResponse<T> implements Serializable {
    private int status;
    private String message;
    private T data;
    private long spentTime;

    public BaseResponse() {
        super();
    }

    public BaseResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public BaseResponse(int status, String message, T data) {
        this(status, message);
        this.data = data;
    }

    public BaseResponse(int status, String message, T data, long spentTime) {
        this(status, message, data);
        this.spentTime = spentTime;
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

    public long getSpentTime() {
        return spentTime;
    }

    public void setSpentTime(long spentTime) {
        this.spentTime = spentTime;
    }

    public static <T> BaseResponseBuilder<T> newBuilder() {
        return new BaseResponseBuilder<T>();
    }
}
