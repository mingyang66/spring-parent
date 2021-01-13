package com.emily.boot.test;


import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestBootStrap {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        SpringApplication application = new SpringApplication(TestBootStrap.class);
        application.run(args);

        long end = System.currentTimeMillis();
        System.out.println("启动耗时：" + (end - start));
    }

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(createStandardConnector());
        return tomcat;
    }

    private Connector createStandardConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(9088);
        return connector;
    }
}
