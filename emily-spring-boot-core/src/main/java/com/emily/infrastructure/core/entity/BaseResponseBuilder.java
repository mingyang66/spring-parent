package com.emily.infrastructure.core.entity;

import com.emily.infrastructure.core.constant.HeaderInfo;
import com.emily.infrastructure.core.context.holder.LocalContextHolder;
import com.emily.infrastructure.core.helper.RequestUtils;
import com.emily.infrastructure.language.convert.LanguageMap;
import com.emily.infrastructure.language.convert.LanguageType;

import java.io.Serializable;

/**
 * 控制器返回结果构造器
 *
 * @author Emily
 * @since 1.0
 */
public class BaseResponseBuilder<T> implements Serializable {
    private int status;
    private String message;
    private T data;
    private long spentTime;

    public BaseResponseBuilder<T> withStatus(int status) {
        this.status = status;
        return this;
    }

    public BaseResponseBuilder<T> withMessage(String message) {
        this.message = message;
        return this;
    }

    public BaseResponseBuilder<T> withData(T data) {
        this.data = data;
        return this;
    }

    public BaseResponseBuilder<T> withSpentTime(long spentTime) {
        this.spentTime = spentTime;
        return this;
    }

    public BaseResponse<T> build() {
        if (RequestUtils.isServlet()) {
            this.spentTime = LocalContextHolder.current().getSpentTime();
            this.message = LanguageMap.acquire(message, LanguageType.getByCode(RequestUtils.getHeader(HeaderInfo.LANGUAGE)));
        }
        return new BaseResponse<>(status, message, data, spentTime);
    }
}
