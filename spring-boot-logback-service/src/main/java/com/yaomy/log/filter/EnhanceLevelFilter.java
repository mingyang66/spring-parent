package com.yaomy.log.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.FilterReply;

public class EnhanceLevelFilter extends LevelFilter {

    Level level;

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMessage().startsWith("{")
                && event.getMessage().endsWith("}")
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