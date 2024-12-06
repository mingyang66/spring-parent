package com.emily.infrastructure.desensitize;

import com.emily.infrastructure.aop.advisor.AnnotationPointcutAdvisor;
import com.emily.infrastructure.desensitize.annotation.DesensitizeOperation;
import com.emily.infrastructure.desensitize.interceptor.DefaultDesensitizeMethodInterceptor;
import com.emily.infrastructure.desensitize.interceptor.DesensitizeCustomizer;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ComposablePointcut;
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
 * @author :  Emily
 * @since :  2024/12/7 下午3:47
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration
@EnableConfigurationProperties(DesensitizeProperties.class)
@ConditionalOnProperty(prefix = DesensitizeProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class DesensitizeAutoConfiguration {
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor dataSourcePointCutAdvice(ObjectProvider<DesensitizeCustomizer> customizers, DesensitizeProperties properties) {
        Assert.isTrue(customizers.orderedStream().findFirst().isPresent(), () -> "DesensitizeCustomizer must not be null");
        //限定类级别的切点
        Pointcut cpc = new AnnotationMatchingPointcut(DesensitizeOperation.class, false);
        //限定方法级别的切点
        Pointcut mpc = new AnnotationMatchingPointcut(null, DesensitizeOperation.class, false);
        //组合切面(并集)，即只要有一个切点的条件符合，则就拦截
        Pointcut pointcut = new ComposablePointcut(cpc).union(mpc);
        //切面增强类
        AnnotationPointcutAdvisor advisor = new AnnotationPointcutAdvisor(customizers.orderedStream().findFirst().get(), pointcut);
        //切面优先级顺序
        advisor.setOrder(0);
        return advisor;
    }

    @Bean
    @ConditionalOnMissingBean
    public DesensitizeCustomizer desensitizeCustomizer() {
        return new DefaultDesensitizeMethodInterceptor();
    }
}
