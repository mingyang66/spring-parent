package com.emily.infrastructure.logger.configuration.classic;

import ch.qos.logback.classic.LoggerContext;
import com.emily.infrastructure.logger.common.CommonKeys;
import com.emily.infrastructure.logger.configuration.property.LoggerProperties;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Logback对象导演类
 *
 * @author :  Emily
 * @since :  2023/10/17 9:10 PM
 */
public class LogbackAdvice {
    public static final List<AbstractLogback> CONTAINER = new ArrayList<>();

    public LogbackAdvice(LoggerProperties properties, LoggerContext lc) {
        CONTAINER.add(new LogbackGroup(properties, lc));
        CONTAINER.add(new LogbackModule(properties, lc));
        CONTAINER.add(new LogbackRoot(properties, lc));
    }

    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param commonKeys 属性配置上下文传递类
     */
    public Logger processLogger(CommonKeys commonKeys) {
        Optional<AbstractLogback> logback = CONTAINER.stream().filter(l -> l.supports(commonKeys.getLogbackType())).findFirst();
        if (logback.isPresent()) {
            return logback.get().getLogger(commonKeys);
        }
        throw new IllegalArgumentException("非法参数");
    }
}
