package com.yaomy.control.test.service;

import com.yaomy.control.test.TestFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @program: spring-parent
 * @description:
 * @author: 姚明洋
 * @create: 2020/11/19
 */
@Configuration(proxyBeanMethods = false)
public class TestConfig {
 /*   @Bean
    public FilterRegistrationBean crossDomainFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new TestFilter());
        registration.addUrlPatterns("/*");
        registration.setName("CrossDomainFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }*/

}
