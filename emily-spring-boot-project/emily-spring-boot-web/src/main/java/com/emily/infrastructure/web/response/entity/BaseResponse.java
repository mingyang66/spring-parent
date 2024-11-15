package com.emily.infrastructure.web.response.entity;

/**
 * 控制器返回结果
 *
 * @author Emily
 * @since 1.0
 */
public class BaseResponse<T> {
    private int status;
    private String message;
    private T data;
    private long spentTime;

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

    public BaseResponse<T> status(int status) {
        this.status = status;
        return this;
    }

    public BaseResponse<T> message(String message) {
        this.message = message;
        return this;
    }

    public BaseResponse<T> data(T data) {
        this.data = data;
        return this;
    }

    public BaseResponse<T> spentTime(long spentTime) {
        this.spentTime = spentTime;
        return this;
    }
}
