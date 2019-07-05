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
 * @Description: 启动基于Spring Security的安全认证
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.config.BaseSecurityConfig
 * @Date: 2019/6/28 15:31
 * @Version: 1.0
 */
@Configuration
@EnableWebSecurity
public class BaseSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    UserAuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    UserAuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    UserLogoutSuccessHandler logoutSuccessHandler;
    @Autowired
    UserAccessDeniedHandler accessDeniedHandler;
    @Autowired
    private TokenAuthenticationFilter tokenAuthenticationFilter;
    @Autowired
    private AuthUserDetailsService authUserDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {


            http
                // 去掉 CSRF（Cross-site request forgery）跨站请求伪造,依赖web浏览器，被混淆过的代理人攻击
                .csrf().disable()
                // 使用 JWT，使用无状态会话，不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    //配置 Http Basic 验证
                    .httpBasic()
                    //匿名用户异常拦截处理器
                    .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                    .authorizeRequests()
                    /**
                     * ant路径风格有三种通配符：[?]匹配任何单字符；[*]匹配0或者任意数量的字符；[**]匹配0或更多的目录
                     */
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
                    .permitAll()
                .and()
                    //认证过的用户访问无权限资源时的处理
                    .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
                .and()
                    //将JWT Token Filter验证配置到Spring Security
                    .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 记住我
       // http.rememberMe().rememberMeParameter("remember-me").userDetailsService(userDetailsService).tokenValiditySeconds(600);

    }
    /**
     * @Description JWT加密算法
     * @Date 2019/7/4 17:38
     * @Version  1.0
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
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
}
