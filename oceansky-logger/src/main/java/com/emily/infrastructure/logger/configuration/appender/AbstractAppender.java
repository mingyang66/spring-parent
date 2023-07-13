package com.emily.infrastructure.logger.configuration.appender;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.emily.infrastructure.logger.manager.LoggerCacheManager;

/**
 * @program: spring-parent
 * @description: Appender抽象类
 * @author: Emily
 * @create: 2022/01/04
 */
public abstract class AbstractAppender {
    /**
     * 获取Appender实例对象
     *
     * @param level logger level
     * @return appender instance
     */
    public Appender<ILoggingEvent> newInstance(Level level) {
        //appender名称重新拼接
        String appenderName = this.getAppenderName(level);
        //如果已经存在，则忽略，否则添加
        LoggerCacheManager.APPENDER.putIfAbsent(appenderName, this.getAppender(level));
        // return appender object
        return LoggerCacheManager.APPENDER.get(appenderName);
    }

    /**
     * 获取appender对象
     *
     * @param level appender过滤日志级别
     * @return appender对象
     */
    protected abstract Appender<ILoggingEvent> getAppender(Level level);

    /**
     * 获取文件路径
     *
     * @param level 日志级别
     * @return 日志文件路径
     */
    protected abstract String resolveFilePath(Level level);

    /**
     * 获取日志输出格式
     *
     * @return 日志文件输出格式
     */
    protected abstract String resolveFilePattern();

    /**
     * 获取appenderName
     *
     * @param level 日志级别
     * @return appender name
     */
    protected abstract String getAppenderName(Level level);

}
