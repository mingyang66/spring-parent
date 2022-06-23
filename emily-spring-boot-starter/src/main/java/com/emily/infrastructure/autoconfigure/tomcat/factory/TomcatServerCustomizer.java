package com.emily.infrastructure.autoconfigure.tomcat.factory;

import com.emily.infrastructure.autoconfigure.tomcat.TomcatProperties;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

/**
 * @Description : 自定义策略接口，会在tomcat启动之后回调，会被WebServerFactoryCustomizerBeanPostProcessor类回调
 * @Author :  Emily
 * @CreateDate :  Created in 2022/6/22 1:37 下午
 */
public class TomcatServerCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    /**
     * 协议
     */
    private static final String SCHEME = "http";
    /**
     * 属性配置
     */
    private TomcatProperties properties;

    public TomcatServerCustomizer(TomcatProperties properties) {
        this.properties = properties;
    }

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(properties.getPort());
        connector.setScheme(SCHEME);
        factory.addAdditionalTomcatConnectors(connector);
    }
}
