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
                    .authorizeRequests()
                    .antMatchers()
                    .permitAll()
                .and()
                    //其它的请求要求必须有身份认证
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()
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
     * @Description 自定义加密
     * @Date 2019/7/10 15:07
     * @Version  1.0
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
