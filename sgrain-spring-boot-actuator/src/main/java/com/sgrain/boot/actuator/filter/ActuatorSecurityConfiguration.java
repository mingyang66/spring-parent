package com.sgrain.boot.actuator.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @description:
 * @create: 2020/07/30
 */
@Configuration(proxyBeanMethods = false)
public class ActuatorSecurityConfiguration extends WebSecurityConfigurerAdapter {
     @Override
     protected void configure(HttpSecurity http) throws Exception {
         http.authorizeRequests().anyRequest().permitAll()
                 .and()
                 .addFilterBefore(new ActuatorFilter(), UsernamePasswordAuthenticationFilter.class)
                 .csrf().disable();
    }
}
