package com.emily.infrastructure.logger.configuration.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.boolex.JaninoEventEvaluator;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.turbo.MarkerFilter;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.filter.EvaluatorFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.emily.infrastructure.logger.common.StrUtils;

/**
 * 日志的过滤级别
 *
 * @author Emily
 * @since : 2020/08/04
 */
public class LogbackFilter {
    private final Context context;

    private LogbackFilter(Context context) {
        this.context = context;
    }

    /**
     * 日志级别过滤器设置
     *
     * @param level 日志级别
     * @return 日志级别过滤器对象
     */
    public LevelFilter buildLevelFilter(Level level) {
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
    public ThresholdFilter buildThresholdLevelFilter(Level level) {
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

    /**
     * 全局标记过滤器，接受指定标记的日志记录到文件中
     *
     * @param marker marker标识
     * @return 标记过滤器，将会接受被标记的日志记录到文件中
     */
    public MarkerFilter buildAcceptMarkerFilter(String marker) {
        MarkerFilter filter = new MarkerFilter();
        //过滤器名称
        filter.setName(StrUtils.join("AcceptMarkerFilter-", marker));
        //上下文
        filter.setContext(context);
        //日志过滤级别
        filter.setMarker(marker);
        //设置符合条件的日志接受
        filter.setOnMatch(FilterReply.ACCEPT.name());
        //不符合条件的日志拒绝
        filter.setOnMismatch(FilterReply.DENY.name());
        //添加内部状态信息
        filter.addError("Build AcceptMarkerFilter Success");
        //标记为启用状态
        filter.start();
        return filter;
    }

    /**
     * 全局标记过滤器，拒绝标记的日志记录到文件中
     *
     * @param marker marker标识
     * @return 标记过滤器，将会拒绝被标记的日志记录到文件中
     */
    public MarkerFilter buildDenyMarkerFilter(String marker) {
        MarkerFilter filter = new MarkerFilter();
        //过滤器名称
        filter.setName(StrUtils.join("DenyMarkerFilter-", marker));
        //上下文
        filter.setContext(context);
        //日志过滤级别
        filter.setMarker(marker);
        //设置符合条件的日志接受
        filter.setOnMatch(FilterReply.DENY.name());
        //不符合条件的日志拒绝
        filter.setOnMismatch(FilterReply.ACCEPT.name());
        //添加内部状态信息
        filter.addError("Build DenyMarkerFilter Success");
        //标记为启用状态
        filter.start();
        return filter;
    }

    /**
     * todo 待定
     *
     * @param context logger context上下文
     * @return 评估过滤器实例
     */
    public EvaluatorFilter buildEvaluatorFilter(Context context) {
        EvaluatorFilter filter = new EvaluatorFilter();
        EventEvaluator evaluator = new JaninoEventEvaluator();
        try {
            evaluator.evaluate("return message.contains('info')");
        } catch (EvaluationException e) {
            throw new RuntimeException(e);
        }
        filter.setEvaluator(evaluator);
        filter.setContext(context);
        filter.setOnMatch(FilterReply.ACCEPT);
        filter.setOnMismatch(FilterReply.DENY);
        filter.start();
        return filter;
    }

    public static LogbackFilter create(Context context) {
        return new LogbackFilter(context);
    }
}
