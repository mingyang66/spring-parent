package com.yaomy.security.openid.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.openid.config.SecurityConfiguration
 * @Date: 2019/7/5 17:32
 * @Version: 1.0
 */
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    /**
     * @Description Web拦截器
     * @Date 2019/7/5 17:33
     * @Version  1.0
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .and().csrf().disable()
                .authorizeRequests().anyRequest().authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("lyt").password("lyt").authorities("ROLE_USER")
                .and().withUser("admin").password("admin").authorities("ROLE_ADMIN");
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
