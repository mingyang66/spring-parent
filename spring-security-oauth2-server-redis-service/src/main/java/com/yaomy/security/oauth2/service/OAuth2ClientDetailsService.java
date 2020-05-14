package com.yaomy.security.oauth2.service;

import com.sgrain.boot.common.enums.GrantTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @Description: 自定义client详细信息类
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
@Service
public class OAuth2ClientDetailsService implements ClientDetailsService {

    private ClientDetailsService clientDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Environment propertyService;
    /**
     被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器调用一次，类似于Serclet的inti()方法。
     被@PostConstruct修饰的方法会在构造函数之后，init()方法之前运行。
     */
    @PostConstruct
    public void init(){
        InMemoryClientDetailsServiceBuilder inMemoryClientDetailsServiceBuilder = new InMemoryClientDetailsServiceBuilder();
        inMemoryClientDetailsServiceBuilder
                .withClient("auth_code")
                    // client secret
                    .secret(passwordEncoder.encode("secret"))
                    /**
                     ----支持的认证授权类型----
                     示例（授权码）：http://localhost:9003/oauth/authorize?client_id=auth_code&response_type=code&redirect_uri=http://localhost:9003/auth_user/get_auth_code
                     示例（access_token）：http://localhost:9003/oauth/token?grant_type=authorization_code&code=kb04Ur&client_id=auth_code&client_secret=secret&redirect_uri=http://localhost:9003/auth_user/get_auth_code
                     refresh_token示例：http://localhost:9003/oauth/token?client_id=auth_code&client_secret=secret&grant_type=refresh_token&refresh_token=xxxx
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
                    .redirectUris("http://localhost:9003/auth_user/get_auth_code")
                    // 用来限制客户端的访问范围，如果为空（默认）的话，那么客户端拥有全部的访问范围
                    .scopes("insert","update","del", "select", "replace")
                    .and()
                .withClient(propertyService.getProperty("spring.security.oauth.resource.client.id"))
                    //资源ID
                    .resourceIds(propertyService.getProperty("spring.security.oauth.resource.id"))
                    /**
                     ----密码模式---
                     自己有一套账号权限体系在认证服务器中对应,客户端认证的时候需要带上自己的用户名和密码
                     示例：http://localhost:9003/oauth/token?username=user&password=123&grant_type=password&client_id=client_password&client_secret=secret
                     refresh_token示例：http://localhost:9003/oauth/token?grant_type=refresh_token&refresh_token=xxx&client_id=client_password&client_secret=secret
                     --client_id：客户端ID，必选
                     --client_secret：客户端密码，必选
                     --grant_type：必须为password，必选
                     --username:用户名，必选
                     --password:密码，必选
                     */
                    .authorizedGrantTypes(GrantTypeEnum.PASSWORD.getGrant_type(), GrantTypeEnum.REFRESH_TOKEN.getGrant_type())
                    //client secret
                    .secret(passwordEncoder.encode(propertyService.getProperty("spring.security.oauth.resource.client.secret")))
                    //此客户端可以使用的权限
                    //.authorities("/a/b")
                    // 用来限制客户端的访问范围，如果为空（默认）的话，那么客户端拥有全部的访问范围
                    .scopes("all")
                    .and()
                .withClient("client")
                    //client secret
                    .secret(passwordEncoder.encode("secret"))
                    /**
                     ---clent模式---
                     没有用户的概念，直接与认证服务器交互，用配置中的用户信息去申请access_token,客户端有自己的client_id、client_secret对应于用户的username、password,
                     客户端拥有自己的authorities，采用client模式认证，客户端的权限就是客户端自己的权限
                     示例：http://localhost:9003/oauth/token?grant_type=client_credentials&scope=insert&client_id=client&client_secret=secret
                     --client_id：客户端ID，必选
                     --client_secret：客户端密码，必选
                     --grant_type：必须为password，必选
                     --scope：授权范围，必选
                     */
                    .authorizedGrantTypes("client_credentials","refresh_token")
                    //用来限制客户端的访问范围，如果为空（默认）的话，那么客户端拥有全部的访问范围
                    .scopes("insert","del", "update")
                    .and()
                .withClient("client_implicit")
                    /**
                     ---授权模式：极简模式---
                     示例:http://localhost:9003/oauth/authorize?client_id=client_implicit&response_type=token&redirect_uri=http://localhost:9003/auth_user/get_auth_code
                     */
                    .authorizedGrantTypes("implicit")
                    //回调uri，在authorization_code与implicit授权方式时，用以接收服务器的返回信息
                    .redirectUris("http://localhost:9003/auth_user/get_auth_code")
                    //用来限制客户端的访问范围，如果为空（默认）的话，那么客户端拥有全部的访问范围
                    .scopes("del","update");
        try{
            clientDetailsService = inMemoryClientDetailsServiceBuilder.build();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        if(clientId == null){
            throw new ClientRegistrationException("客户端不存在");
        }
        return clientDetailsService.loadClientByClientId(clientId);
    }
}
