package com.emily.infrastructure.i18n;

import com.emily.infrastructure.aop.advisor.AnnotationPointcutAdvisor;
import com.emily.infrastructure.aop.constant.AopOrderInfo;
import com.emily.infrastructure.i18n.annotation.I18nOperation;
import com.emily.infrastructure.i18n.interceptor.DefaultI18nMethodInterceptor;
import com.emily.infrastructure.i18n.interceptor.I18nCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
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
 * 多语言翻译拦截器配置类
 *
 * @author :  Emily
 * @since :  2024/10/31 上午10:12
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration
@EnableConfigurationProperties(I18nProperties.class)
@ConditionalOnProperty(prefix = I18nProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class I18nAutoConfiguration implements InitializingBean, DisposableBean {
    private static final Logger LOG = LoggerFactory.getLogger(I18nAutoConfiguration.class);

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor i18nAdvisor(ObjectProvider<I18nCustomizer> customizers) {
        Assert.isTrue(customizers.orderedStream().findFirst().isPresent(), () -> "I18n customizers must not be null");
        //限定类级别的切点
        Pointcut cpc = new AnnotationMatchingPointcut(null, I18nOperation.class, true);
        //组合切面(并集)，即只要有一个切点的条件符合，则就拦截
        Pointcut pointcut = new ComposablePointcut(cpc);
        //切面增强类
        AnnotationPointcutAdvisor advisor = new AnnotationPointcutAdvisor(customizers.orderedStream().findFirst().get(), pointcut);
        //切面优先级顺序
        advisor.setOrder(AopOrderInfo.DATASOURCE);
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean
    public DefaultI18nMethodInterceptor defaultI18nMethodInterceptor() {
        return new DefaultI18nMethodInterceptor();
    }

    @Override
    public void destroy() {
        LOG.info("<== 【销毁--自动化配置】----I18n多语言组件【I18nAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        LOG.info("==> 【初始化--自动化配置】----I18n多语言组件【I18nAutoConfiguration】");
    }
}
