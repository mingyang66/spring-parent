package com.emily.infrastructure.common.exception;

/**
 * @author Emily
 * @Description: 自定义状态码异常枚举类
 * @Version: 1.0
 */
public enum HttpStatusType {
    OK(0, "SUCCESS"),
    EXCEPTION(100000, "网络异常，请稍后再试"),
    ILLEGAL_METHOD(100001, "非法方法请求"),
    ILLEGAL_ARGUMENT(100002, "非法参数"),
    ILLEGAL_DATA(100003, "非法数据"),
    ILLEGAL_ACCESS(100004, "非法访问"),
    ILLEGAL_PROXY(100005, "非法代理");

    /**
     * 状态码
     */
    private int status;
    /**
     * 描述字段
     */
    private String message;

    HttpStatusType(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
