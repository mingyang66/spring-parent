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
public class SimpleResponse<T> implements Serializable {
    private int status;
    private String message;
    private long spentTime;

    public SimpleResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public SimpleResponse(int status, String message, long spentTime) {
        this.status = status;
        this.message = message;
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
    public static <T> SimpleResponse<T> buildResponse(int status, String message) {
        return new SimpleResponse<T>(status, message);
    }

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> SimpleResponse<T> buildResponse(int status, String message, long spentTime) {
        return new SimpleResponse<T>(status, message, spentTime);
    }

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> SimpleResponse<T> buildResponse(AppHttpStatus appHttpStatus) {
        return new SimpleResponse<T>(appHttpStatus.getStatus(), appHttpStatus.getMessage());
    }
}
