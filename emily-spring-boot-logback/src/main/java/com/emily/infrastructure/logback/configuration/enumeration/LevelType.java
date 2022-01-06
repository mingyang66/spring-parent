package com.emily.infrastructure.logback.configuration.enumeration;
/**
* @Description: 日志级别 OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
* @Author: Emily
* @create: 2022/1/4
*/
public enum LevelType {
    OFF("OFF"), ERROR("ERROR"), WARN("WARN"), INFO("INFO"), DEBUG("DEBUG"), TRACE("TRACE"), ALL("ALL");

    public String levelStr;

    LevelType(String levelStr) {
        this.levelStr = levelStr;
    }
}
