package com.emily.boot.test.server;

import com.emily.framework.autoconfigure.response.wrapper.ResponseWrapperAutoConfiguration;
import com.emily.framework.common.utils.log.LoggerUtils;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HTTP自动跳转HTTPS自动化配置类
 * 生成证书命令：
 * keytool -genkey -alias michaelSpica  -storetype PKCS12 -keyalg RSA -keysize 2048  -keystore /Users/yaomingyang/Documents/IDE/workplace/security/keystore.p12 -validity 3650 -dname "CN=localhost, OU=localhost, O=localhost, L=SH, ST=SH, C=CN"
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ServerProperties.class)
@ConditionalOnProperty(prefix = "server.http", name = "enable", havingValue = "true", matchIfMissing = false)
public class TomcatServerAutoConfiguration implements InitializingBean, DisposableBean {


    @Bean
    public TomcatServletWebServerFactory servletContainer(ServerProperties serverProperties) {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(createStandardConnector(serverProperties));
        return tomcat;
    }

    private Connector createStandardConnector(ServerProperties serverProperties) {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(serverProperties.getPort());
        return connector;
    }

    @Override
    public void destroy() throws Exception {
        LoggerUtils.info(ResponseWrapperAutoConfiguration.class, "【销毁--自动化配置】----自定义tomcat服务器");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(ResponseWrapperAutoConfiguration.class, "【初始化--自动化配置】----自定义tomcat服务器");
    }
}