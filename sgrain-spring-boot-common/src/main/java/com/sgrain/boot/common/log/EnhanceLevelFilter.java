package com.sgrain.boot.common.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.FilterReply;

/**
 * @Description 自定义日志过滤器类
 * @Version  1.0
 */
public class EnhanceLevelFilter extends LevelFilter {

    private static final String LEFT_BRACE = "{";
    public static final String RIGHT_BRACE = "}";
    Level level;

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMessage().startsWith(LEFT_BRACE)
                && event.getMessage().endsWith(RIGHT_BRACE)
                && event.getLevel().equals(level)) {
            return FilterReply.ACCEPT;
        } else {
            return FilterReply.DENY;
        }
    }
    @Override
    public void setLevel(Level level) {
        this.level = level;
    }
    @Override
    public void start() {
        if (this.level != null) {
            super.start();
        }
    }
}