package com.emily.infrastructure.autoconfigure.entity;

import com.emily.infrastructure.autoconfigure.filter.utils.RequestUtils;
import com.emily.infrastructure.common.constant.HeaderInfo;
import com.emily.infrastructure.core.context.holder.LocalContextHolder;
import com.emily.infrastructure.language.convert.LanguageMap;
import com.emily.infrastructure.language.convert.LanguageType;

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

    public static class Builder<T> {
        private int status;
        private String message;
        private T data;
        private long spentTime;

        public Builder<T> withStatus(int status) {
            this.status = status;
            return this;
        }

        public Builder<T> withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> withData(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> withSpentTime(long spentTime) {
            this.spentTime = spentTime;
            return this;
        }

        public BaseResponse<T> build() {
            if (RequestUtils.isServlet()) {
                this.spentTime = LocalContextHolder.current().getSpentTime();
                this.message = LanguageMap.acquire(message, LanguageType.getByCode(RequestUtils.getHeader(HeaderInfo.LANGUAGE)));
            }
            BaseResponse response = new BaseResponse();
            response.setStatus(status);
            response.setMessage(message);
            response.setData(data);
            response.setSpentTime(spentTime);
            return response;
        }
    }

    public static <T> Builder<T> newBuilder() {
        return new Builder<T>();
    }
}
