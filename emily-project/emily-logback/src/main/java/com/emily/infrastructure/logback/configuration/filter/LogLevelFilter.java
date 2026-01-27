package com.emily.infrastructure.logback.configuration.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.FilterReply;
import com.emily.infrastructure.logback.common.StrUtils;

/**
 * 日志的过滤级别
 *
 * @author Emily
 * @since : 2020/08/04
 */
public class LogLevelFilter {
    private final Context context;

    public LogLevelFilter(Context context) {
        this.context = context;
    }

    /**
     * 日志级别过滤器设置
     *
     * @param level 日志级别
     * @return 日志级别过滤器对象
     */
    public LevelFilter getFilter(Level level) {
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
        filter.addInfo("Build LevelFilter Success");
        //标记为启用状态
        filter.start();
        return filter;
    }
}
