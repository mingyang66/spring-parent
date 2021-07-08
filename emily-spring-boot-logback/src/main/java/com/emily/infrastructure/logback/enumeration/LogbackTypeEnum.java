package com.emily.infrastructure.logback.enumeration;

/**
 * @Description: 日志类型
 * @Author: Emily
 * @create: 2021/7/7
 */
public enum LogbackTypeEnum {
    ROOT(1, "普通日志"),
    GROUP(2, "分组日志"),
    MODULE(3, "模块日志");

    private Integer id;
    private String desc;

    LogbackTypeEnum(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }
}
