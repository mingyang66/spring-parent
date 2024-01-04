package com.emily.infrastructure.logger.configuration.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import com.emily.infrastructure.logger.common.CommonKeys;
import com.emily.infrastructure.logger.common.PathUtils;
import com.emily.infrastructure.logger.common.StrUtils;
import com.emily.infrastructure.logger.configuration.context.LogbackBeanFactory;
import com.emily.infrastructure.logger.configuration.property.LoggerConfig;
import com.emily.infrastructure.logger.configuration.type.LogbackType;

import java.io.File;
import java.text.MessageFormat;

/**
 * 通过名字和级别设置Appender
 *
 * @author Emily
 * @since : 2020/08/04
 */
public class LogbackRollingFileAppender extends AbstractAppender {
    /**
     * 属性配置
     */
    private final CommonKeys commonKeys;
    /**
     * 属性配置
     */
    private final LoggerConfig config;
    /**
     * logger上下文
     */
    private final LoggerContext lc;

    private LogbackRollingFileAppender(LoggerConfig config, LoggerContext lc, CommonKeys commonKeys) {
        this.config = config;
        this.lc = lc;
        this.commonKeys = commonKeys;
    }

    /**
     * 获取按照时间归档文件附加器对象
     *
     * @param level 日志级别
     * @return appender
     */
    @Override
    protected Appender<ILoggingEvent> getAppender(Level level) {
        //归档策略属性配置
        LoggerConfig.RollingPolicy rp = config.getAppender().getRollingPolicy();
        //日志文件路径
        String loggerPath = this.resolveFilePath(level);
        //这里是可以用来设置appender的，在xml配置文件里面，是这种形式：
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        //设置文件名，policy激活后才可以从appender获取文件路径
        appender.setFile(loggerPath);
        //设置日志文件归档策略
        appender.setRollingPolicy(LogbackBeanFactory.getRollingPolicy(appender, loggerPath, rp));
        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        appender.setContext(lc);
        //appender的name属性
        appender.setName(this.resolveName(level));
        //如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true
        appender.setAppend(config.getAppender().isAppend());
        //如果是 true，日志会被安全的写入文件，即使其他的appender也在向此文件做写入操作，效率低，默认是 false|Support multiple-JVM writing to the same log file
        appender.setPrudent(config.getAppender().isPrudent());
        //设置过滤器
        appender.addFilter(LogbackBeanFactory.getFilter().getLevelFilter(level));
        //设置附加器编码
        appender.setEncoder(LogbackBeanFactory.getEncoder(this.resolveFilePattern()));
        //设置是否将输出流刷新，确保日志信息不丢失，默认：true
        appender.setImmediateFlush(config.getAppender().isImmediateFlush());
        appender.start();
        return appender;
    }

    /**
     * 获取文件路径
     *
     * @param level 日志级别
     * @return 日志文件路径
     */
    @Override
    protected String resolveFilePath(Level level) {
        //基础相对路径
        String basePath = config.getAppender().getPath();
        //文件路径
        String filePath = commonKeys.getFilePath();
        //日志级别
        String levelStr = level.levelStr.toLowerCase();
        // 基础路径
        String loggerPath = StrUtils.join(basePath, filePath, File.separator);
        //基础日志、分组日志
        if (LogbackType.ROOT.equals(commonKeys.getLogbackType()) || LogbackType.GROUP.equals(commonKeys.getLogbackType())) {
            loggerPath = StrUtils.join(loggerPath, levelStr, File.separator, levelStr);
        }
        //分模块日志
        else if (LogbackType.MODULE.equals(commonKeys.getLogbackType())) {
            loggerPath = StrUtils.join(loggerPath, commonKeys.getFileName());
        } else {
            throw new UnsupportedOperationException("Unsupported log type");
        }
        return StrUtils.substVars(lc, loggerPath, ".log");
    }

    /**
     * 获取日志输出格式
     *
     * @return 日志格式
     */
    @Override
    protected String resolveFilePattern() {
        //基础日志
        if (LogbackType.ROOT.equals(commonKeys.getLogbackType())) {
            return config.getRoot().getPattern();
        }
        //分组
        if (LogbackType.GROUP.equals(commonKeys.getLogbackType())) {
            return config.getGroup().getPattern();
        }
        //分模块
        return config.getModule().getPattern();
    }

    /**
     * 日志级别
     * 拼接规则：分组.路径.文件名.日志级别
     *
     * @param level 日志级别
     * @return appender name值
     */
    @Override
    protected String resolveName(Level level) {
        String fileName = commonKeys.getFileName();
        if (StrUtils.isEmpty(fileName)) {
            fileName = level.levelStr.toLowerCase();
        }
        //拼装appender name
        return MessageFormat.format("{0}{1}.{2}.{3}", commonKeys.getLogbackType(), commonKeys.getFilePath(), fileName, level.levelStr.toLowerCase()).replace(PathUtils.SLASH, PathUtils.DOT);
    }

    public static LogbackRollingFileAppender create(LoggerConfig config, LoggerContext lc, CommonKeys commonKeys) {
        return new LogbackRollingFileAppender(config, lc, commonKeys);
    }
}
