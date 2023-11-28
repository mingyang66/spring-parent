package com.emily.infrastructure.logback;

import com.emily.infrastructure.logger.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * LogBack日志组件，加载配置文件优先级：https://logback.qos.ch/manual/configuration.html
 * 加载顺序：loback-test.xml-&gt;logback.xml-&gt;SPI com.qos.logback.classic.spi.Configurator模式-&gt;BasicConfigurator打印控制台
 *
 * @author Emily
 * @see <a href="https://logback.qos.ch/manual/configuration.html">...</a>
 * @since : 2020/08/08
 */
@AutoConfiguration
@EnableConfigurationProperties(LogbackProperties.class)
@ConditionalOnProperty(prefix = LogbackProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class LogbackAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(LogbackAutoConfiguration.class);

    @Override
    public void destroy() {
        logger.info("<== 【销毁--自动化配置】----Logger日志组件【LogbackAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        logger.info("==> 【初始化--自动化配置】----Logger日志组件【LogbackAutoConfiguration】");
    }
}
