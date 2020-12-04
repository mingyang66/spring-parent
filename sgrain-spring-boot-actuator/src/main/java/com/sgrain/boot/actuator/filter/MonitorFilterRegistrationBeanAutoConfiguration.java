package com.sgrain.boot.actuator.filter;

import com.sgrain.boot.common.utils.log.LoggerUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

import java.util.Arrays;

/**
 * @program: spring-parent
 * @description: 过滤器配置类
 * @create: 2020/11/24
 */
@ManagementContextConfiguration(value = ManagementContextType.CHILD, proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class MonitorFilterRegistrationBeanAutoConfiguration implements InitializingBean, DisposableBean {
    /**
     * 监控IP是否是内部IP过滤器
     */
    @Bean
    public FilterRegistrationBean<MonitorIpFilter> monitorIpFilterRegistrationBean() {
        FilterRegistrationBean<MonitorIpFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
        filterRegistrationBean.setFilter(new MonitorIpFilter());
        filterRegistrationBean.setName("monitorIpFilter");
        return filterRegistrationBean;
    }

    @Override
    public void destroy() throws Exception {
        LoggerUtils.info(MonitorFilterRegistrationBeanAutoConfiguration.class, "【销毁--自动化配置】----actuator过滤器注册组件【MonitorFilterRegistrationBeanAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(MonitorFilterRegistrationBeanAutoConfiguration.class, "【初始化--自动化配置】----actuator过滤器注册组件【MonitorFilterRegistrationBeanAutoConfiguration】");
    }
}
