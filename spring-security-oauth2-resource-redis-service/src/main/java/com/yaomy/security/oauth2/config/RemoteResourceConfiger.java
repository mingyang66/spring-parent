package com.yaomy.security.oauth2.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
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
public class RemoteResourceConfiger implements InitializingBean {

    @Value("${oauth.token.uri}")
    private String tokenUri;

    @Value("${oauth.resource.id}")
    private String resourceId;

    @Value("${oauth.resource.client.id}")
    private String resourceClientId;

    @Value("${oauth.resource.client.secret}")
    private String resourceClientSecret;

    @Value("${oauth.resource.user.id}")
    private String resourceUserId;

    @Value("${oauth.resource.user.password}")
    private String resourceUserPassword;

    @Bean
    public OAuth2RestOperations restTemplate() {
        OAuth2RestTemplate template = new OAuth2RestTemplate(resource());
        ResourceOwnerPasswordAccessTokenProvider provider = new ResourceOwnerPasswordAccessTokenProvider();
        template.setAccessTokenProvider(provider);
        return template;
    }

    private ResourceOwnerPasswordResourceDetails resource(){
        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
        resource.setId(resourceId);
        resource.setClientId(resourceClientId);
        resource.setClientSecret(resourceClientSecret);
        resource.setGrantType("password");
        resource.setAccessTokenUri(tokenUri);
        resource.setUsername(resourceUserId);
        resource.setPassword(resourceUserPassword);
        resource.setScope(Arrays.asList("test"));
        return resource;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("init RemoteResourceConfiger-----------------");
    }
}
