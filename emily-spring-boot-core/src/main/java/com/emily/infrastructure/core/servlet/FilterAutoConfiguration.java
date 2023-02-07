package com.emily.infrastructure.core.servlet;

import com.emily.infrastructure.core.servlet.filter.RequestChannelFilter;
import com.emily.infrastructure.core.servlet.filter.RoutingRedirectCustomizer;
import com.emily.infrastructure.core.servlet.filter.RoutingRedirectFilter;
import com.emily.infrastructure.logger.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 过滤器注册自动化配置
 * @create: 2020/11/23
 */
@AutoConfiguration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@EnableConfigurationProperties(FilterProperties.class)
@ConditionalOnProperty(prefix = FilterProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class FilterAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(FilterAutoConfiguration.class);

    /**
     * 注册HTTP请求拦截器注册BEAN
     *
     * @return
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnProperty(prefix = FilterProperties.PREFIX, name = "global-switch", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<RequestChannelFilter> filterRegistrationBean() {
        FilterRegistrationBean<RequestChannelFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setName("requestChannelFilter");
        filterRegistrationBean.setFilter(new RequestChannelFilter());
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }

    /**
     * 路由重定向过滤器
     *
     * @return
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnProperty(prefix = FilterProperties.PREFIX, name = "route-switch", havingValue = "true", matchIfMissing = false)
    public FilterRegistrationBean routeRegistrationBean(RoutingRedirectCustomizer routingRedirectCustomizer) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new RoutingRedirectFilter(routingRedirectCustomizer));
        registration.addUrlPatterns("/*");
        registration.setName("UrlFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registration;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = FilterProperties.PREFIX, name = "route-switch", havingValue = "true", matchIfMissing = false)
    public RoutingRedirectCustomizer routingRedirectCustomizer() {
        return new RoutingRedirectCustomizer() {
            @Override
            public String resolveSpecifiedLookupPath(HttpServletRequest request) {
                return request.getRequestURI();
            }
        };
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
