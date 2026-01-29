package com.emily.infrastructure.logback.configuration.policy;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.common.StrUtils;
import com.emily.infrastructure.logback.configuration.type.RollingPolicyType;

/**
 * logback归档策略
 *
 * @author Emily
 * @since : 2022/01/10
 */
public class LogbackFixedWindowRollingPolicy extends AbstractRollingPolicy {
    private final LoggerContext context;
    private final LogbackProperties properties;

    public LogbackFixedWindowRollingPolicy(LoggerContext context, LogbackProperties properties) {
        this.context = context;
        this.properties = properties;
    }

    @Override
    public boolean support(RollingPolicyType type) {
        return RollingPolicyType.FIXED_WINDOW == type;
    }

    /**
     * 获取基于时间的文件归档策略
     * 将文件名及路径字符串编译为字符串
     * <a href="http://www.logback.cn/04%E7%AC%AC%E5%9B%9B%E7%AB%A0Appenders.html">...</a>
     * /info/foo.%d 每天归档
     * /info/%d{yyyy/MM}/foo.txt 每个月开始的时候归档
     * /info/foo.%d{yyyy-ww}.log 每个周的第一天开始归档
     * /info/foo%d{yyyy-MM-dd_HH}.log 每小时归档
     * /info/foo%d{yyyy-MM-dd_HH-mm}.log 每分钟归档
     * /info/info.%d 每天轮转
     *
     * @param appender   归档文件appender
     * @param loggerPath 日志文件路径
     * @return 基于时间的滚动策略
     */
    @Override
    public RollingPolicy getRollingPolicy(RollingFileAppender<ILoggingEvent> appender, String loggerPath) {
        //文件归档大小和时间设置
        FixedWindowRollingPolicy policy = new FixedWindowRollingPolicy();
        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        //但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        policy.setContext(context);
        //归档文件名格式设置
        String fp = StrUtils.substVars(context, loggerPath, "%d{yyyy-MM-dd}%i.log");
        //设置文件名模式，支持对文件进行压缩ZIP、GZ todo 带打开
        //policy.setFileNamePattern(StrUtils.join(fp, properties.getAppender().getRollingPolicy().getCompressionMode().getSuffix()));
        policy.setMinIndex(1);
        policy.setMaxIndex(10);
        //设置父节点是appender
        policy.setParent(appender);
        //添加内部状态
        policy.addInfo("Build FixedWindowRollingPolicy Success");
        policy.start();
        return policy;
    }
}
