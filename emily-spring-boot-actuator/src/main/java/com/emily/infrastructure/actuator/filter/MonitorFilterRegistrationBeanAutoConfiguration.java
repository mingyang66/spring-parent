package com.emily.infrastructure.actuator.filter;

import com.emily.infrastructure.logger.LoggerFactory;
import org.slf4j.Logger;
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
 * @author Emily
 * @program: spring-parent
 * @description: 过滤器配置类
 * @create: 2020/11/24
 */
@ManagementContextConfiguration(value = ManagementContextType.CHILD, proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class MonitorFilterRegistrationBeanAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(MonitorFilterRegistrationBeanAutoConfiguration.class);

    /**
     * 监控IP是否是内部IP过滤器
     */
    @Bean
    public FilterRegistrationBean<MonitorFilter> monitorIpFilterRegistrationBean() {
        FilterRegistrationBean<MonitorFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
        filterRegistrationBean.setFilter(new MonitorFilter());
        filterRegistrationBean.setName("monitorIpFilter");
        // 指示注册过滤器是否已经启用的标记
        filterRegistrationBean.setEnabled(true);
        return filterRegistrationBean;
    }

    @Override
    public void destroy() throws Exception {
        logger.info("<== 【销毁--自动化配置】----actuator过滤器注册组件【MonitorFilterRegistrationBeanAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("==> 【初始化--自动化配置】----actuator过滤器注册组件【MonitorFilterRegistrationBeanAutoConfiguration】");
    }
}
