package com.emily.infrastructure.logger.configuration.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.FilterReply;
import com.emily.infrastructure.logger.common.StrUtils;

/**
 * @author Emily
 * @description: 日志的过滤级别
 * @create: 2020/08/04
 */
public class LogbackFilter {

    private static final LogbackFilter FILTER = new LogbackFilter();

    private LogbackFilter() {
    }

    public static LogbackFilter getSingleton() {
        return FILTER;
    }

    /**
     * 日志级别过滤器设置
     *
     * @param level 日志级别
     * @return 日志级别过滤器对象
     */
    public LevelFilter getLevelFilter(Context context, Level level) {
        LevelFilter filter = new LevelFilter();
        //过滤器名称
        filter.setName(StrUtils.join("LevelFilter-", level.levelStr));
        //上下文
        filter.setContext(context);
        //日志过滤级别
        filter.setLevel(level);
        //设置符合条件的日志接受
        filter.setOnMatch(FilterReply.ACCEPT);
        //不符合条件的日志拒绝
        filter.setOnMismatch(FilterReply.DENY);
        //添加内部状态信息
        filter.addError("Build LevelFilter Success");
        //标记为启用状态
        filter.start();
        return filter;
    }

    /**
     * 日志阀值过滤器，等于或者高于日志级别
     *
     * @param level 日志级别
     * @return 日志阀值过滤器对象
     */
    public ThresholdFilter getThresholdLevelFilter(Context context, Level level) {
        ThresholdFilter filter = new ThresholdFilter();
        //过滤器名称
        filter.setName(StrUtils.join("ThresholdFilter-", level.levelStr));
        //上下文
        filter.setContext(context);
        //日志级别
        filter.setLevel(level.levelStr);
        //添加内部状态信息
        filter.addInfo("Build ThresholdFilter Success");
        //标记为启用状态
        filter.start();
        return filter;
    }
}
