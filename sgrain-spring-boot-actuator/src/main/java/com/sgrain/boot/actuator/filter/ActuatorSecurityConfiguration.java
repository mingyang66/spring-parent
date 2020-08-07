package com.sgrain.boot.actuator.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @description:
 * @create: 2020/07/30
 */
@Configuration
public class ActuatorSecurityConfiguration extends WebSecurityConfigurerAdapter {
     @Override
     protected void configure(HttpSecurity http) throws Exception {
       //对actuator监控所用的访问全部需要认证
     /*  http.formLogin()
               .and()
                   .authorizeRequests()
                   .antMatchers("/actuator/*")
                   .authenticated()
               .and()
                    .addFilterBefore(new ActuatorFilter(), UsernamePasswordAuthenticationFilter.class);*/
         http.authorizeRequests().anyRequest().permitAll()
                 .and().csrf().disable();
    }
}
