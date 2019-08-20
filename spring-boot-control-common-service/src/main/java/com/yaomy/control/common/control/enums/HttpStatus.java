package com.yaomy.control.common.control.enums;

/**
 * @Description: 自定义状态码异常枚举类
 * @Version: 1.0
 */
public enum HttpStatus {
    /**
     * 执行成功
     */
    OK(200,"SUCCESS"),
    /**
     * 未知异常
     */
    UNKNOW_EXCEPTION(201, "未知异常"),
    /**
     * 运行时异常
     */
    RUNTIME_EXCEPTION(202, "运行时异常"),
    /**
     * 空指针异常
     */
    NULL_POINTER_EXCEPTION(203, "空指针异常"),
    /**
     * 类型转换异常
     */
    CLASS_CAST_EXCEPTION(204, "类型转换异常"),
    /**
     * IO异常
     */
    IO_EXCEPTION(205, "IO异常"),
    /**
     * 数组越界
     */
    INDEX_OUTOF_BOUNDS_EXCEPTION(206, "数组越界异常"),
    /**
     * 参数类型不匹配
     */
    METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTIION(207, "参数类型不匹配"),
    /**
     * 缺少参数
     */
    MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION(208, "缺少参数"),
    /**
     * 不支持的请求METHOD
     */
    HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION(209, "不支持的method类型"),
    /**
     * 参数异常
     */
    PARAM_EXCEPTION(210, "参数异常");

    /**
     * 状态码
     */
    private final int status;
    /**
     * 描述字段
     */
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
