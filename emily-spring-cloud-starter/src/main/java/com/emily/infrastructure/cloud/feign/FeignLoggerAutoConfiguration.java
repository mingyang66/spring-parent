package com.emily.infrastructure.cloud.feign;

import com.emily.infrastructure.cloud.feign.interceptor.FeignLoggerMethodInterceptor;
import com.emily.infrastructure.cloud.feign.interceptor.FeignRequestInterceptor;
import com.emily.infrastructure.cloud.feign.loadbalancer.FeignLoggerLoadBalancerLifecycle;
import com.emily.infrastructure.cloud.feign.logger.FeignLogger;
import com.emily.infrastructure.common.constant.AopOrderInfo;
import com.emily.infrastructure.core.aop.advisor.AnnotationPointcutAdvisor;
import com.emily.infrastructure.logger.LoggerFactory;
import feign.Logger;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.retry.annotation.RetryConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.function.Supplier;

/**
 * @author Emily
 * @Description: 控制器切点配置
 * @Version: 1.0
 */
@Configuration
@EnableConfigurationProperties(FeignLoggerProperties.class)
@ConditionalOnProperty(prefix = FeignLoggerProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class FeignLoggerAutoConfiguration implements BeanFactoryPostProcessor, InitializingBean, DisposableBean {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FeignLoggerAutoConfiguration.class);

    /**
     * @Description 定义接口拦截器切点
     * @Version 1.0
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor feignAdvisor(FeignLoggerMethodInterceptor feignLoggerMethodInterceptor) {
        //限定类|方法级别的切点
        Pointcut pointcut = new AnnotationMatchingPointcut(FeignClient.class, RequestMapping.class, true);
        //切面增强类
        AnnotationPointcutAdvisor advisor = new AnnotationPointcutAdvisor(feignLoggerMethodInterceptor, pointcut);
        //设置增强拦截器执行顺序
        advisor.setOrder(AopOrderInfo.FEIGN);
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public FeignLoggerMethodInterceptor feignLoggerMethodInterceptor() {
        return new FeignLoggerMethodInterceptor();
    }

    /**
     * Feign 请求日志拦截
     */
    @Bean
    public FeignRequestInterceptor feignRequestInterceptor() {
        Supplier<FeignRequestInterceptor> supplier = FeignRequestInterceptor::new;
        return supplier.get();
    }

    /**
     * Feign 声明周期管理，主要获取真实URL
     */
    @Bean
    public FeignLoggerLoadBalancerLifecycle feignLogLoadBalancerLifecycle() {
        Supplier<FeignLoggerLoadBalancerLifecycle> supplier = FeignLoggerLoadBalancerLifecycle::new;
        return supplier.get();
    }

    /**
     * 自定义日志系统代理feign日志系统
     */
    @Bean
    public Logger logger() {
        Supplier<Logger> supplier = FeignLogger::new;
        return supplier.get();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (beanFactory.containsBeanDefinition(RetryConfiguration.class.getName())) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(RetryConfiguration.class.getName());
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
    }

    @Override
    public void destroy() throws Exception {
        logger.info("<== 【销毁--自动化配置】----Feign日志记录组件【FeignLoggerAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("==> 【初始化--自动化配置】----Feign日志记录组件【FeignLoggerAutoConfiguration】");
    }
}
