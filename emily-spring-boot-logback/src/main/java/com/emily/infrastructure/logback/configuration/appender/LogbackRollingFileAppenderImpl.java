package com.emily.infrastructure.logback.configuration.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.OptionHelper;
import com.emily.infrastructure.common.utils.path.PathUtils;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.configuration.entity.LogbackAppender;
import com.emily.infrastructure.logback.configuration.enumeration.LogbackType;
import com.emily.infrastructure.logback.configuration.enumeration.RollingPolicyType;
import com.emily.infrastructure.logback.configuration.filter.LogbackFilter;
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
        //设置文件名
        rollingFileAppender.setFile(OptionHelper.substVars(MessageFormat.format("{0}{1}", loggerPath, ".log"), this.getLoggerContext()));

        if (RollingPolicyType.SIZE_AND_TIME_BASED.equals(this.getProperties().getRollingPolicy().getRollingPolicyType())) {
            //文件归档大小和时间设置
            SizeAndTimeBasedRollingPolicy policy = new SizeAndTimeBasedRollingPolicy();
            //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
            // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
            policy.setContext(this.getLoggerContext());
            /**
             * 归档文件名格式设置
             * 将文件名及路径字符串编译为字符串
             http://www.logback.cn/04%E7%AC%AC%E5%9B%9B%E7%AB%A0Appenders.html
             /info/foo.%d 每天归档
             /info/%d{yyyy/MM}/foo.txt 每个月开始的时候归档
             /info/foo.%d{yyyy-ww}.log 每个周的第一天开始归档
             /info/foo%d{yyyy-MM-dd_HH}.log 每小时归档
             /info/foo%d{yyyy-MM-dd_HH-mm}.log 每分钟归档
             /info/info.%d 每天轮转
             */
            String fp = OptionHelper.substVars(MessageFormat.format("{0}{1}", loggerPath, ".%d{yyyy-MM-dd}.%i.log"), this.getLoggerContext());
            //设置文件名模式
            policy.setFileNamePattern(fp);
            //最大日志文件大小 KB,MB,GB
            if (StringUtils.isNotEmpty(this.getProperties().getRollingPolicy().getMaxFileSize())) {
                policy.setMaxFileSize(FileSize.valueOf(this.getProperties().getRollingPolicy().getMaxFileSize()));
            }
            //设置要保留的最大存档文件数
            policy.setMaxHistory(this.getProperties().getMaxHistory());
            //文件总大小限制 KB,MB,G
            if (StringUtils.isNotEmpty(this.getProperties().getRollingPolicy().getTotalSizeCap())) {
                policy.setTotalSizeCap(FileSize.valueOf(this.getProperties().getRollingPolicy().getTotalSizeCap()));
            }
            //设置父节点是appender
            policy.setParent(rollingFileAppender);
            policy.start();

            //设置文件归档策略
            rollingFileAppender.setRollingPolicy(policy);
        } else {
            //文件归档大小和时间设置
            TimeBasedRollingPolicy policy = new TimeBasedRollingPolicy();
            //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
            // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
            policy.setContext(this.getLoggerContext());
            /**
             * 归档文件名格式设置
             * 将文件名及路径字符串编译为字符串
             http://www.logback.cn/04%E7%AC%AC%E5%9B%9B%E7%AB%A0Appenders.html
             /info/foo.%d 每天归档
             /info/%d{yyyy/MM}/foo.txt 每个月开始的时候归档
             /info/foo.%d{yyyy-ww}.log 每个周的第一天开始归档
             /info/foo%d{yyyy-MM-dd_HH}.log 每小时归档
             /info/foo%d{yyyy-MM-dd_HH-mm}.log 每分钟归档
             /info/info.%d 每天轮转
             */
            String fp = OptionHelper.substVars(StringUtils.join(loggerPath, "%d{yyyy-MM-dd}.log"), this.getLoggerContext());
            //设置文件名模式
            policy.setFileNamePattern(fp);
            //设置要保留的最大存档文件数
            policy.setMaxHistory(this.getProperties().getMaxHistory());
            //设置父节点是appender
            policy.setParent(rollingFileAppender);

            policy.start();

            //设置文件归档策略
            rollingFileAppender.setRollingPolicy(policy);

        }

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        encoder.setContext(this.getLoggerContext());
        //设置格式
        encoder.setPattern(this.getFilePattern());
        //设置编码格式
        encoder.setCharset(StandardCharsets.UTF_8);
        encoder.start();

        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        rollingFileAppender.setContext(this.getLoggerContext());
        //appender的name属性
        rollingFileAppender.setName(appenderName);
        //如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true
        rollingFileAppender.setAppend(true);
        //如果是 true，日志会被安全的写入文件，即使其他的FileAppender也在向此文件做写入操作，效率低，默认是 false
        rollingFileAppender.setPrudent(false);
        //设置过滤器
        rollingFileAppender.addFilter(LogbackFilter.getLevelFilter(level));

        //设置附加器编码
        rollingFileAppender.setEncoder(encoder);
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
