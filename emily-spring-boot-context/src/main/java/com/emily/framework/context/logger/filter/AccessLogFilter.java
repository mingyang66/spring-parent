package com.emily.framework.context.logger.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * @description: 日志的过滤级别
 * @create: 2020/08/04
 */
public class AccessLogFilter {
    /**
     * 过滤器设置
     *
     * @param level 日志级别
     * @return
     */
    public LevelFilter getLevelFilter(Level level) {
        LevelFilter levelFilter = new LevelFilter();
        //日志过滤级别
        levelFilter.setLevel(level);
        //设置符合条件的日志接受
        levelFilter.setOnMatch(FilterReply.ACCEPT);
        //不符合条件的日志拒绝
        levelFilter.setOnMismatch(FilterReply.DENY);
        return levelFilter;
    }

    /**
     * 设置控制台过滤器
     *
     * @param level 日志级别
     * @return
     */
    public ThresholdFilter getThresholdLevelFilter(Level level) {
        ThresholdFilter filter = new ThresholdFilter();
        filter.setLevel(level.levelStr);
        return filter;
    }

}
