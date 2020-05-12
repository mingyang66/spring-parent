package com.sgrain.boot.common.enums;

/**
 * @Description: 自定义状态码异常枚举类
 * @Version: 1.0
 */
public enum AppHttpStatus {
    OK(0,"SUCCESS"),
    FAILED(-1, "FAILED"),
    UNKNOW_EXCEPTION(201, "未知异常"),
    RUNTIME_EXCEPTION(202, "运行时异常"),
    NULL_POINTER_EXCEPTION(203, "空指针异常"),
    CLASS_CAST_EXCEPTION(204, "类型转换异常"),
    IO_EXCEPTION(205, "IO异常"),
    INDEX_OUTOF_BOUNDS_EXCEPTION(206, "数组越界异常"),
    METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTIION(207, "参数类型不匹配"),
    MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION(208, "缺少参数"),
    HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION(209, "不支持的method类型"),
    PARAM_EXCEPTION(210, "参数异常"),
    NOT_FOUND_EXCEPTION(404, "接口不存在"),
    RATE_LIMIT_EXCEPTION(10000, "接口访问过于频繁，请稍后再试！"),
    IDEMPOTENT_EXCEPTION(20000, "接口不可以重复提交，请稍后再试！"),
    JSON_SERIALIZE_EXCEPTION(30000, "序列化数据异常"),
    JSON_DESERIALIZE_EXCEPTION(30001, "反序列化数据异常"),
    READ_REMOTE_RESOURSE_EXCEPTION(30002, "读取远程服务器资源异常");

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
