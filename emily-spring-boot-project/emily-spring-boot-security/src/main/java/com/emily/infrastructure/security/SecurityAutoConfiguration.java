package com.emily.infrastructure.security;


import com.emily.infrastructure.aop.advisor.AnnotationPointcutAdvisor;
import com.emily.infrastructure.aop.constant.AopOrderInfo;
import com.emily.infrastructure.security.annotation.SecurityOperation;
import com.emily.infrastructure.security.interceptor.DefaultSecurityMethodInterceptor;
import com.emily.infrastructure.security.interceptor.SecurityCustomizer;
import com.emily.infrastructure.security.plugin.ComplexSecurityPlugin;
import com.emily.infrastructure.security.plugin.SecurityPluginRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.util.Assert;

/**
 * 入参返回值加解密拦截器配置类
 *
 * @author :  Emily
 * @since :  2024/10/31 上午10:12
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnProperty(prefix = SecurityProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class SecurityAutoConfiguration implements InitializingBean, DisposableBean, ApplicationContextAware {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityAutoConfiguration.class);

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor i18nAdvisor(ObjectProvider<SecurityCustomizer> customizers) {
        Assert.isTrue(customizers.orderedStream().findFirst().isPresent(), () -> "Security customizers must not be null");
        //限定类级别的切点
        Pointcut cpc = new AnnotationMatchingPointcut(null, SecurityOperation.class, true);
        //组合切面(并集)，即只要有一个切点的条件符合，则就拦截
        Pointcut pointcut = new ComposablePointcut(cpc);
        //切面增强类
        AnnotationPointcutAdvisor advisor = new AnnotationPointcutAdvisor(customizers.orderedStream().findFirst().get(), pointcut);
        //切面优先级顺序
        advisor.setOrder(AopOrderInfo.Security);
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean
    public DefaultSecurityMethodInterceptor defaultSecurityMethodInterceptor() {
        return new DefaultSecurityMethodInterceptor();
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SecurityPluginRegistry.registerSecurityPlugin(context.getBeansOfType(ComplexSecurityPlugin.class));
    }

    @Override
    public void destroy() {
        LOG.info("<== 【销毁--自动化配置】----入参返回值加解密组件【SecurityAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        LOG.info("==> 【初始化--自动化配置】----入参返回值加解密组件【SecurityAutoConfiguration】");
    }
}
