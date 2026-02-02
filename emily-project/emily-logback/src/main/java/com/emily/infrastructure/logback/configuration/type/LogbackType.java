package com.emily.infrastructure.logback.configuration.type;

/**
 * 日志类型
 *
 * @author Emily
 * @since : 2021/7/7
 */
public enum LogbackType {
    CONSOLE(0, "控制台日志"),
    ROOT(1, "普通日志"),
    GROUP(2, "分组日志"),
    MODULE(3, "模块日志");

    private final int code;
    private final String message;

    LogbackType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
