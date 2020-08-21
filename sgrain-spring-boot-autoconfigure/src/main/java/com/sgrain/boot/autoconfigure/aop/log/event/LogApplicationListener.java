package com.sgrain.boot.autoconfigure.aop.log.event;

import ch.qos.logback.classic.Level;
import com.sgrain.boot.common.utils.LoggerUtils;
import com.sgrain.boot.common.utils.json.JSONUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @program: spring-parent
 * @description: 日志监听器
 * @create: 2020/08/07
 */
@Component
public class LogApplicationListener implements ApplicationListener<LogApplicationEvent> {
    @Override
    public void onApplicationEvent(LogApplicationEvent event) {
        if (event.getSource() instanceof LogAop) {
            LogAop logAop = (LogAop) event.getSource();
            if (StringUtils.equalsIgnoreCase(Level.INFO.levelStr, logAop.getLogLevel())) {
                if (LoggerUtils.isDebug()) {
                    LoggerUtils.info(logAop.getaClass(), JSONUtils.toJSONPrettyString(logAop.getTraceLog()));
                } else {
                    LoggerUtils.info(logAop.getaClass(), JSONUtils.toJSONString(logAop.getTraceLog()));
                }
            } else if (StringUtils.equalsIgnoreCase(Level.ERROR.levelStr, logAop.getLogLevel())) {
                if (LoggerUtils.isDebug()) {
                    LoggerUtils.error(logAop.getaClass(), JSONUtils.toJSONPrettyString(logAop.getTraceLog()));
                } else {
                    LoggerUtils.error(logAop.getaClass(), JSONUtils.toJSONString(logAop.getTraceLog()));
                }
            }
        }
    }

}
