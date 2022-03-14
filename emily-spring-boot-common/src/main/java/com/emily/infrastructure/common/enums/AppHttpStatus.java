package com.emily.infrastructure.common.enums;

/**
 * @author Emily
 * @Description: 自定义状态码异常枚举类
 * @Version: 1.0
 */
public enum AppHttpStatus {
    OK(0, "SUCCESS"),
    ERROR(-1, "服务器走神了..."),

    EXCEPTION(100000, "异常"),
    RUNTIME_EXCEPTION(100001, "运行时异常"),
    NULL_POINTER(100002, "空指针异常"),
    ILLEGAL_CLASS_CONVERT(100003, "类转换异常"),
    ILLEGAL_INDEX(100004, "非法索引下标"),
    MISMATCH_PARAMETER(100005, "参数不匹配"),
    MISSING_PARAMETER(100006, "参数缺失"),
    ILLEGAL_METHOD(100007, "非法Method请求"),
    ILLEGAL_PARAMETER(100010, "非法参数"),
    ILLEGAL_NUMBER_FORMAT(100012, "数字格式异常"),
    JSON_PARSE_EXCEPTION(100013, "json数据格式转换异常"),
    DATE_TIME_EXCEPTION(100014, "日期格式转换异常"),
    IO_EXCEPTION(100016, "网络请求异常"),

    AUTH_EXCEPTION(200000, "身份认证失败"),
    AUTH_EXPIRE(200001, "身份认证过期"),
    PERMISSION_DENY(20002, "权限不匹配"),
    ACCOUNT_NOT_EXIT(200003, "账号不存"),
    ACCOUNT_PASSWORD_ERROR(200004, "账号/密码错误"),
    VALIDATE_CODE_ERROR(200005, "验证码错误"),
    PHONE_VALIDATE_ERROR(200006, "手机验证码错误"),
    IMAGE_CODE_VALIDATE_ERROR(200007, "图形验证码错误"),

    SERVER_LIMITING_EXCEPTION(200008, "服务访问过于频繁，请稍后再试"),
    SERVER_RETRY_EXCEPTION(200009, "服务不可以重复提交，请稍后再试"),
    SERVER_ILLEGAL_ACCESS(200010, "非法访问"),
    SERVER_CIRCUIT_BREAKER(200011, "触发服务降级处理"),

    DATABASE_EXCEPTION(300000, "数据库异常"),

    INIT_EXCEPTION(3180000, "初始化异常"),

    API404_EXCEPTION(404, "远程接口不存在"),
    API500_EXCEPTION(500, "远程接口服务错误");


    /**
     * 状态码
     */
    private int status;
    /**
     * 描述字段
     */
    private String message;

    AppHttpStatus(int status, String message) {
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
