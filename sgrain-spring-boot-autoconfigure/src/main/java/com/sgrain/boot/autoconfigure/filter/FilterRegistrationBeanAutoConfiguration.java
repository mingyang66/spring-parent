package com.sgrain.boot.autoconfigure.filter;

import com.sgrain.boot.context.filter.RequestChannelFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.Arrays;

/**
 * @program: spring-parent
 * @description: 过滤器注册自动化配置
 * @create: 2020/11/23
 */
@Configuration(proxyBeanMethods = false)
public class FilterRegistrationBeanAutoConfiguration {
    /**
     * 注册HTTP请求拦截器注册BEAN
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean<RequestChannelFilter> filterRegistrationBean() {
        FilterRegistrationBean<RequestChannelFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setName("requestChannelFilter");
        filterRegistrationBean.setFilter(new RequestChannelFilter());
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }
}
