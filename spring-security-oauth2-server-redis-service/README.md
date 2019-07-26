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

### Spring Security OAuth2 使用Redis存储token键值详解

#### 1.Spring Security OAuth2存储token值的方式由多种，所有的实现方式都是实现了TokenStore接口
* InMemoryTokenStore:token存储在本机的内存之中
* JdbcTokenStore:token存储在数据库之中
* JwtTokenStore:token不会存储到任何介质中
* RedisTokenStore:token存储在Redis数据库之中

#### 2.看下RedisTokenStore实现类在redis中存储了那些key,贴上源码如下：
```
    private static final String ACCESS = "access:";
    private static final String AUTH_TO_ACCESS = "auth_to_access:";
    private static final String AUTH = "auth:";
    private static final String REFRESH_AUTH = "refresh_auth:";
    private static final String ACCESS_TO_REFRESH = "access_to_refresh:";
    private static final String REFRESH = "refresh:";
    private static final String REFRESH_TO_ACCESS = "refresh_to_access:";
    private static final String CLIENT_ID_TO_ACCESS = "client_id_to_access:";
    private static final String UNAME_TO_ACCESS = "uname_to_access:";
```
本案例是使用password、refresh_token模式，在Redis缓存中共存储了9个键值对，其中有5个跟access_token相关，4个和refresh_token相关；
* access_token相关access:(OAuth2AccessToken)、auth:(OAuth2Authentication)、auth_to_access:(OAuth2AccessToken)、client_id_to_access:(OAuth2AccessToken)、uname_to_access:(OAuth2AccessToken)
* refresh_token相关refresh:(OAuth2RefreshToken)、refresh_auth:(OAuth2Authentication)、access_to_refresh(refresh_token):、refresh_to_access:(refresh_token)

****

#### 3.通过查看RedisTokenStore源码（源码我就不贴出来了）的方式理解每个key所存储的数据

1. access:中存储的键是access:be171b573f5a496ca601b32b1360fe84，值是OAuth2AccessToken对象序列化后的值
* 键是access:+access_token
* 值示例如下：
  ```
  {
          "access_token": "12833d6c89fb4ea58cbe7b6ada5de7b5",
          "token_type": "bearer",
          "refresh_token": "357304ee0a404700b3e65d547713011b",
          "expires_in": 898,
          "scope": "test"
      }
  ```   
2. auth_to_access:中存储的键是auth_to_access:a994f2a9a61186f32870e32d72a38d21，值是OAuth2AccessToken序列化后的值
* 键是auth_to_access:+  username、client_id、scope三个MD5加密后的值 
* 值示例如下：
 
  ```
  {
          "access_token": "12833d6c89fb4ea58cbe7b6ada5de7b5",
          "token_type": "bearer",
          "refresh_token": "357304ee0a404700b3e65d547713011b",
          "expires_in": 898,
          "scope": "test"
      }
  ``` 
   
3. auth:中存储的键是auth:be171b573f5a496ca601b32b1360fe84，值是OAuth2Authentication对象序列化后的值
* 键是auth:+access_token值
* 值示例如下：
```
{
    "authorities": [
        {
            "authority": "ROLE"
        }
    ],
    "details": {
        "remoteAddress": "0:0:0:0:0:0:0:1",
        "sessionId": null,
        "tokenValue": "dfec9f18e161408dbf66b85b94401d7f",
        "tokenType": "Bearer",
        "decodedDetails": null
    },
    "authenticated": true,
    "userAuthentication": {
        "authorities": [
            {
                "authority": "ROLE"
            }
        ],
        "details": {
            "grant_type": "password",
            "username": "user",
            "scope": "test"
        },
        "authenticated": true,
        "principal": {
            "password": null,
            "username": "user",
            "authorities": [
                {
                    "authority": "ROLE"
                }
            ],
            "accountNonExpired": true,
            "accountNonLocked": true,
            "credentialsNonExpired": true,
            "enabled": true
        },
        "credentials": null,
        "name": "user"
    },
    "credentials": "",
    "principal": {
        "password": null,
        "username": "user",
        "authorities": [
            {
                "authority": "ROLE"
            }
        ],
        "accountNonExpired": true,
        "accountNonLocked": true,
        "credentialsNonExpired": true,
        "enabled": true
    },
    "oauth2Request": {
        "clientId": "client_password",
        "scope": [
            "test"
        ],
        "requestParameters": {
            "grant_type": "password",
            "scope": "test",
            "username": "user"
        },
        "resourceIds": [
            "resource_password_id"
        ],
        "authorities": [],
        "approved": true,
        "refresh": false,
        "redirectUri": null,
        "responseTypes": [],
        "extensions": {},
        "grantType": "password",
        "refreshTokenRequest": null
    },
    "clientOnly": false,
    "name": "user"
}
```

4. refresh_auth:中存储的是refresh_auth:d0017ce6db6441d1b87a0a2804d1434b,值是OAuth2Authentication序列化后的值
* 键是：refresh_auth:+refresh_token值
* 值示例如下：
```
{
    "authorities": [
        {
            "authority": "ROLE"
        }
    ],
    "details": {
        "remoteAddress": "0:0:0:0:0:0:0:1",
        "sessionId": null,
        "tokenValue": "dfec9f18e161408dbf66b85b94401d7f",
        "tokenType": "Bearer",
        "decodedDetails": null
    },
    "authenticated": true,
    "userAuthentication": {
        "authorities": [
            {
                "authority": "ROLE"
            }
        ],
        "details": {
            "grant_type": "password",
            "username": "user",
            "scope": "test"
        },
        "authenticated": true,
        "principal": {
            "password": null,
            "username": "user",
            "authorities": [
                {
                    "authority": "ROLE"
                }
            ],
            "accountNonExpired": true,
            "accountNonLocked": true,
            "credentialsNonExpired": true,
            "enabled": true
        },
        "credentials": null,
        "name": "user"
    },
    "credentials": "",
    "principal": {
        "password": null,
        "username": "user",
        "authorities": [
            {
                "authority": "ROLE"
            }
        ],
        "accountNonExpired": true,
        "accountNonLocked": true,
        "credentialsNonExpired": true,
        "enabled": true
    },
    "oauth2Request": {
        "clientId": "client_password",
        "scope": [
            "test"
        ],
        "requestParameters": {
            "grant_type": "password",
            "scope": "test",
            "username": "user"
        },
        "resourceIds": [
            "resource_password_id"
        ],
        "authorities": [],
        "approved": true,
        "refresh": false,
        "redirectUri": null,
        "responseTypes": [],
        "extensions": {},
        "grantType": "password",
        "refreshTokenRequest": null
    },
    "clientOnly": false,
    "name": "user"
}
```
5. access_to_refresh:中存储的是access_to_refresh:c90cab28971948d2a85ca2ae814641ed，值是refresh_token值
* 键是access_to_refresh:+refresh_token值
* 值是refresh_token值
6. refresh:中存储的是refresh:d0017ce6db6441d1b87a0a2804d1434b，值是OAuth2RefreshToken对象序列化后的值
* 键是refresh:+refresh_token值
* 值示例如下：
```
 {
        "access_token": "dfec9f18e161408dbf66b85b94401d7f",
        "token_type": "bearer",
        "refresh_token": "8bcd9cfb04a3445e8933c788b2673a89",
        "expires_in": 898,
        "scope": "test"
    }
```
7. refresh_to_access:中存储的值是refresh_to_access:d0017ce6db6441d1b87a0a2804d1434b，值是refresh_token值
* 键是refresh_to_access:+refresh_token值
* 值示例如下：
```
be171b573f5a496ca601b32b1360fe84
```

8. client_id_to_access:中存储的值是client_id_to_access:client_password，值是OAuth2AccessToken序列化后的值
* 键是client_id_to_access:+clientId
* 值示例如下：
```
{
        "access_token": "dfec9f18e161408dbf66b85b94401d7f",
        "token_type": "bearer",
        "refresh_token": "8bcd9cfb04a3445e8933c788b2673a89",
        "expires_in": 898,
        "scope": "test"
    }
```

9. uname_to_access:中存储的键是uname_to_access:client_password:user，值是OAuth2AccessToken对象序列化后的值
* 键是：uname_to_access:+clientid+用户名
* 值示例如下：
```
{
        "access_token": "dfec9f18e161408dbf66b85b94401d7f",
        "token_type": "bearer",
        "refresh_token": "8bcd9cfb04a3445e8933c788b2673a89",
        "expires_in": 898,
        "scope": "test"
    }
```
 ***
 
 ### Spring Security OAuth2 Redis存储token refresh_token永不过期问题详解
 ***
 #### 1.先看几个实现类，然后再看源码分析这样会更清晰
 * OAuth2AccessToken接口的默认实现是DefaultOAuth2AccessToken类（自带过期时间属性）
 * OAuth2RefreshToken接口的默认实现是DefaultOAuth2RefreshToken类（不带过期时间属性）
 * ExpiringOAuth2RefreshToken接口父接口是OAuth2RefreshToken，ExpiringOAuth2RefreshToken的默认实现是DefaultExpiringOAuth2RefreshToken（自带过期时间属性）
 
 #### 2.当前demo是使用自定义方式来实现access_token和refresh_token的生成，看如下代码：
 ```
 package com.yaomy.security.oauth2.enhancer;
 
 import com.google.common.collect.Maps;
 import org.apache.commons.lang3.StringUtils;
 import org.springframework.security.oauth2.common.*;
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
            if(refreshToken instanceof OAuth2RefreshToken){
                token.setRefreshToken(new OAuth2RefreshToken(getToken()));
            }
            Map<String, Object> additionalInformation = Maps.newHashMap();
            additionalInformation.put("client_id", authentication.getOAuth2Request().getClientId());
            //添加额外配置信息
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
         return StringUtils.join(UUID.randomUUID().toString().replace("-", ""));
     }
 }
```
 在实际的测试环境之中我可以拿到access_token的过期时间，并且在redis的客户端查看access_token相关键值对都是跟我设置的过期时间是一直的，但是refresh_token设置的过期
 时间一直不起作用，TTL显示-1，也就是一直有效，感觉就很奇怪，所以就翻看了TokenStore的实现类RedisTokenStore，源码如下
 * 生成access_token键值对的代码如下：
 ```
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        //序列化相关key
         byte[] serializedAccessToken = this.serialize((Object)token);
         byte[] serializedAuth = this.serialize((Object)authentication);
         byte[] accessKey = this.serializeKey("access:" + token.getValue());
         byte[] authKey = this.serializeKey("auth:" + token.getValue());
         byte[] authToAccessKey = this.serializeKey("auth_to_access:" + this.authenticationKeyGenerator.extractKey(authentication));
         byte[] approvalKey = this.serializeKey("uname_to_access:" + getApprovalKey(authentication));
         byte[] clientId = this.serializeKey("client_id_to_access:" + authentication.getOAuth2Request().getClientId());
         RedisConnection conn = this.getConnection();
 
         try {
             conn.openPipeline();
             if (springDataRedis_2_0) {
                 try {
                     //存储键值对
                     this.redisConnectionSet_2_0.invoke(conn, accessKey, serializedAccessToken);
                     this.redisConnectionSet_2_0.invoke(conn, authKey, serializedAuth);
                     this.redisConnectionSet_2_0.invoke(conn, authToAccessKey, serializedAccessToken);
                 } catch (Exception var24) {
                     throw new RuntimeException(var24);
                 }
             } else {
                 conn.set(accessKey, serializedAccessToken);
                 conn.set(authKey, serializedAuth);
                 conn.set(authToAccessKey, serializedAccessToken);
             }
 
             if (!authentication.isClientOnly()) {
                 conn.sAdd(approvalKey, new byte[][]{serializedAccessToken});
             }
 
             conn.sAdd(clientId, new byte[][]{serializedAccessToken});
             //设置access_token过期时间
             if (token.getExpiration() != null) {
                 int seconds = token.getExpiresIn();
                 conn.expire(accessKey, (long)seconds);
                 conn.expire(authKey, (long)seconds);
                 conn.expire(authToAccessKey, (long)seconds);
                 conn.expire(clientId, (long)seconds);
                 conn.expire(approvalKey, (long)seconds);
             }
 
             OAuth2RefreshToken refreshToken = token.getRefreshToken();
             if (refreshToken != null && refreshToken.getValue() != null) {
                 byte[] refresh = this.serialize(token.getRefreshToken().getValue());
                 byte[] auth = this.serialize(token.getValue());
                 byte[] refreshToAccessKey = this.serializeKey("refresh_to_access:" + token.getRefreshToken().getValue());
                 byte[] accessToRefreshKey = this.serializeKey("access_to_refresh:" + token.getValue());
                 if (springDataRedis_2_0) {
                     try {
                         this.redisConnectionSet_2_0.invoke(conn, refreshToAccessKey, auth);
                         this.redisConnectionSet_2_0.invoke(conn, accessToRefreshKey, refresh);
                     } catch (Exception var23) {
                         throw new RuntimeException(var23);
                     }
                 } else {
                     conn.set(refreshToAccessKey, auth);
                     conn.set(accessToRefreshKey, refresh);
                 }
                 //判断refresh_token对象是否是ExpiringOAuth2RefreshToken的实例对象，这一段是设置refresh_token的关键，如果是就会进行过期时间
                 //设置，否则生成的refresh_token相关的键值对永远有效          
                 if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
                     ExpiringOAuth2RefreshToken expiringRefreshToken = (ExpiringOAuth2RefreshToken)refreshToken;
                     Date expiration = expiringRefreshToken.getExpiration();
                     if (expiration != null) {
                         int seconds = Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L).intValue();
                         conn.expire(refreshToAccessKey, (long)seconds);
                         conn.expire(accessToRefreshKey, (long)seconds);
                     }
                 }
             }
 
             conn.closePipeline();
         } finally {
             conn.close();
         }
 
     }
 ```
 上面的refreshToken instanceof ExpiringOAuth2RefreshToken这一段代码是来判断刷新token是否是带有有效期时间的ExpiringOAuth2RefreshToken实例对象，我们可以
 看到上面我自定义的生成refresh_token的实例对象是OAuth2RefreshToken类型，只带有一个refresh_token值，而没有有效时间的字段值，我们看下ExpiringOAuth2RefreshToken
 类的源码：
 ```
 package org.springframework.security.oauth2.common;
 
 import java.util.Date;
 
 public interface ExpiringOAuth2RefreshToken extends OAuth2RefreshToken {
     Date getExpiration();
 }
 ```
 我们可以看到ExpiringOAuth2RefreshToken是OAuth2RefreshToken的子类，所以我们可以将生成的refresh_token实例对象更改为ExpiringOAuth2RefreshToken对象，代码如下：
 ```
     @Override
     public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        if(accessToken instanceof DefaultOAuth2AccessToken){
            DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
            token.setValue(getToken());
            //使用DefaultExpiringOAuth2RefreshToken类生成refresh_token，自带过期时间，否则不生效，refresh_token一直有效
            DefaultExpiringOAuth2RefreshToken refreshToken = (DefaultExpiringOAuth2RefreshToken)token.getRefreshToken();
            //OAuth2RefreshToken refreshToken = token.getRefreshToken();
            if(refreshToken instanceof DefaultExpiringOAuth2RefreshToken){
                token.setRefreshToken(new DefaultExpiringOAuth2RefreshToken(getToken(), refreshToken.getExpiration()));
            }
            Map<String, Object> additionalInformation = Maps.newHashMap();
            additionalInformation.put("client_id", authentication.getOAuth2Request().getClientId());
            //添加额外配置信息
            token.setAdditionalInformation(additionalInformation);
            return token;
        }
         return accessToken;
     }
 ```
 经测试上面的方案完美解决自定义token生成refresh_token永不过期问题。。。
 
 
 ### 
 ***
 
 
 ### Spring Security OAuth2之认证服务、资源服务、web安全配置服务加载优先级详解
 
 最近一直在搭建Spring Security OAuth2认证服务，经常会遇到在资源服务器配置中配置生效，但是在web安全配置类中配置就不生效等等像这样的问题，今天我就
 深入的研究了一下原来是三个类在IOC容器之中加载的优先级问题所造成的，下面我们就一步一步的来分析下三个类的优先级问题；
 
 #### 1.@EnableAuthorizationServer注解的类继承AuthorizationServerConfigurerAdapter类配置认证服务
 * 首先查看@EnableAuthorizationServer的源码如下
 ```
 
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({AuthorizationServerEndpointsConfiguration.class, AuthorizationServerSecurityConfiguration.class})
public @interface EnableAuthorizationServer {
}
```
* 上面的注解引入了两个类，我们点击进入ClientDetailsServiceConfiguration类中
```
@Configuration
@Order(0)
@Import({ClientDetailsServiceConfiguration.class, AuthorizationServerEndpointsConfiguration.class})
public class AuthorizationServerSecurityConfiguration extends WebSecurityConfigurerAdapter {
                        ...
``` 
上面的注解@Order(0)，也就是认证服务器配置的优先级为0；

#### 2.@EnableResourceServer注解的类继承ResourceServerConfigurerAdapter类配置资源服务器
* 点击注解@EnableResourceServer查看源码
```

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ResourceServerConfiguration.class})
public @interface EnableResourceServer {
}
```
* 注解中引入了ResourceServerConfiguration类，点击进入
```
@Configuration
public class ResourceServerConfiguration extends WebSecurityConfigurerAdapter implements Ordered {
    private int order = 3;
```
上面的类实现了Ordered接口，优先级为3

#### 3.@EnableWebSecurity注解修饰的类继承WebSecurityConfigurerAdapter类配置web安全配置
* 查看WebSecurityConfigurerAdapter的源码
```
@Order(100)
public abstract class WebSecurityConfigurerAdapter implements WebSecurityConfigurer<WebSecurity> {
```
上面的类使用注解@Order,优先级为100

#### 4.分析说明
* order的值越小，类的优先级越高，IOC容器就会优先加载，上面的优先级是：认证服务器配置（0）>资源服务器配置（3）>web安全服务配置（100）
* 在做资源权限配置的时候按照优先级高的来配置，否则不会生效

 ***
 GitHub源码：[https://github.com/mingyang66/spring-parent/tree/master/spring-security-oauth2-server-redis-service](https://github.com/mingyang66/spring-parent/tree/master/spring-security-oauth2-server-redis-service)

