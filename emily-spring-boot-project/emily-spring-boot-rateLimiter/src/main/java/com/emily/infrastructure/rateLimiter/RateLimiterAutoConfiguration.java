package com.emily.infrastructure.rateLimiter;

import com.emily.infrastructure.aop.advisor.AnnotationPointcutAdvisor;
import com.emily.infrastructure.aop.constant.AopOrderInfo;
import com.emily.infrastructure.rateLimiter.annotation.RateLimiter;
import com.emily.infrastructure.rateLimiter.interceptor.DefaultRateLimiterMethodInterceptor;
import com.emily.infrastructure.rateLimiter.interceptor.RateLimiterCustomizer;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
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
public class RateLimiterAutoConfiguration {
    private final RateLimiterProperties properties;

    public RateLimiterAutoConfiguration(RateLimiterProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor rateLimiterAdvisor(ObjectProvider<RateLimiterCustomizer> customizers) {
        Assert.isTrue(customizers.orderedStream().findFirst().isPresent(), "阀值拦截器必须存在");
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
}
