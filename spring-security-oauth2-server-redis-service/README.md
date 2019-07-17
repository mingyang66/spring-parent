# spring-parent
<h3>Spring Security OAuth2 Redis模式下认证服务器</h3> 

#### 1.四种授权码模式
- 授权码模式
- 密码模式
- 客户端模式
- 简化模式
#### 2.密码模式

```
http://localhost:9001/oauth/token?username=user&password=user&grant_type=password&client_id=client&client_secret=secret
```
- grant_type:授权类型，必选，此处固定值“password”<br>
- username：表示用户名，必选<br>
- password：表示用户密码，必选<br>
- scope：权限范围，可选<br>
#### 3.授权码模式
- client_id：客户端ID，必选
- response_type：必须为code，必选
- redirect_uri：回掉url,必选
* 获取授权码：

```
http://localhost:9001/oauth/authorize?client_id=client&response_type=code&redirect_uri=http://localhost:9001/auth_user/get_auth_code
```
* 获取access_token

```
http://localhost:9001/oauth/token?grant_type=authorization_code&code=XQfMUi&client_id=client&client_secret=secret&redirect_uri=http://localhost:9001/auth_user/get_token_info
```
* 通过refresh_token获取access_token
```
http://localhost:9001/oauth/token?grant_type=refresh_token&refresh_token=Beared5d74d532ba446b58f78186013f5e170&client_id=client&client_secret=secret
```
* 通过refresh_token获取新的access_token时可以自定义用户信息验证service
```
package com.yaomy.security.oauth2.po;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @Description: 用户认证
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.po.User
 * @Date: 2019/6/28 17:37
 * @Version: 1.0
 */
@Component
public class AuthUserDetailsService implements UserDetailsService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    /**
     * @Description 根据用户名查询用户角色、权限等信息
     * @Date 2019/7/1 14:50
     * @Version  1.0
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("---用户信息验证----"+username);
        /**
         isEnabled 账户是否启用
         isAccountNonExpired 账户没有过期
         isCredentialsNonExpired 身份认证是否是有效的
         isAccountNonLocked 账户没有被锁定
         */
         return new User(username, passwordEncoder.encode("123"),
                true,
                true,
                true,
                true,
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE"));
    }

}
```
>在Security OAuth2授权服务配置类中添加上自定义的用户信息校验类
```
  /**
     用来配置授权（authorization）以及令牌（token)的访问端点和令牌服务（token services）
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore)
                .approvalStore(approvalStore)
                //通过authenticationManager开启密码授权
                .authenticationManager(authenticationManager)
                //自定义token生成
                .tokenEnhancer(tokenEnhancer())
                //自定义refresh_token刷新令牌对用户信息的检查，以确保用户信息仍然有效
                .userDetailsService(authUserDetailsService);
    }
```

#### 4.自定义token生成

* 自定义一个实现TokenEnhancer接口的token增强器
```
package com.yaomy.security.oauth2.enhancer;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.Map;
import java.util.UUID;

/**
 * @Description: 用户自定义token令牌，包括access_token和refresh_token
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.enhancer.UserTokenEnhancer
 * @Date: 2019/7/9 19:43
 * @Version: 1.0
 */
public class UserTokenEnhancer implements TokenEnhancer {
    /**
     * @Description 重新定义令牌token
     * @Date 2019/7/9 19:56
     * @Version  1.0
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
       if(accessToken instanceof DefaultOAuth2AccessToken){
           DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
           token.setValue(getToken());
           OAuth2RefreshToken refreshToken = token.getRefreshToken();
           if(refreshToken instanceof DefaultOAuth2RefreshToken){
               token.setRefreshToken(new DefaultOAuth2RefreshToken(getToken()));
           }
           Map<String, Object> additionalInformation = Maps.newHashMap();
           additionalInformation.put("client_id", authentication.getOAuth2Request().getClientId());
           token.setAdditionalInformation(additionalInformation);
           return token;
       }
        return accessToken;
    }
    /**
     * @Description 生成自定义token
     * @Date 2019/7/9 19:50
     * @Version  1.0
     */
    private String getToken(){
        return StringUtils.join("Beare", UUID.randomUUID().toString().replace("-", ""));
    }
}
```

* 将自定义的token增强器加入IOC容器中
```
    /**
     * @Description 自定义生成令牌token
     * @Date 2019/7/9 19:58
     * @Version  1.0
     */
    @Bean
    public TokenEnhancer tokenEnhancer(){
        return new UserTokenEnhancer();
    }
```
* 将token增强器加入授权配置端点
```
   /**
     用来配置授权（authorization）以及令牌（token)的访问端点和令牌服务（token services）
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore)
                .approvalStore(approvalStore)
                //通过authenticationManager开启密码授权
                .authenticationManager(authenticationManager)
                //自定义token生成
                .tokenEnhancer(tokenEnhancer())
                //自定义refresh_token刷新令牌对用户信息的检查，以确保用户信息仍然有效
                .userDetailsService(authUserDetailsService);
    }
    
```

#### 5.自定义token过期时长

```
    /**
     用来配置授权（authorization）以及令牌（token)的访问端点和令牌服务（token services）
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        //token持久化容器
        tokenServices.setTokenStore(tokenStore());
        //是否支持refresh_token，默认false
        tokenServices.setSupportRefreshToken(true);
        //客户端信息
        tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
        //自定义token生成
        tokenServices.setTokenEnhancer(tokenEnhancer());
        //access_token 的有效时长 (秒), 默认 12 小时
        tokenServices.setAccessTokenValiditySeconds(60*15);
        //refresh_token 的有效时长 (秒), 默认 30 天
        tokenServices.setRefreshTokenValiditySeconds(60*20);
        //是否复用refresh_token,默认为true(如果为false,则每次请求刷新都会删除旧的refresh_token,创建新的refresh_token)
        tokenServices.setReuseRefreshToken(true);

        endpoints
                //通过authenticationManager开启密码授权
                .authenticationManager(authenticationManager)
                //自定义refresh_token刷新令牌对用户信息的检查，以确保用户信息仍然有效
                .userDetailsService(authUserDetailsService)
                //token相关服务
                .tokenServices(tokenServices);
    }
```

***

#### 6.认证服务器配置--token存入redis缓存
```
- 使用Redis缓存需要引入的依赖

```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
```

- 认证服务器配置代码

```
package com.yaomy.security.oauth2.config;

import com.yaomy.security.oauth2.enhancer.UserTokenEnhancer;
import com.yaomy.security.oauth2.po.AuthUserDetailsService;
import com.yaomy.security.oauth2.service.OAuth2ClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * @Description: @EnableAuthorizationServer注解开启OAuth2授权服务机制
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.config.OAuth2ServerConfig
 * @Date: 2019/7/9 11:26
 * @Version: 1.0
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private OAuth2ClientDetailsService oAuth2ClientDetailsService;
    @Autowired
    private AuthUserDetailsService authUserDetailsService;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
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
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        //token持久化容器
        tokenServices.setTokenStore(tokenStore());
        //客户端信息
        tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
        //自定义token生成
        tokenServices.setTokenEnhancer(tokenEnhancer());
        //access_token 的有效时长 (秒), 默认 12 小时
        tokenServices.setAccessTokenValiditySeconds(60*15);
        //refresh_token 的有效时长 (秒), 默认 30 天
        tokenServices.setRefreshTokenValiditySeconds(60*20);
        //是否支持refresh_token，默认false
        tokenServices.setSupportRefreshToken(true);
        //是否复用refresh_token,默认为true(如果为false,则每次请求刷新都会删除旧的refresh_token,创建新的refresh_token)
        tokenServices.setReuseRefreshToken(true);

        endpoints
                //通过authenticationManager开启密码授权
                .authenticationManager(authenticationManager)
                //自定义refresh_token刷新令牌对用户信息的检查，以确保用户信息仍然有效
                .userDetailsService(authUserDetailsService)
                //token相关服务
                .tokenServices(tokenServices)
                /**
                 pathMapping用来配置端点URL链接，第一个参数是端点URL默认地址，第二个参数是你要替换的URL地址
                 上面的参数都是以“/”开头，框架的URL链接如下：
                 /oauth/authorize：授权端点。----对应的类：AuthorizationEndpoint.java
                 /oauth/token：令牌端点。----对应的类：TokenEndpoint.java
                 /oauth/confirm_access：用户确认授权提交端点。----对应的类：WhitelabelApprovalEndpoint.java
                 /oauth/error：授权服务错误信息端点。
                 /oauth/check_token：用于资源服务访问的令牌解析端点。
                 /oauth/token_key：提供公有密匙的端点，如果你使用JWT令牌的话。
                 */
                .pathMapping("/oauth/confirm_access", "/custom/confirm_access");
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
        //return new InMemoryTokenStore();
        return new RedisTokenStore(redisConnectionFactory);
    }
    /**
     * @Description ApprovalStore用户保存、检索和撤销用户审批的界面
     * @Author 姚明洋
     * @Date 2019/7/11 14:11
     * @Version  1.0
     */
/*    @Bean
    public ApprovalStore approvalStore() throws Exception {
        TokenApprovalStore store = new TokenApprovalStore();
        store.setTokenStore(tokenStore());
        return store;
    }*/
/*    @Bean
    public UserApprovalHandler userApprovalHandler1(){
        TokenStoreUserApprovalHandler userApprovalHandler = new TokenStoreUserApprovalHandler();
        userApprovalHandler.setTokenStore(tokenStore());
        return userApprovalHandler;
    }*/
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
```
替换的核心是将InMemoryTokenStore对象更换为RedisTokenStore对象，并传递一个RedisConnectionFactory接口，接口的具体实现类是JedisConnectionFactory类；<br>


- Redis缓存配置
```
##单机应用环境配置
spring.redis.host=127.0.0.1
spring.redis.port=6379
#spring.redis.password=
##Redis数据库索引，默认0
spring.redis.database=0
#spring.redis.timeout=

##redis连接池配置
## 连接池中的最小空闲连接，默认0
spring.redis.jedis.pool.min-idle=0
## 连接池中的最大空闲连接，默认8
spring.redis.jedis.pool.max-idle=8
## 连接池最大阻塞等待时间（使用负值表示没有限制），默认-1ms
spring.redis.jedis.pool.max-wait=-1ms
##连接池最大连接数（使用负值表示没有限制），默认8
spring.redis.jedis.pool.max-active=8
```

* RedisConnectionFactory可以通过如下三个配置类应用在不同的应用场景

* RedisStandaloneConfiguration

