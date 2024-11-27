package com.emily.infrastructure.web.response.enums;

/**
 * 自定义状态码异常枚举类
 *
 * @author Emily
 * @since 1.0
 */
public enum ApplicationStatus {
    OK(0, "SUCCESS"),
    EXCEPTION(100000, "网络异常，请稍后再试"),
    ILLEGAL_ARGUMENT(100001, "非法参数"),
    ILLEGAL_DATA(100002, "非法数据"),
    ILLEGAL_ACCESS(100003, "非法访问"),
    ILLEGAL_PROXY(100004, "非法代理"),
    METHOD_NOT_ALLOWED(405, "方法不允许");

    private final int status;
    private final String message;

    ApplicationStatus(int status, String message) {
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
