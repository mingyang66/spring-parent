package com.yaomy.security.oauth2.config;

import com.yaomy.security.oauth2.enhancer.UserTokenEnhancer;
import com.yaomy.security.oauth2.service.OAuth2ClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import java.util.Arrays;

/**
 * @Description: @EnableAuthorizationServer注解开启OAuth2授权服务机制
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.config.OAuth2ServerConfig
 * @Date: 2019/7/9 11:26
 * @Version: 1.0
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2ServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private OAuth2ClientDetailsService oAuth2ClientDetailsService;

    @Autowired
    private ApprovalStore approvalStore;
    /**
     用来配置客户端详情服务（ClientDetailsService），客户端详情信息在这里初始化，
     你可以把客户端详情信息写死也可以写入内存或者数据库中
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //使用自定义ClientDetailsService初始化配置
        clients.withClientDetails(oAuth2ClientDetailsService);
    }
    /**
     用来配置授权（authorization）以及令牌（token)的访问端点和令牌服务（token services）
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        enhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer()));
        endpoints.tokenStore(tokenStore)
                .approvalStore(approvalStore)
                .authenticationManager(authenticationManager)
                .tokenEnhancer(enhancerChain);
    }
    /**
     用来配置令牌端点（Token Endpoint）的安全约束
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.realm("OAuth2-Sample")
                .allowFormAuthenticationForClients()
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }
    /**
     * @Description OAuth2 token持久化接口
     * @Date 2019/7/9 17:45
     * @Version  1.0
     */
    @Bean
    public TokenStore tokenStore() {
        //token保存在内存中（也可以保存在数据库、Redis中）。
        //如果保存在中间件（数据库、Redis），那么资源服务器与认证服务器可以不在同一个工程中。
        //注意：如果不保存access_token，则没法通过access_token取得用户信息
        return new InMemoryTokenStore();
    }

    @Bean
    public ApprovalStore approvalStore() throws Exception {
        TokenApprovalStore store = new TokenApprovalStore();
        store.setTokenStore(tokenStore);
        return store;
    }
    /**
     * @Description 自定义生成令牌token
     * @Date 2019/7/9 19:58
     * @Version  1.0
     */
    @Bean
    public TokenEnhancer tokenEnhancer(){
        return new UserTokenEnhancer();
    }
}
