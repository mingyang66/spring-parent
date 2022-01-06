package com.emily.infrastructure.logback.configuration.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * @author Emily
 * @description: 日志的过滤级别
 * @create: 2020/08/04
 */
public class LogbackFilter {
    /**
     * 日志级别过滤器设置
     *
     * @param level 日志级别
     * @return
     */
    public static LevelFilter getLevelFilter(Level level) {
        LevelFilter filter = new LevelFilter();
        //日志过滤级别
        filter.setLevel(level);
        //设置符合条件的日志接受
        filter.setOnMatch(FilterReply.ACCEPT);
        //不符合条件的日志拒绝
        filter.setOnMismatch(FilterReply.DENY);
        filter.start();
        return filter;
    }

    /**
     * 日志阀值过滤器，等于或者高于日志级别
     *
     * @param level 日志级别
     * @return
     */
    public static ThresholdFilter getThresholdLevelFilter(Level level) {
        ThresholdFilter filter = new ThresholdFilter();
        filter.setLevel(level.levelStr);
        filter.start();
        return filter;
    }

}
