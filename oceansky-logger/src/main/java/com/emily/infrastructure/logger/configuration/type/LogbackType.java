package com.emily.infrastructure.logger.configuration.type;

/**
 * @Description: 日志类型
 * @Author: Emily
 * @create: 2021/7/7
 */
public enum LogbackType {
    CONSOLE(0, "控制台日志"),
    ROOT(1, "普通日志"),
    GROUP(2, "分组日志"),
    MODULE(3, "模块日志");

    private Integer type;
    private String desc;

    LogbackType(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
