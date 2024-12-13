package com.emily.sample.transfer.entity;

/**
 * 控制器返回结果
 *
 * @author Emily
 * @since 1.0
 */
public class TransferSampleResponse<T> {
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

}
