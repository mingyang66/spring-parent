package com.yaomy.control.exception.enums;

/**
 * @Description: 自定义状态码异常枚举类
 * @Version: 1.0
 */
public enum HttpStatus {
    OK(200,"SUCCESS"),
    UNKNOW_EXCEPTION(201, "未知异常"),
    RUNTIME_EXCEPTION(202, "运行时异常"),
    NULL_POINTER_EXCEPTION(203, "空指针异常"),
    CLASS_CAST_EXCEPTION(204, "类型转换异常"),
    IO_EXCEPTION(205, "IO异常"),
    INDEX_OUTOF_BOUNDS_EXCEPTION(206, "数组越界异常"),
    METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTIION(207, "参数类型不匹配"),
    MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION(208, "缺少参数"),
    HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION(209, "不支持的method类型"),
    PARAM_EXCEPTION(210, "参数异常");


    private final int status;
    private final String message;

    HttpStatus(int status, String message){
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
