package com.emily.infrastructure.logger.configuration.type;

/**
 * 日志级别 OFF &gt; ERROR &gt; WARN &gt; INFO &gt; DEBUG &gt; TRACE &gt;ALL
 *
 * @author Emily
 * @since : 2022/1/4
 */
public enum LevelType {
    OFF("OFF"), ERROR("ERROR"), WARN("WARN"), INFO("INFO"), DEBUG("DEBUG"), TRACE("TRACE"), ALL("ALL");

    public String levelStr;

    LevelType(String levelStr) {
        this.levelStr = levelStr;
    }
}
