package com.yaomy.security.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import java.util.Arrays;

/**
 * @Description: 资源服务器访问认证服务器资源配置 示例：https://www.programcreek.com/java-api-examples/index.php?api=org.springframework.security.oauth2.client.OAuth2RestOperations
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.config.RemoteResourceConfiguration
 * @Date: 2019/7/22 17:54
 * @Version: 1.0
 */
@Configuration
@EnableOAuth2Client
public class ClientResourceConfiger {

    @Bean
    public OAuth2RestOperations restTemplate() {
        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
        resource.setId("resource_password_id");
        resource.setClientId("client_password");
        resource.setClientSecret("secret");
        resource.setGrantType("password");
        resource.setAccessTokenUri("http://127.0.0.1:9003/oauth/token");
        resource.setUsername("user");
        resource.setPassword("123");
        resource.setScope(Arrays.asList("test"));

        return new OAuth2RestTemplate(resource);
    }

}
