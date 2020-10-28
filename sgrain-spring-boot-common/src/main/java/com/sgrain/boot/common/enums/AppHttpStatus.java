package com.sgrain.boot.common.enums;

/**
 * @Description: 自定义状态码异常枚举类
 * @Version: 1.0
 */
public enum AppHttpStatus {
    OK(0,"SUCCESS"),
    FAILED(-1, "FAILED"),
    EXCEPTION(201, "未知异常"),
    RUNTIME_EXCEPTION(202, "运行时异常"),
    NULL_POINTER_EXCEPTION(203, "空指针异常"),
    CLASS_CAST_EXCEPTION(204, "类型转换异常"),
    IO_EXCEPTION(205, "IO异常"),
    SYSTEM_EXCEPTION(210, "系统异常"),
    NOT_FOUND(404, "Not Found"),


    API_EXCEPTION(10000, "接口异常"),
    API_NOT_FOUND_EXCEPTION(10001, "接口不存在"),
    API_RATE_LIMIT_EXCEPTION(10002, "接口访问过于频繁，请稍后再试"),
    API_IDEMPOTENT_EXCEPTION(10003, "接口不可以重复提交，请稍后再试"),
    API_PARAM_EXCEPTION(10004, "参数异常"),
    API_PARAM_MISSING_EXCEPTION(10005, "缺少参数"),
    API_METHOD_NOT_SUPPORTED_EXCEPTION(10006, "不支持的Method类型"),
    API_METHOD_PARAM_TYPE_EXCEPTIION(10007, "参数类型不匹配"),

    ARRAY_EXCEPTION(11001, "数组异常"),
    ARRAY_OUT_OF_BOUNDS_EXCEPTION(11002, "数组越界异常"),

    JSON_SERIALIZE_EXCEPTION(30000, "序列化数据异常"),
    JSON_DESERIALIZE_EXCEPTION(30001, "反序列化数据异常"),

    READ_RESOURSE_EXCEPTION(31002, "读取资源异常"),
    READ_RESOURSE_NOT_FOUND_EXCEPTION(31003, "资源不存在异常"),

    DATA_EXCEPTION(32004, "数据异常"),
    DATA_NOT_FOUND_EXCEPTION(32005, "未找到符合条件的数据异常"),
    DATA_CALCULATION_EXCEPTION(32006, "数据计算异常"),
    DATA_COMPRESS_EXCEPTION(32007, "数据压缩异常"),
    DATA_DE_COMPRESS_EXCEPTION(32008, "数据解压缩异常"),
    DATA_PARSE_EXCEPTION(32009, "数据转换异常"),

    ENCODING_EXCEPTION(33006, "编码异常"),
    ENCODING_UNSUPPORTED_EXCEPTION(33006, "编码不支持异常"),

    DATE_PARSE_EXCEPTION(34001, "日期转换异常"),

    MAILE_SEND_EXCEPTION(35001, "邮件发送异常");


    /**
     * 状态码
     */
    private final int status;
    /**
     * 描述字段
     */
    private final String message;

    AppHttpStatus(int status, String message){
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
