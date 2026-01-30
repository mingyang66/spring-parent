package com.emily.infrastructure.logback.configuration.appender;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.emily.infrastructure.logback.common.StrUtils;

/**
 * Appender抽象类
 *
 * @author Emily
 * @since : 2022/01/04
 */
public interface LogbackAppender {
    /**
     * 获取appender对象
     *
     * @param level appender过滤日志级别
     * @return appender对象
     */
    Appender<ILoggingEvent> getAppender(Level level);

    /**
     * 容器中存在忽略，否则注册，获取对应bean对象
     */
    Appender<ILoggingEvent> registerAndGet(Level level);

    /**
     * 获取文件路径
     *
     * @param level 日志级别
     * @return 日志文件路径
     */
    default String getFilePath(Level level) {
        return StrUtils.EMPTY;
    }

    /**
     * 获取日志输出格式
     *
     * @return 日志文件输出格式
     */
    String getFilePattern();

    /**
     * 获取appenderName
     *
     * @param level 日志级别
     * @return appender name
     */
    String getName(Level level);

}
