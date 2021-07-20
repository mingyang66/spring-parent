package com.emily.infrastructure.common.base;

import com.emily.infrastructure.common.enums.AppHttpStatus;

import java.io.Serializable;

/**
 * @author Emily
 * @Description: 控制器返回结果
 * @ProjectName: spring-parent
 * @Date: 2019/7/1 15:33
 * @Version: 1.0
 */
public class BaseResponse<T> implements Serializable {
    private int status;
    private String message;
    private T data;
    private long spentTime;

    public BaseResponse() {
        super();
    }

    public BaseResponse(int status, String message, T data, long spentTime) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.spentTime = spentTime;
    }

    public BaseResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public BaseResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
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


    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> BaseResponse<T> buildResponse(int status, String message) {
        return new BaseResponse<T>(status, message);
    }

    /**
     * 创建响应对象
     *
     * @param data
     * @return
     */
    public static <T> BaseResponse<T> buildResponse(T data) {
        return new BaseResponse<T>(AppHttpStatus.OK.getStatus(), AppHttpStatus.OK.getMessage(), data);
    }

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> BaseResponse<T> buildResponse(int status, String message, T data) {
        return new BaseResponse<T>(status, message, data);
    }

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> BaseResponse<T> buildResponse(int status, String message, T data, long spentTime) {
        return new BaseResponse<T>(status, message, data, spentTime);
    }

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> BaseResponse<T> buildResponse(AppHttpStatus appHttpStatus) {
        return new BaseResponse<>(appHttpStatus.getStatus(), appHttpStatus.getMessage());
    }

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> BaseResponse<T> buildResponse(AppHttpStatus appHttpStatus, T data) {
        return new BaseResponse<T>(appHttpStatus.getStatus(), appHttpStatus.getMessage(), data);
    }
}
