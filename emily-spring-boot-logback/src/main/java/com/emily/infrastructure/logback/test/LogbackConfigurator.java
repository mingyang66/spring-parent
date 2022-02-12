package com.emily.infrastructure.logback.test;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.core.spi.ContextAwareBase;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/12/31
 */
public class LogbackConfigurator extends ContextAwareBase implements Configurator {
    public LogbackConfigurator() {
    }

    @Override
    public void configure(LoggerContext lc) {
        //String s = System.getProperty("user.dir");
        // System.out.println(s);
    }
}
