package com.yaomy.security.config;

import com.yaomy.security.filter.TokenAuthenticationFilter;
import com.yaomy.security.handler.*;
import com.yaomy.security.po.AuthUserDetailsService;
import com.yaomy.security.provider.UserAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.config.BaseSecurityConfig
 * @Author: 姚明洋
 * @Date: 2019/6/28 15:31
 * @Version: 1.0
 */
@Configuration
@EnableWebSecurity
public class BaseSecurityConfig extends WebSecurityConfigurerAdapter {
    //  未登陆时返回 JSON 格式的数据给前端（否则为 html）
    @Autowired
    UserAuthenticationEntryPoint authenticationEntryPoint;
    // 登录成功返回的 JSON 格式数据给前端（否则为 html）
    @Autowired
    UserAuthenticationSuccessHandler authenticationSuccessHandler;
    //  登录失败返回的 JSON 格式数据给前端（否则为 html）
    @Autowired
    UserAuthenticationFailureHandler authenticationFailureHandler;
    // 注销成功返回的 JSON 格式数据给前端（否则为 登录时的 html）
    @Autowired
    UserLogoutSuccessHandler logoutSuccessHandler;
    // 无权访问返回的 JSON 格式数据给前端（否则为 403 html 页面）
    @Autowired
    UserAccessDeniedHandler accessDeniedHandler;
    // JWT 拦截器
    @Autowired
    private TokenAuthenticationFilter jwtAuthenticationTokenFilter;
    @Autowired
    private AuthUserDetailsService authUserDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // 去掉 CSRF
        http.csrf().disable()
                // 使用 JWT，关闭token
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    //配置 Http Basic 验证
                    .httpBasic()
                    .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                    .authorizeRequests()
                    .antMatchers("/user/login").permitAll()
                    .anyRequest()
                    // RBAC 动态 url 认证
                    .access("@rbacServiceImpl.hasPermission(request, authentication)")
                .and()
                    //指定支持基于表单的身份验证。如果未指定FormLoginConfigurer#loginPage(String)，则将生成默认登录页面
                    .formLogin()
                    //自定义登录页url,默认为/login
                    .loginPage("/login.html")
                    //登录请求拦截的url,也就是form表单提交时指定的action
                    .loginProcessingUrl("/user/login")
                    //用户名的请求字段 username
                    .usernameParameter("username")
                    // 密码的请求字段 默认为password
                    .passwordParameter("password")
                    // 登录成功
                    .successHandler(authenticationSuccessHandler)
                    // 登录失败
                    .failureHandler(authenticationFailureHandler)
                    //无条件允许访问
                    .permitAll()
                .and()
                    .logout()
                    .logoutSuccessHandler(logoutSuccessHandler)
                    .permitAll();

        // 记住我
       // http.rememberMe().rememberMeParameter("remember-me").userDetailsService(userDetailsService).tokenValiditySeconds(600);
        // 无权访问 JSON 格式的数据
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler);
        // JWT Filter
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        AuthenticationProvider authenticationProvider = new UserAuthenticationProvider();
        return authenticationProvider;
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 加入自定义的安全认证
        auth.userDetailsService(this.authUserDetailsService).passwordEncoder(this.passwordEncoder());
        auth.authenticationProvider(this.authenticationProvider());
    }
}
