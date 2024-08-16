package com.emily.infrastructure.autoconfigure.tomcat.factory;

import com.emily.infrastructure.autoconfigure.tomcat.TomcatProperties;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

/**
 * 自定义策略接口，会在tomcat启动之后回调，会被WebServerFactoryCustomizerBeanPostProcessor类回调
 *
 * @author Emily
 * @since Created in 2022/6/22 1:37 下午
 */
public class TomcatServerCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    /**
     * 属性配置
     */
    private final TomcatProperties properties;

    public TomcatServerCustomizer(TomcatProperties properties) {
        this.properties = properties;
    }

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setPort(properties.getPort());
        //non-SSL to SSL（暂时不生效）
        connector.setRedirectPort(8081);
        factory.addAdditionalTomcatConnectors(connector);
    }
}
