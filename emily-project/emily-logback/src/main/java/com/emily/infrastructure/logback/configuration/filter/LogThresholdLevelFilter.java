package com.emily.infrastructure.logback.configuration.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.core.Context;
import com.emily.infrastructure.logback.common.StrUtils;

/**
 * 日志的过滤级别
 *
 * @author Emily
 * @since : 2020/08/04
 */
public class LogThresholdLevelFilter {
    private final Context context;

    public LogThresholdLevelFilter(Context context) {
        this.context = context;
    }

    /**
     * 日志阀值过滤器，等于或者高于日志级别
     *
     * @param level 日志级别
     * @return 日志阀值过滤器对象
     */
    public ThresholdFilter getFilter(Level level) {
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
