package com.emily.infrastructure.core.entity;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.core.helper.RequestHelper;

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
    private long time;

    public BaseResponse() {
        super();
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

    public BaseResponse(int status, String message, T data, long time) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.time = time;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> BaseResponse<T> buildResponse(int status, String message) {
        return new BaseResponse<T>(status, message, null, RequestHelper.getTime());
    }

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> BaseResponse<T> buildResponse(int status, String message, T data) {
        return new BaseResponse<T>(status, message, data, RequestHelper.getTime());
    }

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> BaseResponse<T> buildResponse(AppHttpStatus appHttpStatus, T data) {
        return new BaseResponse<>(appHttpStatus.getStatus(), appHttpStatus.getMessage(), data, RequestHelper.getTime());
    }

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> BaseResponse<T> buildResponse(AppHttpStatus appHttpStatus) {
        return new BaseResponse<>(appHttpStatus.getStatus(), appHttpStatus.getMessage(), null, RequestHelper.getTime());
    }

    /**
     * 创建响应数据对象
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> buildResponse(T data) {
        return new BaseResponse<>(AppHttpStatus.OK.getStatus(), AppHttpStatus.OK.getMessage(), data, RequestHelper.getTime());
    }
}
