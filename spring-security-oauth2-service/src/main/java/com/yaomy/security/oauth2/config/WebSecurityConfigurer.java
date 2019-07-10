package com.yaomy.security.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
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
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        //用户信息保存在内存中
        //在鉴定角色roler时，会默认加上ROLLER_前缀
        auth.inMemoryAuthentication()
                .withUser("user").password(passwordEncoder.encode("user")).roles("USER")
                .and()
                .withUser("test").password(passwordEncoder.encode("test")).roles("TEST");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin() //登记界面，默认是permit All
                .and()
                    .authorizeRequests()
                    .antMatchers("/oauth/*")
                    .permitAll() //不用身份认证可以访问
                .and()
                    //其它的请求要求必须有身份认证
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                .and()
                    .csrf() //防止CSRF（跨站请求伪造）配置
                    .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize")).disable();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
