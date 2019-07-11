package com.yaomy.security.oauth2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.service.OAuth2ClientDetailsService
 * @Author: 姚明洋
 * @Date: 2019/7/9 16:25
 * @Version: 1.0
 */
@Service
public class OAuth2ClientDetailsService implements ClientDetailsService {

    private ClientDetailsService clientDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    /**
     被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器调用一次，类似于Serclet的inti()方法。
     被@PostConstruct修饰的方法会在构造函数之后，init()方法之前运行。
     */
    @PostConstruct
    public void init(){
        InMemoryClientDetailsServiceBuilder inMemoryClientDetailsServiceBuilder = new InMemoryClientDetailsServiceBuilder();
        inMemoryClientDetailsServiceBuilder
                .withClient("client")
                    // client_secret
                    .secret(passwordEncoder.encode("secret"))
                    /**
                     ----支持的认证授权类型----
                     授权模式：
                     授权码模式（authorization_code）
                     --client_id：客户端ID，必选
                     --response_type：必须为code，必选
                     --redirect_uri：回掉url,必选
                     简化模式（implicit）
                     密码模式（password）
                     客户端模式（client_credentials）
                     */
                    .authorizedGrantTypes("authorization_code","password", "implicit", "client_credentials", "refresh_token")
                    //回调uri，在authorization_code与implicit授权方式时，用以接收服务器的返回信息
                    .redirectUris("http://localhost:9001/auth_user/get_auth_code")
                    // 允许的授权范围
                    .scopes("insert","update","del", "select", "replace")
                    .and()
                .withClient("client_password")
                    .secret(passwordEncoder.encode("secret"))
                    /**
                     ----密码模式---
                     --client_id：客户端ID，必选
                     --client_secret：客户端密码，必选
                     --grant_type：必须为password，必选
                     --username:用户名，必选
                     --password:密码，必选
                     */
                    .authorizedGrantTypes("password")
                    .scopes("test","ceshi");
        try{
            clientDetailsService = inMemoryClientDetailsServiceBuilder.build();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        System.out.println("--------clientId-------------"+clientId);
        return clientDetailsService.loadClientByClientId(clientId);
    }
}
