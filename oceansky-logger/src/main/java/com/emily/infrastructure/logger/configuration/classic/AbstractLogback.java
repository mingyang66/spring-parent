package com.emily.infrastructure.logger.configuration.classic;

import ch.qos.logback.classic.Logger;
import com.emily.infrastructure.logger.configuration.property.LogbackProperty;

/**
 * 日志实现抽象类
 *
 * @author Emily
 * @since : 2021/12/17
 */
public abstract class AbstractLogback {
    /**
     * 获取Logger对象
     *
     * @param property appender属性名
     * @return 返回Logger日志对象
     */
    public abstract Logger getLogger(LogbackProperty property);
}
