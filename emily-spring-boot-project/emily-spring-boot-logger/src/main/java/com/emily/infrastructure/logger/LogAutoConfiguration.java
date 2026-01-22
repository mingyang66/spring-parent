package com.emily.infrastructure.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * LogBack日志组件，加载配置文件优先级：<a href="https://logback.qos.ch/manual/configuration.html">...</a>
 * 加载顺序：logback-test.xml-&gt;logback.xml-&gt;SPI com.qos.logback.classic.spi.Configurator模式-&gt;BasicConfigurator打印控制台
 *
 * @author Emily
 * @see <a href="https://logback.qos.ch/manual/configuration.html">...</a>
 * @since : 2020/08/08
 */
@AutoConfiguration
@EnableConfigurationProperties(LogProperties.class)
@ConditionalOnProperty(prefix = LogProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class LogAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(LogAutoConfiguration.class);

    @Override
    public void destroy() {
        LOG.info("<== 【销毁--自动化配置】----Logger日志组件【LoggerAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        LOG.info("==> 【初始化--自动化配置】----Logger日志组件【LoggerAutoConfiguration】");
    }
}
