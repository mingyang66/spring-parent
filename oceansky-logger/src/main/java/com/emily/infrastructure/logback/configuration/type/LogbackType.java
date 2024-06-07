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

    private Integer code;
    private String desc;

    LogbackType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
