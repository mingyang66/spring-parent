package com.yaomy.security.oauth2.config;

import com.yaomy.security.oauth2.provider.UserAuthenticationProvider;
import com.yaomy.security.oauth2.provider.UserSmsAuthenticationProvider;
import com.yaomy.security.oauth2.service.UserAuthDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Description: 启动基于Spring Security的安全认证,优先级顺序order=100-order的值越小，类的优先级越高
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.config.WebSecurityConfigurer
 * @Date: 2019/7/8 17:43
 * @Version: 1.0
 */
@SuppressWarnings("all")
@Configuration
@EnableWebSecurity(debug = true)
public class BaseSecurityConfigurer extends WebSecurityConfigurerAdapter {
    /**
     * 加载用户数据，提供给AuthenticationProvider使用
     */
    @Autowired
    private UserAuthDetailsService authUserDetailsService;
    @Override
    public void configure(WebSecurity web) throws Exception {
        //Spring Security应该忽略URLS以xxx开头的路由
         web.ignoring().antMatchers("/oauth2/**");
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/public/**").permitAll();
    }
    /**
     * @Description Spring Security认证服务中的相关实现重新定义
     * @Date 2019/7/4 17:40
     * @Version  1.0
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 加入自定义的安全认证
        auth
            .userDetailsService(this.authUserDetailsService)
            .passwordEncoder(this.passwordEncoder())
         .and()
            .authenticationProvider(smsAuthenticationProvider())
            .authenticationProvider(authenticationProvider());
    }

    /**
     * 授权管理器
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
}
