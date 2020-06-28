package com.sgrain.boot.autoconfigure.https;

import com.sgrain.boot.autoconfigure.https.HttpsProperties;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HTTP自动跳转HTTPS自动化配置类
 * 生成证书命令：
 * keytool -genkey -alias michaelSpica  -storetype PKCS12 -keyalg RSA -keysize 2048  -keystore /Users/yaomingyang/Documents/IDE/workplace/security/keystore.p12 -validity 3650 -dname "CN=localhost, OU=localhost, O=localhost, L=SH, ST=SH, C=CN"
 */
@Configuration
@EnableConfigurationProperties(HttpsProperties.class)
@ConditionalOnProperty(prefix = "spring.sgrain.https", name = "enable", havingValue = "true", matchIfMissing = true)
public class HttpsConfiguration {

    /**
     * spring boot 2.0
     * @return
     */
    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint constraint = new SecurityConstraint();
                constraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                constraint.addCollection(collection);
                context.addConstraint(constraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(httpConnector());
        return tomcat;
    }

    @Bean
    public Connector httpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        //设置请求方案
        connector.setScheme("http");
        //Connector监听的http的端口号
        connector.setPort(9001);
        //是否通过此连接器接收的所有请求上设置安全连接标志
        connector.setSecure(false);
        //监听到http的端口号后转向到的https的端口号
        connector.setRedirectPort(9000);
        return connector;
    }

}