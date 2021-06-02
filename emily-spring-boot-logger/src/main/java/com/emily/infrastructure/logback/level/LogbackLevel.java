package com.emily.infrastructure.logback.level;

import ch.qos.logback.classic.Level;

import java.util.Arrays;
import java.util.List;

/**
 * @Author Emily
 * @description: 访问日志级别
 * @create: 2020/08/07
 */
public class LogbackLevel {
    private static final List<String> LOGGER_LEVEL = Arrays.asList(Level.OFF.levelStr, Level.ERROR.levelStr, Level.WARN.levelStr, Level.INFO.levelStr, Level.DEBUG.levelStr, Level.TRACE.levelStr, Level.ALL.levelStr);

    /**
     * 获取日志下一级别
     * @param level ERROR > WARN > INFO > DEBUG > TRACE >ALL
     * @return
     */
    public static Level getNextLogLevel(String level) {
        level = level.toUpperCase();
        if(LOGGER_LEVEL.indexOf(level)+1 < LOGGER_LEVEL.size()){
            level = LOGGER_LEVEL.get(LOGGER_LEVEL.indexOf(level)+1);
        }
        return Level.toLevel(level);
    }
}
