package com.emily.infrastructure.logback.common.level;

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
     * 获取配置日志级别
     * ERROR > WARN > INFO > DEBUG > TRACE >ALL
     * @return
     */
    /*public static Level getLogLevel(String level) {
        if (Level.ERROR.levelStr.equals(level.toUpperCase())) {
            return Level.ERROR;
        } else if (Level.WARN.levelStr.equals(level.toUpperCase())) {
            return Level.WARN;
        } else if (Level.INFO.levelStr.equals(level.toUpperCase())) {
            return Level.INFO;
        } else if (Level.DEBUG.levelStr.equals(level.toUpperCase())) {
            return Level.DEBUG;
        } else if (Level.TRACE.levelStr.equals(level.toUpperCase())) {
            return Level.TRACE;
        }else {
            return Level.ALL;
        }
    }*/

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
