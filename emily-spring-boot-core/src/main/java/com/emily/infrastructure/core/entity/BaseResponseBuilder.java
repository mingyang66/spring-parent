package com.emily.infrastructure.core.entity;

import com.emily.infrastructure.common.constant.HeaderInfo;
import com.emily.infrastructure.core.helper.RequestUtils;
import com.emily.infrastructure.language.convert.LanguageMap;
import com.emily.infrastructure.language.convert.LanguageType;

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

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public long getSpentTime() {
        return spentTime;
    }

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
            this.message = LanguageMap.acquire(message, LanguageType.getByCode(RequestUtils.getRequest().getHeader(HeaderInfo.LANGUAGE)));
        }
        return new BaseResponse<>(status, message, data, spentTime);
    }
}
