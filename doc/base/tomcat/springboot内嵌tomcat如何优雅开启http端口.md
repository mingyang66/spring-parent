#### springboot内嵌tomcat如何优雅开启http端口

>
springboot默认使用的是内置tomcat，默认是只可以开启一个端口，如果开启了https服务，并且想同时可以通过http访问，如何开启第二个端口号呢？网上有很多解决方案，大都是通过new一个TomcatServletWebServerFactory实例对象，这样其实是在springboot内部创建了两个容器，而不是在一个容器内部开启两个端口。

##### 一、创建WebServerFactoryCustomizer自定义策略接口实现类

```java
package com.emily.infrastructure.autoconfigure.tomcat.factory;

import com.emily.infrastructure.autoconfigure.tomcat.TomcatProperties;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

/**
 * 自定义策略接口，会在tomcat启动之后回调，会被WebServerFactoryCustomizerBeanPostProcessor类回调
 * @author  Emily
 * @since  Created in 2022/6/22 1:37 下午
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

```

##### 二、创建属性配置类TomcatProperties

```java
package com.emily.infrastructure.autoconfigure.tomcat;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Emily
 *  http属性配置文件
 * @create: 2020/06/28
 */
@ConfigurationProperties(prefix = "server.http")
public class TomcatProperties {
    /**
     * 是否开启http服务
     */
    private boolean enabled;
    /**
     * 端口号
     */
    private int port = 8080;


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

```

##### 三、创建配置类TomcatAutoConfiguration

```java
package com.emily.infrastructure.autoconfigure.tomcat;

import com.emily.infrastructure.autoconfigure.tomcat.factory.TomcatServerCustomizer;
import com.emily.infrastructure.logger.LoggerFactory;
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
```

>
如需配置自动化配置可在META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports文件中配置上配置类即可（springboot2.7.0）

通过上述三个简单类完美实现在同一个容器内开启第二个端口，启动信息如下：

```
 o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 9000 (https) 9001 (http) with context path ''
```

示例GitHub参考：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)