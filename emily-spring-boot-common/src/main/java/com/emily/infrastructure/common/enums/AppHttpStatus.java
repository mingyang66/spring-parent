package com.emily.infrastructure.common.enums;

/**
 * @author Emily
 * @Description: 自定义状态码异常枚举类
 * @Version: 1.0
 */
public enum AppHttpStatus {
    OK(0, "SUCCESS"),
    UNKNOWN_EXCEPTION(-1, "未知异常"),

    EXCEPTION(100000, "异常"),
    RUNTIME_EXCEPTION(100001, "运行时异常"),
    NULL_POINTER_EXCEPTION(100002, "空指针异常"),
    CLASS_CAST_EXCEPTION(100003, "类转换异常"),
    INDEX_OUT_OF_BOUNDS_EXCEPTION(100004, "数组越界异常"),
    PARAMETER_MISMATCH_EXCEPTION(100005, "参数类型不匹配"),
    PARAMETER_MISSING_EXCEPTION(100006, "参数缺失"),
    METHOD_SUPPORTED_EXCEPTION(100007, "请求Method不支持"),
    PARAMETER_TYPE_EXCEPTION(100008, "非法参数转换"),
    PARAMETER_EXCEPTION(100010, "非法参数"),
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
