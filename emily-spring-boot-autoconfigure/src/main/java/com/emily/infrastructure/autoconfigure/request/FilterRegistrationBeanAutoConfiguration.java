package com.emily.infrastructure.autoconfigure.request;

import com.emily.infrastructure.autoconfigure.request.filter.RequestChannelFilter;
import com.emily.infrastructure.logback.factory.LogbackFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.Arrays;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 过滤器注册自动化配置
 * @create: 2020/11/23
 */
@Configuration(proxyBeanMethods = false)
public class FilterRegistrationBeanAutoConfiguration implements InitializingBean, DisposableBean {
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

    @Override
    public void destroy() {
        LogbackFactory.info(FilterRegistrationBeanAutoConfiguration.class, "<== 【销毁--自动化配置】----过滤器注册自动化配置组件【FilterRegistrationBeanAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        LogbackFactory.info(FilterRegistrationBeanAutoConfiguration.class, "==> 【初始化--自动化配置】----过滤器注册自动化配置组件【FilterRegistrationBeanAutoConfiguration】");
    }
}
