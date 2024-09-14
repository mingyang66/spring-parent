package com.emily.infrastructure.rateLimiter;

import com.emily.infrastructure.aop.advisor.AnnotationPointcutAdvisor;
import com.emily.infrastructure.aop.constant.AopOrderInfo;
import com.emily.infrastructure.rateLimiter.annotation.RateLimiter;
import com.emily.infrastructure.rateLimiter.interceptor.DefaultRateLimiterMethodInterceptor;
import com.emily.infrastructure.rateLimiter.interceptor.RateLimiterCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.util.Assert;

/**
 * 限流自动化配置类
 *
 * @author :  Emily
 * @since :  2024/8/29 下午5:30
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration
@EnableConfigurationProperties(RateLimiterProperties.class)
@ConditionalOnProperty(prefix = RateLimiterProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class RateLimiterAutoConfiguration implements BeanFactoryPostProcessor, InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(RateLimiterAutoConfiguration.class);

    @Bean("rateLimiterAdvisor")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor rateLimiterAdvisor(ObjectProvider<RateLimiterCustomizer> customizers, RateLimiterProperties properties) {
        Assert.isTrue(customizers.orderedStream().findFirst().isPresent(), "限流拦截器必须存在");
        //限定方法级别的切点
        Pointcut cpc = new AnnotationMatchingPointcut(null, RateLimiter.class, properties.isCheckInherited());
        AnnotationPointcutAdvisor advisor = new AnnotationPointcutAdvisor(customizers.orderedStream().findFirst().get(), cpc);
        advisor.setOrder(AopOrderInfo.RATE_LIMITER);
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(RateLimiterCustomizer.class)
    public RateLimiterCustomizer rateLimiterCustomizer() {
        return new DefaultRateLimiterMethodInterceptor();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanNames = beanFactory.getBeanNamesForType(RateLimiterProperties.class);
        if (beanNames.length > 0 && beanFactory.containsBeanDefinition(beanNames[0])) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanNames[0]);
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
    }

    @Override
    public void destroy() {
        logger.info("<== 【销毁--自动化配置】----限流组件【RateLimiterAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        logger.info("==> 【初始化--自动化配置】----限流组件【RateLimiterAutoConfiguration】");
    }
}
