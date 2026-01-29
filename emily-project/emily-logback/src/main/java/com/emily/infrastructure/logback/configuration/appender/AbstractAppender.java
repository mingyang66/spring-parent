package com.emily.infrastructure.logback.configuration.appender;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import com.emily.infrastructure.logback.factory.LogBeanFactory;

/**
 * Appender抽象类
 *
 * @author Emily
 * @since : 2022/01/04
 */
public abstract class AbstractAppender {
    /**
     * 获取Appender实例对象
     *
     * @param level logger level
     * @return appender instance
     */
    public Appender<ILoggingEvent> build(Level level) {
        //appender名称重新拼接
        String appenderName = this.getName(level);
        //如果已经存在，则忽略，否则添加
        LogBeanFactory.registerBean(appenderName, this.getAppender(level));
        // return appender object
        return LogBeanFactory.getBean(appenderName);
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
    protected abstract String getFilePath(Level level);

    /**
     * 获取日志输出格式
     *
     * @return 日志文件输出格式
     */
    protected abstract String getFilePattern();

    /**
     * 获取appenderName
     *
     * @param level 日志级别
     * @return appender name
     */
    protected abstract String getName(Level level);

}
