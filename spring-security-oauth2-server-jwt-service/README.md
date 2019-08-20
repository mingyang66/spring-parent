# spring-parent
<h3>Spring Security OAuth2 JWT认证服务器配置</h3> 

#### 1.四种授权模式
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
#### 4.依赖pom
```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.security.oauth</groupId>
            <artifactId>spring-security-oauth2</artifactId>
            <version>2.3.5.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-jwt</artifactId>
            <version>1.0.10.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
```

#### 5.认证服务器配置
```
package com.yaomy.security.oauth2.config;

import com.yaomy.security.oauth2.enhancer.UserTokenEnhancer;
import com.yaomy.security.oauth2.po.AuthUserDetailsService;
import com.yaomy.security.oauth2.service.OAuth2ClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.core.userdetails.User;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: @EnableAuthorizationServer注解开启OAuth2授权服务机制
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.config.AuthorizationServerConfig
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
        tokenServices.setTokenEnhancer(accessTokenConverter());
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
                //控制TokenEndpoint端点请求访问的类型，默认HttpMethod.POST
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
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
                .checkTokenAccess("permitAll()");
    }
    /**
     * @Description OAuth2 token持久化接口
     * @Date 2019/7/9 17:45
     * @Version  1.0
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }
    /**
     * @Description 自定义token令牌增强器
     * @Date 2019/7/11 16:22
     * @Version  1.0
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter(){
        JwtAccessTokenConverter accessTokenConverter = new UserTokenEnhancer();
        accessTokenConverter.setSigningKey("123");
        return accessTokenConverter;
    }
}
```

#### 6.自定义ClientDetailsService实现类

```
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
 * @Description: 自定义client详细信息类
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.service.OAuth2ClientDetailsService
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
                    .withClient("auth_code")
                    // client secret
                    .secret(passwordEncoder.encode("secret"))
                    /**
                     ----支持的认证授权类型----
                     示例（授权码）：http://localhost:9001/oauth/authorize?client_id=auth_code&response_type=code&redirect_uri=http://localhost:9001/auth_user/get_auth_code
                     示例（access_token）：http://localhost:9001/oauth/token?grant_type=authorization_code&code=kb04Ur&client_id=auth_code&client_secret=secret&redirect_uri=http://localhost:9001/auth_user/get_auth_code
                     refresh_token示例：http://localhost:9001/oauth/token?client_id=auth_code&client_secret=secret&grant_type=refresh_token&refresh_token=xxxx
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
                    //client secret
                    .secret(passwordEncoder.encode("secret"))
                    /**
                     ----密码模式---
                     自己有一套账号权限体系在认证服务器中对应,客户端认证的时候需要带上自己的用户名和密码
                     示例：http://localhost:9001/oauth/token?username=user&password=123&grant_type=password&client_id=client_password&client_secret=secret
                     refresh_token示例：http://localhost:9001/oauth/token?grant_type=refresh_token&refresh_token=xxx&client_id=client_password&client_secret=secret
                     --client_id：客户端ID，必选
                     --client_secret：客户端密码，必选
                     --grant_type：必须为password，必选
                     --username:用户名，必选
                     --password:密码，必选
                     */
                    .authorizedGrantTypes("password","refresh_token")
                    //资源ID
                    .resourceIds("resource_password_id")
                    // 允许的授权范围
                    .scopes("test","ceshi")
                .and()
                    .withClient("client")
                    //client secret
                    .secret(passwordEncoder.encode("secret"))
                    /**
                     ---clent模式---
                     没有用户的概念，直接与认证服务器交互，用配置中的用户信息去申请access_token,客户端有自己的client_id、client_secret对应于用户的username、password,
                     客户端拥有自己的authorities，采用client模式认证，客户端的权限就是客户端自己的权限
                     示例：http://localhost:9001/oauth/token?grant_type=client_credentials&scope=insert&client_id=client&client_secret=secret
                     --client_id：客户端ID，必选
                     --client_secret：客户端密码，必选
                     --grant_type：必须为password，必选
                     --scope：授权范围，必选
                     */
                    .authorizedGrantTypes("client_credentials","refresh_token")
                    //允许的授权范围
                    .scopes("insert","del", "update")
                .and()
                    .withClient("client_implicit")
                    /**
                     ---授权模式：极简模式---
                     示例:http://localhost:9001/oauth/authorize?client_id=client_implicit&response_type=token&redirect_uri=http://localhost:9001/auth_user/get_auth_code
                     */
                    .authorizedGrantTypes("implicit")
                    //回调uri，在authorization_code与implicit授权方式时，用以接收服务器的返回信息
                    .redirectUris("http://localhost:9001/auth_user/get_auth_code")
                    //允许的授权范围
                    .scopes("del","update");
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
```

#### 7.自定义WebSecurityConfigurerAdapter实现类

```
package com.yaomy.security.oauth2.config;

import com.yaomy.security.oauth2.handler.*;
import com.yaomy.security.oauth2.po.AuthUserDetailsService;
import com.yaomy.security.oauth2.provider.UserAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * @Description: 启动基于Spring Security的安全认证
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.config.WebSecurityConfigurer
 * @Date: 2019/7/8 17:43
 * @Version: 1.0
 */
@Configuration
@EnableWebSecurity
public class BaseSecurityConfigurer extends WebSecurityConfigurerAdapter {
    @Autowired
    private AuthUserDetailsService authUserDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserAccessDeniedHandler accessDeniedHandler;
    @Autowired
    private UserAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private UserAuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private UserAuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private UserLogoutSuccessHandler logoutSuccessHandler;
    @Override
    protected void configure(HttpSecurity http) throws Exception {

            http
                    //指定支持基于表单的身份验证。如果未指定FormLoginConfigurer#loginPage(String)，则将生成默认登录页面
                .formLogin()
                    //自定义登录页url,默认为/login
                    .loginPage("/test/login")
                    //登录请求拦截的url,也就是form表单提交时指定的action
                    .loginProcessingUrl("/user/login")
                    //用户名的请求字段 username
                    .usernameParameter("username")
                    // 密码的请求字段 默认为password
                    .passwordParameter("password")
                    // 登录成功
                   // .successHandler(authenticationSuccessHandler)
                    // 登录失败
                    .failureHandler(authenticationFailureHandler)
                    //无条件允许访问
                    .permitAll()
                .and()
                    .requestMatchers()
                    .anyRequest()
                .and()
                    .authorizeRequests()
                    .antMatchers("/oauth/**")
                    .permitAll()
              /*  .and()
                    //其它的请求要求必须有身份认证
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()*/
                .and()
                    .logout()
                    .logoutSuccessHandler(logoutSuccessHandler)
                    .permitAll()
                .and()
                    //认证过的用户访问无权限资源时的处理
                    .exceptionHandling().accessDeniedHandler(accessDeniedHandler);
            http.csrf().disable();
    }
    /**
     * @Description Spring Security认证服务中的相关实现重新定义
     * @Date 2019/7/4 17:40
     * @Version  1.0
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 加入自定义的安全认证
        auth.userDetailsService(this.authUserDetailsService).passwordEncoder(this.passwordEncoder());
        auth.authenticationProvider(this.authenticationProvider());
    }
    /**
     * @Description 需要主动暴漏AuthenticationManager，否则会报异常
     * @Date 2019/7/12 13:42
     * @Version  1.0
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    /**
     * @Description Spring security认证Bean
     * @Date 2019/7/4 17:39
     * @Version  1.0
     */
    @Bean
    public AuthenticationProvider authenticationProvider(){
        AuthenticationProvider authenticationProvider = new UserAuthenticationProvider();
        return authenticationProvider;
    }
    /**
     * @Description 自定义加密器
     * @Date 2019/7/10 15:07
     * @Version  1.0
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
```

#### 8.自定义JWT token增强类
```
package com.yaomy.security.oauth2.enhancer;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Map;

/**
 * @Description: 用户自定义token令牌，包括access_token和refresh_token
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.enhancer.UserTokenEnhancer
 * @Date: 2019/7/9 19:43
 * @Version: 1.0
 */
public class UserTokenEnhancer extends JwtAccessTokenConverter {
    /**
     * @Description 重新定义令牌token
     * @Date 2019/7/9 19:56
     * @Version  1.0
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        //授权类型 authorization_code、password、client_credentials、refresh_token、implicit
        String grantType = authentication.getOAuth2Request().getGrantType();
        if(!StringUtils.equals(grantType, "client_credentials")){
            String userName = authentication.getUserAuthentication().getName();
            // 与登录时候放进去的UserDetail实现类一直查看link{SecurityConfiguration}
            Object principal = authentication.getUserAuthentication().getPrincipal();
            /**
             自定义一些token属性
             **/
            Map<String, Object> additionalInformation = Maps.newHashMap();
            additionalInformation.put("username", userName);
            if(principal instanceof User){
                additionalInformation.put("principal", ((User)principal).getAuthorities());
            } else {
                additionalInformation.put("principal", principal);
            }
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);

        }
        OAuth2AccessToken enhancedToken = super.enhance(accessToken, authentication);
        return enhancedToken;
    }
}
```
#### 9.用户自定义身份认证AuthenticationProvider

```
package com.yaomy.security.oauth2.provider;

import com.yaomy.security.oauth2.po.AuthUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @Description: 用户自定义身份认证
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.provider.MyAuthenticationProvider
 * @Date: 2019/7/2 17:17
 * @Version: 1.0
 */
@Component
public class UserAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private AuthUserDetailsService authUserDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    /**
     * @Description 认证处理，返回一个Authentication的实现类则代表认证成功，返回null则代表认证失败
     * @Date 2019/7/5 15:19
     * @Version  1.0
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        //获取用户信息
        UserDetails user = authUserDetailsService.loadUserByUsername(username);
        //比较前端传入的密码明文和数据库中加密的密码是否相等
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new DisabledException("用户密码不正确");
        }
        //获取用户权限信息
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        return new UsernamePasswordAuthenticationToken(user, password, authorities);

    }
    /**
     * @Description 如果该AuthenticationProvider支持传入的Authentication对象，则返回true
     * @Date 2019/7/5 15:18
     * @Version  1.0
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }

}
```

#### 10.自定义登陆页面
```
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>登录</title>
</head>

<style>
    .login-container {
        margin: 50px;
        width: 100%;
    }

    .form-container {
        margin: 0px auto;
        width: 50%;
        text-align: center;
        box-shadow: 1px 1px 10px #888888;
        height: 300px;
        padding: 5px;
    }

    input {
        margin-top: 10px;
        width: 350px;
        height: 30px;
        border-radius: 3px;
        border: 1px #E9686B solid;
        padding-left: 2px;

    }


    .btn {
        width: 350px;
        height: 35px;
        line-height: 35px;
        cursor: pointer;
        margin-top: 20px;
        border-radius: 3px;
        background-color: #E9686B;
        color: white;
        border: none;
        font-size: 15px;
    }

    .title{
        margin-top: 5px;
        font-size: 18px;
        color: #E9686B;
    }
</style>
<body>
<div class="login-container">
    <div class="form-container">
        <p class="title">用户登录</p>
        <form name="loginForm" method="post" th:action="${loginProcessUrl}">
            <input type="text" name="username" placeholder="用户名"/>
            <br>
            <input type="password" name="password" placeholder="密码"/>
            <br>
            <button type="submit" class="btn">登 &nbsp;&nbsp; 录</button>
        </form>
    </div>
</div>
</body>
</html>
```

#### 11.自定义授权页面
```
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>授权</title>
</head>
<style>

    html{
        padding: 0px;
        margin: 0px;
    }

    .title {
        background-color: #E9686B;
        height: 50px;
        padding-left: 20%;
        padding-right: 20%;
        color: white;
        line-height: 50px;
        font-size: 18px;
    }
    .title-left{
        float: right;
    }
    .title-right{
        float: left;
    }
    .title-left a{
        color: white;
    }
    .container{
        clear: both;
        text-align: center;
    }
    .btn {
        width: 350px;
        height: 35px;
        line-height: 35px;
        cursor: pointer;
        margin-top: 20px;
        border-radius: 3px;
        background-color: #E9686B;
        color: white;
        border: none;
        font-size: 15px;
    }
</style>
<body style="margin: 0px">
<div class="title">
    <div class="title-right">Spring Security 授权</div>
    <div class="title-left">
        <a href="#help">帮助</a>
    </div>
</div>
<div class="container">
    <h3 th:text="${clientId}+' 请求授权，该应用将获取你的以下信息'"></h3>
    <form method="post" action="/oauth/authorize">
        <input type="hidden" name="user_oauth_approval" value="true">
        <!--<input type="hidden" name="_csrf" th:value="${_csrf.getToken()}"/>-->
        <ul style="list-style-type: none">
            <li th:each="s:${scope}">
                <div class="form-group"><a th:text="${s}"></a>: <input type="radio" th:name="'scope.'+${s}" value="true">Approve(授权) <input type="radio" th:name="${s}" value="false" checked="">Deny（拒绝）</div>
            </li>
        </ul>
        授权后表明你已同意 <a  href="#boot" style="color: #E9686B">服务协议</a><br>
        <button class="btn" type="submit"> 同意/授权</button>
    </form>
</div>
</body>
</html>
```

#### 12.自定义授权重定向相关接口
```
package com.yaomy.security.oauth2.api;

import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Description: 自定义登陆页面
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.api.GrantController
 * @Date: 2019/7/10 16:28
 * @Version: 1.0
 */
@Controller
@SessionAttributes("authorizationRequest")
public class GrantController {
    @GetMapping("/test/login")
    public String index(Model model) {
        model.addAttribute("loginProcessUrl","/user/login");
        return "login";
    }
    @RequestMapping("/custom/confirm_access")
    public ModelAndView getAccessConfirmation(Map<String, Object> model, HttpServletRequest request) throws Exception {
        AuthorizationRequest authorizationRequest = (AuthorizationRequest) model.get("authorizationRequest");
        ModelAndView view = new ModelAndView();
        view.setViewName("grant");
        view.addObject("clientId", authorizationRequest.getClientId());
        view.addObject("scope", authorizationRequest.getScope());
        System.out.println(authorizationRequest.getScope());
        System.out.println(authorizationRequest.getClientId());
        return view;
    }

}
```
上面展示了主要得一些实现类，其他的一些辅助类可以参考源码：<br/>
GitHub源码地址：[https://github.com/mingyang66/spring-parent/tree/master/spring-security-oauth2-server-jwt-service](https://github.com/mingyang66/spring-parent/tree/master/spring-security-oauth2-server-jwt-service)