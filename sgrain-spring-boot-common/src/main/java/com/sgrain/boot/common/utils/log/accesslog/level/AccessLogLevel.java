package com.sgrain.boot.common.utils.log.accesslog.level;

import ch.qos.logback.classic.Level;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @description: 访问日志级别
 * @create: 2020/08/07
 */
public class AccessLogLevel {
    private static final List<String> levelArray = Arrays.asList(Level.ERROR.levelStr, Level.WARN.levelStr, Level.INFO.levelStr, Level.DEBUG.levelStr, Level.TRACE.levelStr, Level.ALL.levelStr);
    /**
     * 获取配置日志级别
     * ERROR > WARN > INFO > DEBUG > TRACE >ALL
     * @return
     */
    public static Level getLogLevel(String level) {
        if (StringUtils.equalsIgnoreCase(level, Level.ERROR.levelStr)) {
            return Level.ERROR;
        } else if (StringUtils.equalsIgnoreCase(level, Level.WARN.levelStr)) {
            return Level.WARN;
        } else if (StringUtils.equalsIgnoreCase(level, Level.INFO.levelStr)) {
            return Level.INFO;
        } else if (StringUtils.equalsIgnoreCase(level, Level.DEBUG.levelStr)) {
            return Level.DEBUG;
        } else if (StringUtils.equalsIgnoreCase(level, Level.TRACE.levelStr)) {
            return Level.TRACE;
        }else {
            return Level.ALL;
        }
    }

    /**
     * 获取日志下一级别
     * @param level ERROR > WARN > INFO > DEBUG > TRACE >ALL
     * @return
     */
    public static Level getNextLogLevel(String level) {
        level = StringUtils.upperCase(level);
        if(levelArray.indexOf(level)+1 < levelArray.size()){
            level = levelArray.get(levelArray.indexOf(level)+1);
        }
        return getLogLevel(level);
    }
}
