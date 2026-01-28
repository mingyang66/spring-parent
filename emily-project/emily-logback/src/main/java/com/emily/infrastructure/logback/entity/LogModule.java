package com.emily.infrastructure.logback.entity;

import org.slf4j.event.Level;

/**
 * @author :  Emily
 * @since :  2026/1/28 下午2:19
 */
public class LogModule {
    /**
     * 是否将模块日志信息输出到控制台，默认：false
     */
    private boolean console = false;
    /**
     * 模块输出的日志级别，ERROR > WARN > INFO > DEBUG >TRACE, 默认：DEBUG
     */
    private Level level = Level.INFO;
    /**
     * 模块日志输出格式，默认：%msg%n
     */
    private String pattern = "%msg%n";

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean isConsole() {
        return console;
    }

    public void setConsole(boolean console) {
        this.console = console;
    }
}
