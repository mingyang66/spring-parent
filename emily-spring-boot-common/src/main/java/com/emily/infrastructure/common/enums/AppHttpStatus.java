package com.emily.infrastructure.common.enums;

/**
 * @Description: 自定义状态码异常枚举类
 * @Version: 1.0
 */
public enum AppHttpStatus {
    OK(0, "SUCCESS"),

    EXCEPTION(100000, "异常"),
    RUNTIME_EXCEPTION(100001, "运行时异常"),
    NULL_POINTER_EXCEPTION(100002, "空指针异常"),
    CLASS_CAST_EXCEPTION(100003, "类转换异常"),
    INDEX_OUT_OF_BOUNDS_EXCEPTION(100004, "数组越界异常"),
    METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION(100005, "参数类型不匹配"),
    MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION(100006, "缺少参数"),
    HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION(100007, "请求Method不支持"),
    HTTP_MESSAGE_NOT_READABLE_EXCEPTION(100008, "控制器方法中@RequestBody类型参数数据类型转换异常"),
    METHOD_ARGUMENT_NOT_VALID_EXCEPTION(100009,"非法参数"),
    BIND_EXCEPTION(100010, "控制器方法参数验证异常"),
    UNDECLARED_THROWABLE_EXCEPTION(100011, "代理异常"),
    NUMBER_FORMAT_EXCEPTION(100012, "数字格式异常"),
    JSON_PARSE_EXCEPTION(100013, "json数据格式转换异常"),
    DATE_TIME_EXCEPTION(100014, "日期格式转换异常"),
    ILLEGAL_ARGUMENT_EXCEPTION(100015, "非法参数"),
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
