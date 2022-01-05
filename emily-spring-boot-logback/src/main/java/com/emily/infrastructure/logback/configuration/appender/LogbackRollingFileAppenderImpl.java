package com.emily.infrastructure.logback.configuration.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.OptionHelper;
import com.emily.infrastructure.common.utils.path.PathUtils;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.configuration.encoder.LogbackEncoder;
import com.emily.infrastructure.logback.configuration.entity.LogbackAppender;
import com.emily.infrastructure.logback.configuration.enumeration.LogbackType;
import com.emily.infrastructure.logback.configuration.enumeration.RollingPolicyType;
import com.emily.infrastructure.logback.configuration.filter.LogbackFilter;
import com.emily.infrastructure.logback.configuration.policy.LogbackRollingPolicy;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Emily
 * @description: 通过名字和级别设置Appender
 * @create: 2020/08/04
 */
public class LogbackRollingFileAppenderImpl extends AbstractAppender {

    private static final Map<String, RollingFileAppender> APPENDER = new ConcurrentHashMap<>();

    private LogbackAppender appender;

    public LogbackRollingFileAppenderImpl(LoggerContext loggerContext, LogbackProperties properties, LogbackAppender appender) {
        super(loggerContext, properties);
        this.appender = appender;
    }

    /**
     * 获取按照时间归档文件附加器对象
     *
     * @return
     */
    @Override
    public Appender<ILoggingEvent> getAppender(Level level) {
        //appender名称重新拼接
        String appenderName = MessageFormat.format("{0}_{1}", appender.getAppenderName(), level.levelStr.toLowerCase());
        //如果已经存在，则复用
        if (APPENDER.containsKey(appenderName)) {
            return APPENDER.get(appenderName);
        }
        //这里是可以用来设置appender的，在xml配置文件里面，是这种形式：
        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
        //日志文件路径
        String loggerPath = this.getFilePath(level);
        //日志文件归档策略
        RollingPolicy policy;
        if (RollingPolicyType.SIZE_AND_TIME_BASED.equals(this.getProperties().getRollingPolicy().getRollingPolicyType())) {
            policy = LogbackRollingPolicy.getSizeAndTimeBasedRollingPolicy(this.getLoggerContext(), this.getProperties(), rollingFileAppender, loggerPath);
        } else {
            policy = LogbackRollingPolicy.getTimeBasedRollingPolicy(this.getLoggerContext(), this.getProperties(), rollingFileAppender, loggerPath);
        }
        //设置文件名
        rollingFileAppender.setFile(OptionHelper.substVars(MessageFormat.format("{0}{1}", loggerPath, ".log"), this.getLoggerContext()));
        //设置日志文件归档策略
        rollingFileAppender.setRollingPolicy(policy);
        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        rollingFileAppender.setContext(this.getLoggerContext());
        //appender的name属性
        rollingFileAppender.setName(appenderName);
        //如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true
        rollingFileAppender.setAppend(true);
        //如果是 true，日志会被安全的写入文件，即使其他的FileAppender也在向此文件做写入操作，效率低，默认是 false|Support multiple-JVM writing to the same log file
        rollingFileAppender.setPrudent(false);
        //设置过滤器
        rollingFileAppender.addFilter(LogbackFilter.getLevelFilter(level));
        //设置附加器编码
        rollingFileAppender.setEncoder(LogbackEncoder.getPatternLayoutEncoder(this.getLoggerContext(), this.getFilePattern()));
        //设置是否里面将输出流刷新，确保日志信息不丢失，默认：true
        rollingFileAppender.setImmediateFlush(true);
        rollingFileAppender.start();

        //生成appender存入缓存
        APPENDER.put(appenderName, rollingFileAppender);

        return rollingFileAppender;
    }

    /**
     * 获取文件路径
     *
     * @param level 日志级别
     * @return
     */
    @Override
    public String getFilePath(Level level) {
        //基础相对路径
        String basePath = this.getProperties().getBasePath();
        //文件路径
        String filePath = PathUtils.normalizePath(appender.getFilePath());
        //日志级别
        String levelStr = level.levelStr.toLowerCase();
        //基础日志
        if (LogbackType.ROOT.equals(appender.getLogbackType())) {
            return StringUtils.join(basePath, filePath, File.separator, levelStr, File.separator, levelStr);
        }
        //分模块日志
        if (LogbackType.MODULE.equals(appender.getLogbackType())) {
            return StringUtils.join(basePath, filePath, File.separator, appender.getFileName());
        }
        //分组日志
        if (StringUtils.isEmpty(appender.getFileName())) {
            return StringUtils.join(basePath, filePath, File.separator, levelStr, File.separator, levelStr);
        }
        return StringUtils.join(basePath, filePath, File.separator, levelStr, File.separator, appender.getFileName());
    }

    /**
     * 获取日志输出格式
     *
     * @return 日志格式
     */
    @Override
    public String getFilePattern() {
        //基础日志
        if (LogbackType.ROOT.equals(appender.getLogbackType())) {
            return this.getProperties().getRoot().getPattern();
        }
        //分组
        if (LogbackType.GROUP.equals(appender.getLogbackType())) {
            return this.getProperties().getGroup().getPattern();
        }
        //分模块
        return this.getProperties().getModule().getPattern();
    }
}
