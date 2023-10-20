package com.emily.infrastructure.core.context;

import com.emily.infrastructure.core.context.ioc.BeanFactoryUtils;
import com.emily.infrastructure.logger.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;

/**
 * 全链路追踪上下文自动化配置
 *
 * @author Emily
 * @since 2021/11/27
 */
@AutoConfiguration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(ContextProperties.class)
@ConditionalOnProperty(prefix = ContextProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class ContextAutoConfiguration implements BeanFactoryPostProcessor, InitializingBean, DisposableBean {
    public static final String BEANNAME = "advisor";
    private static final Logger logger = LoggerFactory.getLogger(ContextAutoConfiguration.class);

    /**
     * 将指定的bean 角色标记为基础设施类型，相关提示类在 org.springframework.context.support.PostProcessorRegistrationDelegate
     *
     * @param beanFactory 工厂类
     * @throws BeansException 异常
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //设置IOC容器工厂类
        BeanFactoryUtils.registerDefaultListableBeanFactory((DefaultListableBeanFactory) beanFactory);
        if (beanFactory.containsBeanDefinition(BEANNAME)) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(BEANNAME);
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
    }

    @Override
    public void destroy() {
        logger.info("<== 【销毁--自动化配置】----全链路日志追踪组件【ContextAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        logger.info("==> 【初始化--自动化配置】----全链路日志追踪组件【ContextAutoConfiguration】");
    }
}
