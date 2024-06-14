package com.emily.infrastructure.autoconfigure.tomcat;

import com.emily.infrastructure.autoconfigure.tomcat.factory.TomcatServerCustomizer;
import com.emily.infrastructure.logback.factory.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * HTTP自动跳转HTTPS自动化配置类
 * 生成证书命令：
 * keytool -genkey -alias michaelSpica  -storetype PKCS12 -keyalg RSA -keysize 2048  -keystore /Users/Emily/Documents/IDE/workplace/security/keystore.p12 -validity 3650 -dname "CN=localhost, OU=localhost, O=localhost, L=SH, ST=SH, C=CN"
 *
 * @author Emily
 */
@AutoConfiguration
@EnableConfigurationProperties(TomcatProperties.class)
@ConditionalOnProperty(prefix = "server.http", name = "enabled", havingValue = "true", matchIfMissing = false)
public class TomcatAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(TomcatAutoConfiguration.class);

    @Bean
    public TomcatServerCustomizer tomcatServerCustomizer(TomcatProperties properties) {
        return new TomcatServerCustomizer(properties);
    }

    @Override
    public void destroy() throws Exception {
        logger.info("<== 【销毁--自动化配置】----自定义tomcat服务器");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("==> 【初始化--自动化配置】----自定义tomcat服务器");
    }
}