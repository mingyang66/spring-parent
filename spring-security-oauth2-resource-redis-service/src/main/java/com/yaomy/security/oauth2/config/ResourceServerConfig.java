package com.yaomy.security.oauth2.config;

import com.yaomy.security.oauth2.handler.UserAccessDeniedHandler;
import com.yaomy.security.oauth2.handler.UserAuthenticationEntryPoint;
import com.yaomy.security.oauth2.handler.UserAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * @Description: @EnableResourceServer注解实际上相当于加上OAuth2AuthenticationProcessingFilter过滤器
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.config.ResServerConfig
 * @Date: 2019/7/9 13:28
 * @Version: 1.0
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private UserAuthenticationEntryPoint userAuthenticationEntryPoint;
    @Autowired
    private UserAccessDeniedHandler userAccessDeniedHandler;
    @Autowired
    private UserAuthenticationSuccessHandler userAuthenticationSuccessHandler;
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources
                .tokenServices(tokenServices())
                //资源ID
                .resourceId("resource_password_id")
                //用来解决匿名用户访问无权限资源时的异常
                .authenticationEntryPoint(userAuthenticationEntryPoint)
                //访问资源权限相关异常处理
                .accessDeniedHandler(userAccessDeniedHandler);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http
            .authorizeRequests()
            .antMatchers("/resource/auth")
            .denyAll()
        .and()
            .authorizeRequests()
            .anyRequest()
            .permitAll();

    }
    /**
     * @Description OAuth2 token持久化接口
     * @Date 2019/7/15 18:12
     * @Version  1.0
     */
    @Bean
    public TokenStore tokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }
    /**
     * @Description 令牌服务
     * @Date 2019/7/15 18:07
     * @Version  1.0
     */
    @Bean
    public DefaultTokenServices tokenServices(){
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }
    /**
     * @Description 加密方式
     * @Date 2019/7/15 18:06
     * @Version  1.0
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
