package com.emily.infrastructure.logback.entity;

import org.slf4j.event.Level;

/**
 * @author :  Emily
 * @since :  2026/1/28 下午2:20
 */
public class LogRoot {
    /**
     * 是否将日志信息输出到控制台，默认：true
     */
    private boolean console = true;
    /**
     * 基础日志文件路径，相对
     */
    private String filePath = "base";
    /**
     * 日志级别，OFF > ERROR > WARN > INFO > DEBUG >TRACE > ALL, 默认：DEBUG
     */
    private Level level = Level.INFO;
    /**
     * 记录文件格式-不带颜色
     */
    private String pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %cn --- [%18.18thread] %-36.36logger{36}:%-4.4line : %msg %n";
    /**
     * 打印控制台格式-带颜色
     * 可以打印当前类名格式，默认：[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%thread] [%p (%file:%line\\)] : %msg%n
     * 通用日志输出格式：[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%thread] [%-5level] [%-36.36logger{36}:%-4.4line] : %msg%n
     */
    private String consolePattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %highlight(%-5level) %cn --- [%18.18thread] %cyan(%-36.36logger{36}:%-4.4line) : %msg %n";
    /**
     * ANSI color codes支持，默认：false；请注意，基于Unix的操作系统（如Linux和Mac OS X）默认支持ANSI颜色代码。
     */
    private boolean withJansi = false;

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

    public String getConsolePattern() {
        return consolePattern;
    }

    public void setConsolePattern(String consolePattern) {
        this.consolePattern = consolePattern;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isConsole() {
        return console;
    }

    public void setConsole(boolean console) {
        this.console = console;
    }

    public boolean isWithJansi() {
        return withJansi;
    }

    public void setWithJansi(boolean withJansi) {
        this.withJansi = withJansi;
    }
}
