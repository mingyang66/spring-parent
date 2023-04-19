package com.emily.infrastructure.common.entity;

import com.emily.infrastructure.common.exception.HttpStatusType;
import com.emily.infrastructure.common.i18n.LanguageMap;
import com.emily.infrastructure.common.utils.RequestUtils;

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

    public BaseResponse(int status, String message) {
        this.status = status;
        this.message = LanguageMap.acquire(message);
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

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> BaseResponse<T> buildResponse(int status, String message) {
        return new BaseResponse<T>(status, message, null, RequestUtils.getSpentTime());
    }

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> BaseResponse<T> buildResponse(int status, String message, T data) {
        return new BaseResponse<T>(status, message, data, RequestUtils.getSpentTime());
    }

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> BaseResponse<T> buildResponse(HttpStatusType appHttpStatus, T data) {
        return new BaseResponse<>(appHttpStatus.getStatus(), appHttpStatus.getMessage(), data, RequestUtils.getSpentTime());
    }

    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version 1.0
     */
    public static <T> BaseResponse<T> buildResponse(HttpStatusType appHttpStatus) {
        return new BaseResponse<>(appHttpStatus.getStatus(), appHttpStatus.getMessage(), null, RequestUtils.getSpentTime());
    }

    /**
     * 创建响应数据对象
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> buildResponse(T data) {
        return new BaseResponse<>(HttpStatusType.OK.getStatus(), HttpStatusType.OK.getMessage(), data, RequestUtils.getSpentTime());
    }
}
