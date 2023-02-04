package com.emily.infrastructure.core.servlet;

import com.emily.infrastructure.core.servlet.filter.RequestChannelFilter;
import com.emily.infrastructure.logger.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;

import java.util.Arrays;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 过滤器注册自动化配置
 * @create: 2020/11/23
 */
@AutoConfiguration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class FilterAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(FilterAutoConfiguration.class);

    /**
     * 注册HTTP请求拦截器注册BEAN
     *
     * @return
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
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
        logger.info("<== 【销毁--自动化配置】----过滤器注册自动化配置组件【FilterAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        logger.info("==> 【初始化--自动化配置】----过滤器注册自动化配置组件【FilterAutoConfiguration】");
    }
}
