package com.yaomy.control.common.enums;

/**
 * @Description: 自定义状态码异常枚举类
 * @ProjectName: spring-parent
 * @Package: com.yaomy.common.HttpStatusMsg
 * @Date: 2019/7/18 15:09
 * @Version: 1.0
 */
@SuppressWarnings("all")
@Deprecated
public enum HttpStatusMsg {
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

    //--------------------OAuth2认证异常------------------
    AUTHENTICATION_EXCEPTION(300, "登录异常，请检查登录信息..."),
    ACCESS_DENIDED_EXCEPTION(301, "访问资源受限"),
    PASSWORD_EXCEPTION(302, "密码异常"),
    USERNAME_EXCEPTION(303, "用户名异常"),
    LOGOUT_EXCEPTION(304, "登出异常"),;

    private final int status;
    private final String message;

    HttpStatusMsg(int status, String message){
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
