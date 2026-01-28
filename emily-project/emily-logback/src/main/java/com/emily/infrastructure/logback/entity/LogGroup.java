package com.emily.infrastructure.logback.entity;

import org.slf4j.event.Level;

/**
 * @author :  Emily
 * @since :  2026/1/28 下午2:20
 */
public class LogGroup {
    /**
     * 是否将模块日志信息输出到控制台，默认false
     */
    private boolean console = false;
    /**
     * 模块输出的日志级别，ERROR > WARN > INFO > DEBUG >TRACE, 默认：DEBUG
     */
    private Level level = Level.INFO;
    /**
     * 模块日志输出格式，默认：%msg%n
     */
    private String pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %cn --- [%18.18thread] %-36.36logger{36}:%-4.4line : %msg %n";

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
