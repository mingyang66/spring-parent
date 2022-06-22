package com.emily.infrastructure.autoconfigure.server.factory;

import com.emily.infrastructure.autoconfigure.server.ServerProperties;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

/**
 * @Description :
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
    private ServerProperties properties;

    public TomcatServerCustomizer(ServerProperties properties) {
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
