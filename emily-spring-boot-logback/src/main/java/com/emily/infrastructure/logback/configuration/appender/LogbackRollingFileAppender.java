package com.emily.infrastructure.logback.configuration.appender;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.OptionHelper;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.configuration.entity.LogbackAppender;
import com.emily.infrastructure.logback.configuration.filter.LogbackFilter;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author Emily
 * @description: 通过名字和级别设置Appender
 * @create: 2020/08/04
 */
public class LogbackRollingFileAppender {
    /**
     * logger上下文
     */
    private LoggerContext loggerContext;
    /**
     * 属性配置
     */
    private LogbackProperties properties;

    public LogbackRollingFileAppender(LoggerContext loggerContext, LogbackProperties properties) {
        this.loggerContext = loggerContext;
        this.properties = properties;
    }

    /**
     * 获取按照时间归档文件附加器对象
     *
     * @return
     */
    public RollingFileAppender getRollingFileAppender(LogbackAppender logbackAppender) {
        //这里是可以用来设置appender的，在xml配置文件里面，是这种形式：
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        //日志文件路径
        String loggerPath = logbackAppender.getFilePath(properties);
        //设置文件名
        appender.setFile(OptionHelper.substVars(StringUtils.join(loggerPath, ".log"), loggerContext));
        //获取过滤器
        LevelFilter levelFilter = LogbackFilter.getLevelFilter(logbackAppender.getLevel());
        levelFilter.start();
        if (properties.isSizeAndTimeRollingPolicy()) {
            //文件归档大小和时间设置
            SizeAndTimeBasedRollingPolicy policy = new SizeAndTimeBasedRollingPolicy();
            //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
            // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
            policy.setContext(loggerContext);
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
            String fp = OptionHelper.substVars(StringUtils.join(loggerPath, ".%d{yyyy-MM-dd}.%i.log"), loggerContext);
            //设置文件名模式
            policy.setFileNamePattern(fp);
            //最大日志文件大小 KB,MB,GB
            if (StringUtils.isNotEmpty(properties.getMaxFileSize())) {
                policy.setMaxFileSize(FileSize.valueOf(properties.getMaxFileSize()));
            }
            //设置要保留的最大存档文件数
            policy.setMaxHistory(properties.getMaxHistory());
            //文件总大小限制 KB,MB,G
            if (StringUtils.isNotEmpty(properties.getTotalSizeCap())) {
                policy.setTotalSizeCap(FileSize.valueOf(properties.getTotalSizeCap()));
            }
            //设置父节点是appender
            policy.setParent(appender);
            policy.start();

            //设置文件归档策略
            appender.setRollingPolicy(policy);
        } else {
            //文件归档大小和时间设置
            TimeBasedRollingPolicy policy = new TimeBasedRollingPolicy();
            //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
            // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
            policy.setContext(loggerContext);
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
            String fp = OptionHelper.substVars(StringUtils.join(loggerPath, "%d{yyyy-MM-dd}.log"), loggerContext);
            //设置文件名模式
            policy.setFileNamePattern(fp);
            //设置要保留的最大存档文件数
            policy.setMaxHistory(properties.getMaxHistory());
            //设置父节点是appender
            policy.setParent(appender);

            policy.start();

            //设置文件归档策略
            appender.setRollingPolicy(policy);

        }

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        encoder.setContext(loggerContext);
        //设置格式
        encoder.setPattern(logbackAppender.getFilePattern(properties));
        //设置编码格式
        encoder.setCharset(StandardCharsets.UTF_8);
        encoder.start();

        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        appender.setContext(loggerContext);
        //appender的name属性
        appender.setName(logbackAppender.getAppenderName());
        //如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true
        appender.setAppend(true);
        //如果是 true，日志会被安全的写入文件，即使其他的FileAppender也在向此文件做写入操作，效率低，默认是 false
        appender.setPrudent(false);
        //设置过滤器
        appender.addFilter(levelFilter);

        //设置附加器编码
        appender.setEncoder(encoder);
        appender.start();

        return appender;
    }
}
