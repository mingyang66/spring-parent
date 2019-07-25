# spring-parent
<h3>Spring Security OAuth2 Redis模式下认证服务器</h3> 

#### 1.四种授权码模式
- 授权码模式
- 密码模式
- 客户端模式
- 简化模式

***
#### 2.密码模式

```
http://localhost:9001/oauth/token?username=user&password=user&grant_type=password&client_id=client&client_secret=secret
```
- grant_type:授权类型，必选，此处固定值“password”<br>
- username：表示用户名，必选<br>
- password：表示用户密码，必选<br>
- scope：权限范围，可选<br>
***

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
***

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
***

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
>>替换的核心是将InMemoryTokenStore对象更换为RedisTokenStore对象，并传递一个RedisConnectionFactory接口，接口的具体实现类是JedisConnectionFactory类；<br>


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

 1. RedisStandaloneConfiguration:RedisConnectionFactory工厂类单机模式的配置类<br>
 2. RedisSentinelConfiguration：RedisConnectionFactory工厂类高可用模式的配置类<br>
 3. RedisClusterConfiguration：RedisConnectionFactory工厂类集群模式的配置类<br>
 
 ***
 
 ### Spring Security OAuth2 认证服务器自定义异常处理
 
 认证服务器默认返回的数据格式如下：
 ```
 {
     "error": "unsupported_grant_type",
     "error_description": "Unsupported grant type: password1"
 }
 ```
 上面的返回结果很不友好，而且前端代码也很难判断是什么错误，所以我们需要对返回的错误进行统一的异常处理
 
 #### 1.默认的异常处理器
 默认情况是使用WebResponseExceptionTranslator接口的实现类DefaultWebResponseExceptionTranslator对抛出的异常进行处理；所以可以通过WebResponseExceptionTranslator
 接口来入手，实现接口的方法对异常进行处理。
 
 ***
 
 #### 2.定义继承OAuth2Exception的异常类
 ```
 package com.yaomy.security.oauth2.exception;
 
 
 import com.fasterxml.jackson.databind.annotation.JsonSerialize;
 import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
 
 /**
  * @Description: 异常处理类
  * @ProjectName: spring-parent
  * @Package: com.yaomy.security.oauth2.exception.UserOAuth2Exception
  * @Date: 2019/7/17 15:29
  * @Version: 1.0
  */
 @JsonSerialize(using = UserOAuth2ExceptionSerializer.class)
 public class UserOAuth2Exception extends OAuth2Exception {
     private Integer status = 400;
 
     public UserOAuth2Exception(String message, Throwable t) {
         super(message, t);
         status = ((OAuth2Exception)t).getHttpErrorCode();
     }
 
     public UserOAuth2Exception(String message) {
         super(message);
     }
     @Override
     public int getHttpErrorCode() {
         return status;
     }
 
 }

```

***

#### 3.定义序列化实现类
```
package com.yaomy.security.oauth2.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @Description: 序列化异常类
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.exception.BootOAuthExceptionJacksonSerializer
 * @Date: 2019/7/17 15:32
 * @Version: 1.0
 */
public class UserOAuth2ExceptionSerializer extends StdSerializer<UserOAuth2Exception> {

    protected UserOAuth2ExceptionSerializer() {
        super(UserOAuth2Exception.class);
    }
    @Override
    public void serialize(UserOAuth2Exception e, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
        generator.writeStartObject();
        generator.writeObjectField("status", e.getHttpErrorCode());
        String message = e.getMessage();
        if (message != null) {
            message = HtmlUtils.htmlEscape(message);
        }
        generator.writeStringField("message", message);
        if (e.getAdditionalInformation()!=null) {
            for (Map.Entry<String, String> entry : e.getAdditionalInformation().entrySet()) {
                String key = entry.getKey();
                String add = entry.getValue();
                generator.writeStringField(key, add);
            }
        }
        generator.writeEndObject();
    }
}
```
***

#### 4.自定义实现异常转换类
```
package com.yaomy.security.oauth2.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultThrowableAnalyzer;
import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.io.IOException;

/**
 * @Description: 资源服务器异常自定义捕获
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.exception.OAuth2ServerWebResponseExceptionTranslator
 * @Date: 2019/7/17 14:49
 * @Version: 1.0
 */
@Component
public class UserOAuth2WebResponseExceptionTranslator implements WebResponseExceptionTranslator {
    private ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();

    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
        Throwable[] causeChain = this.throwableAnalyzer.determineCauseChain(e);
        Exception ase = (OAuth2Exception)this.throwableAnalyzer.getFirstThrowableOfType(OAuth2Exception.class, causeChain);
        //异常链中有OAuth2Exception异常
        if (ase != null) {
            return this.handleOAuth2Exception((OAuth2Exception)ase);
        }
        //身份验证相关异常
        ase = (AuthenticationException)this.throwableAnalyzer.getFirstThrowableOfType(AuthenticationException.class, causeChain);
        if (ase != null) {
            return this.handleOAuth2Exception(new UserOAuth2WebResponseExceptionTranslator.UnauthorizedException(e.getMessage(), e));
        }
        //异常链中包含拒绝访问异常
        ase = (AccessDeniedException)this.throwableAnalyzer.getFirstThrowableOfType(AccessDeniedException.class, causeChain);
        if (ase instanceof AccessDeniedException) {
            return this.handleOAuth2Exception(new UserOAuth2WebResponseExceptionTranslator.ForbiddenException(ase.getMessage(), ase));
        }
        //异常链中包含Http方法请求异常
        ase = (HttpRequestMethodNotSupportedException)this.throwableAnalyzer.getFirstThrowableOfType(HttpRequestMethodNotSupportedException.class, causeChain);
        if(ase instanceof HttpRequestMethodNotSupportedException){
            return this.handleOAuth2Exception(new UserOAuth2WebResponseExceptionTranslator.MethodNotAllowed(ase.getMessage(), ase));
        }
        return this.handleOAuth2Exception(new UserOAuth2WebResponseExceptionTranslator.ServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e));
    }

    private ResponseEntity<OAuth2Exception> handleOAuth2Exception(OAuth2Exception e) throws IOException {
        int status = e.getHttpErrorCode();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        if (status == HttpStatus.UNAUTHORIZED.value() || e instanceof InsufficientScopeException) {
            headers.set("WWW-Authenticate", String.format("%s %s", "Bearer", e.getSummary()));
        }
        UserOAuth2Exception exception = new UserOAuth2Exception(e.getMessage(),e);
        ResponseEntity<OAuth2Exception> response = new ResponseEntity(exception, headers, HttpStatus.valueOf(status));
        return response;
    }


    private static class MethodNotAllowed extends OAuth2Exception {
        public MethodNotAllowed(String msg, Throwable t) {
            super(msg, t);
        }
        @Override
        public String getOAuth2ErrorCode() {
            return "method_not_allowed";
        }
        @Override
        public int getHttpErrorCode() {
            return 405;
        }
    }

    private static class UnauthorizedException extends OAuth2Exception {
        public UnauthorizedException(String msg, Throwable t) {
            super(msg, t);
        }
        @Override
        public String getOAuth2ErrorCode() {
            return "unauthorized";
        }
        @Override
        public int getHttpErrorCode() {
            return 401;
        }
    }

    private static class ServerErrorException extends OAuth2Exception {
        public ServerErrorException(String msg, Throwable t) {
            super(msg, t);
        }
        @Override
        public String getOAuth2ErrorCode() {
            return "server_error";
        }
        @Override
        public int getHttpErrorCode() {
            return 500;
        }
    }

    private static class ForbiddenException extends OAuth2Exception {
        public ForbiddenException(String msg, Throwable t) {
            super(msg, t);
        }
        @Override
        public String getOAuth2ErrorCode() {
            return "access_denied";
        }
        @Override
        public int getHttpErrorCode() {
            return 403;
        }
    }
}

```

***

#### 5.将自定义异常处理类添加到认证服务器配置
```
package com.yaomy.security.oauth2.config;

import com.yaomy.security.oauth2.enhancer.UserTokenEnhancer;
import com.yaomy.security.oauth2.handler.UserAccessDeniedHandler;
import com.yaomy.security.oauth2.handler.UserAuthenticationEntryPoint;
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
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
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
    private WebResponseExceptionTranslator webResponseExceptionTranslator;
   
    /**
     用来配置授权（authorization）以及令牌（token)的访问端点和令牌服务（token services）
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
       ...

        endpoints.exceptionTranslator(webResponseExceptionTranslator);
        ...
    }
    ...

}
```
 
 
 ***
 
 ### Spring boot + Security + OAuth2 password模式、refresh_token模式访问/oauth/token端点
 
 #### 1./oauth/token端点
 * 端点过滤器TokenEndpointAuthenticationFilter
 * 端点对应的action类TokenEndpoint
 * 受保护的资源信息类ResourceOwnerPasswordResourceDetails
 * 和认证服务器交互资源信息类ResourceOwnerPasswordAccessTokenProvider
 
 ***
 #### 2./oauth/token(令牌端点) 获取用户token信息
 ```
    @RequestMapping(value = "token", method = RequestMethod.POST)
     public ResponseEntity<BaseResponse> getToken(@RequestParam String username, @RequestParam String password){
         ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
         resource.setId(resourceId);
         resource.setClientId(resourceClientId);
         resource.setClientSecret(resourceClientSecret);
         resource.setGrantType("password");
         resource.setAccessTokenUri(tokenUri);
         resource.setUsername(username);
         resource.setPassword(password);
         resource.setScope(Arrays.asList("test"));
 
         OAuth2RestTemplate template = new OAuth2RestTemplate(resource);
         ResourceOwnerPasswordAccessTokenProvider provider = new ResourceOwnerPasswordAccessTokenProvider();
         template.setAccessTokenProvider(provider);
         System.out.println("过期时间是："+template.getAccessToken().getExpiration());
         BaseResponse response = null;
         try {
             response = BaseResponse.createResponse(HttpStatusMsg.OK, template.getAccessToken());
         } catch (Exception e){
             response = BaseResponse.createResponse(HttpStatusMsg.AUTHENTICATION_EXCEPTION, e.toString());
         }
         return ResponseEntity.ok(response);
     }
 ```
 返回结果如下：
 ```
 {
     "status": 200,
     "message": "SUCCESS",
     "data": {
         "access_token": "9de1856b9e0b4400a8b162cd3b3cfbea",
         "token_type": "bearer",
         "refresh_token": "71e5515f99424278bd53d93e322e60d5",
         "expires_in": 898,
         "scope": "test"
     }
 }
 ```
 
 #### 3./oauth/token（令牌端点）刷新token信息
 
 ```
    @RequestMapping(value = "refresh_token", method = RequestMethod.POST)
     public ResponseEntity<BaseResponse> refreshToken(String refresh_token){
 
         ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
         resource.setClientId(resourceClientId);
         resource.setClientSecret(resourceClientSecret);
         resource.setGrantType("refresh_token");
         resource.setAccessTokenUri(tokenUri);
 
         ResourceOwnerPasswordAccessTokenProvider provider = new ResourceOwnerPasswordAccessTokenProvider();
         OAuth2RefreshToken refreshToken = tokenStore.readRefreshToken(refresh_token);
         OAuth2AccessToken accessToken = provider.refreshAccessToken(resource,refreshToken, new DefaultAccessTokenRequest());
         BaseResponse response = null;
         try {
             response = BaseResponse.createResponse(HttpStatusMsg.OK, accessToken);
         } catch (Exception e){
             response = BaseResponse.createResponse(HttpStatusMsg.AUTHENTICATION_EXCEPTION, e.toString());
         }
         return ResponseEntity.ok(response);
     }
 ```
 返回结果如下：
 ```
 {
     "status": 200,
     "message": "SUCCESS",
     "data": {
         "access_token": "2029dad0b3a0453c987d52815095b9dd",
         "token_type": "bearer",
         "refresh_token": "f7cffb9b19634f72943b5ab39c63d652",
         "expires_in": 899,
         "scope": "test"
     }
 }
 ```
 #### 4.oauth/check_token（端点校验）token有效性
 ```
     @RequestMapping(value = "check_token", method = RequestMethod.POST)
     public ResponseEntity<BaseResponse> checkToken(String token){
         OAuth2AccessToken accessToken = tokenStore.readAccessToken(token);
         OAuth2Authentication auth2Authentication = tokenStore.readAuthentication(token);
         Map<String, Object> map = Maps.newHashMap();
         //用户名
         map.put("username", auth2Authentication.getUserAuthentication().getName());
         //是否过期
         map.put("isExpired", accessToken.isExpired());
         //过期时间
         map.put("expiration", DateFormatUtils.format(accessToken.getExpiration(), "yyyy-MM-dd HH:mm:ss"));
         BaseResponse response = null;
         try {
             response = BaseResponse.createResponse(HttpStatusMsg.OK, map);
         } catch (Exception e){
             response = BaseResponse.createResponse(HttpStatusMsg.AUTHENTICATION_EXCEPTION, e.toString());
         }
         return ResponseEntity.ok(response);
     }
 ```
 返回结果如下：
 ```
 {
     "status": 200,
     "message": "SUCCESS",
     "data": {
         "expiration": "2019-07-25 17:42:14",
         "isExpired": false,
         "username": "user"
     }
 }
 ```

***

### Spring Security OAuth2 token存储Redis用户登出logOut

Redis用户登出有两种方案,一种是通过资源服务器配置logoutSuccessHandler处理函数，并实现LogoutSuccessHandler接口来处理退出用户；
另外一种是自定义封装接口，通过RedisTokenStore来删除用户信息的形式；

#### 1.通过资源服务器配置的方式
* ResourceServerConfigurerAdapter配置
```
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/auth_user/*").denyAll()
            .antMatchers("/oauth2/**","/oauth/**").permitAll()
            .anyRequest().authenticated()
        .and()
            .logout()
            //
            .logoutSuccessHandler(logoutSuccessHandler)
        .and()
            .csrf().disable();

    }
```
* 退出成功处理LogoutSuccessHandler类
```

package com.yaomy.security.oauth2.handler;

import com.yaomy.common.enums.HttpStatusMsg;
import com.yaomy.common.po.BaseResponse;
import com.yaomy.common.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @Description: 用户成功退出
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.handler.AjaxLogoutSuccessHandler
 * @Date: 2019/7/1 15:39
 * @Version: 1.0
 *//*

@Component
public class UserLogoutSuccessHandler implements LogoutSuccessHandler {
    @Autowired
    private TokenStore tokenStore;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String accessToken = request.getParameter("access_token");
        if(StringUtils.isNotBlank(accessToken)){
            OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(accessToken);
            if(oAuth2AccessToken != null){
                System.out.println("----access_token是："+oAuth2AccessToken.getValue());
                tokenStore.removeAccessToken(oAuth2AccessToken);
                OAuth2RefreshToken oAuth2RefreshToken = oAuth2AccessToken.getRefreshToken();
                tokenStore.removeRefreshToken(oAuth2RefreshToken);
                tokenStore.removeAccessTokenUsingRefreshToken(oAuth2RefreshToken);
            }
        }
        HttpUtils.writeSuccess(BaseResponse.createResponse(HttpStatusMsg.OK.getStatus(), "退出成功"), response);

    }
}
```
 
 #### 2.自定义退出接口方案
 ```
    @RequestMapping(value = "refresh_token", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse> refreshToken(String refresh_token){

        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
        resource.setClientId(resourceClientId);
        resource.setClientSecret(resourceClientSecret);
        resource.setGrantType("refresh_token");
        resource.setAccessTokenUri(tokenUri);

        ResourceOwnerPasswordAccessTokenProvider provider = new ResourceOwnerPasswordAccessTokenProvider();
        OAuth2RefreshToken refreshToken = tokenStore.readRefreshToken(refresh_token);
        OAuth2AccessToken accessToken = provider.refreshAccessToken(resource, refreshToken, new DefaultAccessTokenRequest());
        BaseResponse response = BaseResponse.createResponse(HttpStatusMsg.OK, accessToken);
        return ResponseEntity.ok(response);
    }
```

 ***
 GitHub源码：[https://github.com/mingyang66/spring-parent/tree/master/spring-security-oauth2-server-redis-service](https://github.com/mingyang66/spring-parent/tree/master/spring-security-oauth2-server-redis-service)

