package com.emily.infrastructure.core.entity;

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
            BaseResponse<T> response = new BaseResponse<>();
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
