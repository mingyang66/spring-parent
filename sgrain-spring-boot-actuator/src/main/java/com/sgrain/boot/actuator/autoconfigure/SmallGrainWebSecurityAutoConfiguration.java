package com.sgrain.boot.actuator.autoconfigure;

import com.sgrain.boot.actuator.filter.MonitorIpFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @description:
 * @create: 2020/07/30
 */
@Configuration(proxyBeanMethods = false)
public class SmallGrainWebSecurityAutoConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().antMatchers("/api/**", "/actuator/**").permitAll()
            .and()
                .authorizeRequests().anyRequest().denyAll()
            .and()
                .addFilterBefore(new MonitorIpFilter(), UsernamePasswordAuthenticationFilter.class)
                .httpBasic().disable();
    }
}
