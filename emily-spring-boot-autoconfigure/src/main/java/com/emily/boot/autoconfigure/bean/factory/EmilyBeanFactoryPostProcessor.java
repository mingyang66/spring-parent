package com.emily.boot.autoconfigure.bean.factory;

import com.emily.boot.autoconfigure.apilog.ApiLogAutoConfiguration;
import com.emily.boot.autoconfigure.apilog.ApiLogProperties;
import com.emily.boot.autoconfigure.ratelimit.RateLimitAutoConfiguration;
import com.emily.boot.autoconfigure.threadpool.AsyncThreadPoolAutoConfiguration;
import com.emily.boot.autoconfigure.threadpool.AsyncThreadPoolProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

/**
 * @program: spring-parent
 * @description: bean注册成功但未实例化之前调用的后置处理器，用来更改BeanDefinition
 * @create: 2020/09/11
 */
public class EmilyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (beanFactory.containsBeanDefinition(AsyncThreadPoolAutoConfiguration.class.getName())) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(AsyncThreadPoolAutoConfiguration.class.getName());
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition("spring.emily.async-thread-pool-" + AsyncThreadPoolProperties.class.getName())) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("spring.emily.async-thread-pool-" + AsyncThreadPoolProperties.class.getName());
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition("spring.emily.api-log-" + ApiLogProperties.class.getName())) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("spring.emily.api-log-" + ApiLogProperties.class.getName());
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition(ApiLogAutoConfiguration.class.getName())) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(ApiLogAutoConfiguration.class.getName());
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition("asyncLogAopService")) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("asyncLogAopService");
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition(ApiLogAutoConfiguration.API_LOG_EXCEPTION_BEAN_NAME)) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(ApiLogAutoConfiguration.API_LOG_EXCEPTION_BEAN_NAME);
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition(ApiLogAutoConfiguration.API_LOG_NORMAL_BEAN_NAME)) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(ApiLogAutoConfiguration.API_LOG_NORMAL_BEAN_NAME);
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition(RateLimitAutoConfiguration.RATE_LIMIT_POINT_CUT_ADVISOR_NAME)) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(RateLimitAutoConfiguration.RATE_LIMIT_POINT_CUT_ADVISOR_NAME);
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition(RateLimitAutoConfiguration.class.getName())) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(RateLimitAutoConfiguration.class.getName());
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition(RedisAutoConfiguration.class.getName())) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(RedisAutoConfiguration.class.getName());
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition(StringUtils.join("spring.redis-", RedisProperties.class.getName()))) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(StringUtils.join("spring.redis-", RedisProperties.class.getName()));
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition("org.springframework.boot.autoconfigure.data.redis.LettuceConnectionConfiguration")) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("org.springframework.boot.autoconfigure.data.redis.LettuceConnectionConfiguration");
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition("redisConnectionFactory")) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("redisConnectionFactory");
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition("lettuceClientResources")) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("lettuceClientResources");
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition("stringRedisTemplate")) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("stringRedisTemplate");
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition("org.springframework.retry.annotation.RetryConfiguration")) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("org.springframework.retry.annotation.RetryConfiguration");
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
    }
}
