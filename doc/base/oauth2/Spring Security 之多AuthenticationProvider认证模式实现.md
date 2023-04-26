### Spring Security 之多AuthenticationProvider认证模式实现

多AuthenticationProvider认证模式实现原理及源码分析可参考：[https://github.com/mingyang66/spring-parent/blob/master/spring-security-oauth2-server-redis-service/providermanager.md](https://github.com/mingyang66/spring-parent/blob/master/spring-security-oauth2-server-redis-service/providermanager.md)

> 多AuthenticationProvider实现ProviderManager会按照添加入认证请求链中的顺序来验证，上面的源码分析及实现原理已经说的很清楚了，
> 这一片就直接看代码实现；

#### 1.AuthenticationProvider认证类UserSmsAuthenticationProvider实现

```
package com.yaomy.security.oauth2.provider;

import com.yaomy.security.oauth2.event.event.UserLoginFailedEvent;
import com.yaomy.security.oauth2.exception.PasswordException;
import com.yaomy.security.oauth2.exception.UsernameException;
import com.yaomy.security.oauth2.service.UserAuthDetailsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @Description: 用户自定义身份认证,短信验证码模式
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.provider.MyAuthenticationProvider
 * @Date: 2019/7/2 17:17
 * @Version: 1.0
 */
@Component
public class UserSmsAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserAuthDetailsService authUserDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ApplicationEventPublisher publisher;
    /**
     * @Description 认证处理，返回一个Authentication的实现类则代表认证成功，返回null则代表认证失败
     * @Date 2019/7/5 15:19
     * @Version  1.0
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //测试，正式环境应该根据登录认证的通道来进行认证
        String username = authentication.getName();
        String smscode = (String) authentication.getCredentials();
        if(StringUtils.isBlank(username)){
            throw new UsernameNotFoundException("username用户名不可以为空");
        }
        if(StringUtils.isBlank(smscode)){
            throw new BadCredentialsException("验证码不可以为空");
        }
        //获取用户信息
        UserDetails user = authUserDetailsService.loadUserByUsername(username);
        String smscodeCache = "1234";
        //比较前端传入的密码明文和数据库中加密的密码是否相等
        if (!passwordEncoder.matches(smscode, smscodeCache)) {
            //发布密码不正确事件
            publisher.publishEvent(new UserLoginFailedEvent(authentication));
            throw new BadCredentialsException("sms_code验证码不正确");
        }
        //获取用户权限信息
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        return new UsernamePasswordAuthenticationToken(user, smscode, authorities);

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

#### 2.AuthenticationProvider认证类UserAuthenticationProvider实现

```
package com.yaomy.security.oauth2.provider;

import com.yaomy.security.oauth2.event.event.UserLoginFailedEvent;
import com.yaomy.security.oauth2.exception.PasswordException;
import com.yaomy.security.oauth2.exception.UsernameException;
import com.yaomy.security.oauth2.service.UserAuthDetailsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private UserAuthDetailsService authUserDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ApplicationEventPublisher publisher;
    /**
     * @Description 认证处理，返回一个Authentication的实现类则代表认证成功，返回null则代表认证失败
     * @Date 2019/7/5 15:19
     * @Version  1.0
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        if(StringUtils.isBlank(username)){
            throw new UsernameNotFoundException("username用户名不可以为空");
        }
        if(StringUtils.isBlank(password)){
            throw new BadCredentialsException("密码不可以为空");
        }
        //获取用户信息
        UserDetails user = authUserDetailsService.loadUserByUsername(username);
        //比较前端传入的密码明文和数据库中加密的密码是否相等
        if (!passwordEncoder.matches(password, user.getPassword())) {
            //发布密码不正确事件
            publisher.publishEvent(new UserLoginFailedEvent(authentication));
            throw new BadCredentialsException("password密码不正确");
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

#### 3.Security安全配置类实现

```
package com.yaomy.security.oauth2.config;

import com.yaomy.security.oauth2.provider.UserSmsAuthenticationProvider;
import com.yaomy.security.oauth2.service.UserAuthDetailsService;
import com.yaomy.security.oauth2.provider.UserAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * @Description: 启动基于Spring Security的安全认证,优先级顺序order=100
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.config.WebSecurityConfigurer
 * @Date: 2019/7/8 17:43
 * @Version: 1.0
 */
@EnableWebSecurity(debug = true)
public class BaseSecurityConfigurer extends WebSecurityConfigurerAdapter {
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private UserAuthDetailsService authUserDetailsService;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
    }
    @Override
    public void configure(WebSecurity web) throws Exception {
        //忽略swagger访问权限限制
       // web.ignoring().antMatchers(
        //        "/userlogin",
            //    "/userlogout",
           //     "/userjwt",
             //   "/v2/api-docs",
             //   "/swagger-resources/configuration/ui",
              //  "/swagger-resources",
               // "/swagger-resources/configuration/security",
                //"/swagger-ui.html",
                //"/css/**",
                //"/js/**",
                //"/images/**",
                //"/webjars/**",
                //"**/favicon.ico",
                //"/index");
        super.configure(web);
    }
    /**
     * @Description Spring Security认证服务中的相关实现重新定义
     * @Date 2019/7/4 17:40
     * @Version  1.0
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 加入自定义的安全认证
        auth.userDetailsService(this.authUserDetailsService)
                .passwordEncoder(this.passwordEncoder())
             .and()
                //添加自定义的认证管理类
                .authenticationProvider(smsAuthenticationProvider())
                .authenticationProvider(authenticationProvider());
    }
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
    @Bean
    public AuthenticationProvider smsAuthenticationProvider(){
        AuthenticationProvider authenticationProvider = new UserSmsAuthenticationProvider();
        return authenticationProvider;
    }
    /**
     * @Description 自定义加密
     * @Date 2019/7/10 15:07
     * @Version  1.0
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
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
}
```

GitHub源码：[https://github.com/mingyang66/spring-parent/blob/master/spring-security-oauth2-server-redis-service/authenticateprovider.md](https://github.com/mingyang66/spring-parent/blob/master/spring-security-oauth2-server-redis-service/authenticateprovider.md)