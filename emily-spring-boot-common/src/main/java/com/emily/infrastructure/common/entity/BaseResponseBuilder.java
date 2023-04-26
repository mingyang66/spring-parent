package com.emily.infrastructure.common.entity;

import com.emily.infrastructure.common.utils.RequestUtils;

import java.io.Serializable;

/**
 * @author Emily
 * @Description: 控制器返回结果构造器
 * @ProjectName: spring-parent
 * @Date: 2019/7/1 15:33
 * @Version: 1.0
 */
public class BaseResponseBuilder<T> implements Serializable {
    private int status;
    private String message;
    private T data;
    private long spentTime;

    public BaseResponseBuilder<T> status(int status) {
        this.status = status;
        return this;
    }

    public BaseResponseBuilder<T> message(String message) {
        this.message = message;
        return this;
    }

    public BaseResponseBuilder<T> data(T data) {
        this.data = data;
        return this;
    }

    public BaseResponseBuilder<T> spentTime(long spentTime) {
        this.spentTime = spentTime;
        return this;
    }

    public BaseResponse<T> build() {
        if (this.spentTime == 0L && RequestUtils.isServlet()) {
            this.spentTime = RequestUtils.getSpentTime();
        }
        return new BaseResponse<>(status, message, data, spentTime);
    }
}
