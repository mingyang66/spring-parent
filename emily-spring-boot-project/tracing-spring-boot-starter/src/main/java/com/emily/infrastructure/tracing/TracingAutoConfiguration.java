package com.emily.infrastructure.tracing;

import com.emily.infrastructure.aop.advisor.AnnotationPointcutAdvisor;
import com.emily.infrastructure.aop.constant.AopOrderInfo;
import com.emily.infrastructure.tracing.annotation.TracingOperation;
import com.emily.infrastructure.tracing.helper.SystemNumberHelper;
import com.emily.infrastructure.tracing.interceptor.DefaultTracingMethodInterceptor;
import com.emily.infrastructure.tracing.interceptor.TracingCustomizer;
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
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;

/**
 * 全链路追踪上下文自动化配置
 *
 * @author Emily
 * @since 2021/11/27
 */
@AutoConfiguration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(TracingProperties.class)
@ConditionalOnProperty(prefix = TracingProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class TracingAutoConfiguration implements InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(TracingAutoConfiguration.class);
    private final ThreadPoolTaskExecutor taskExecutor;

    public TracingAutoConfiguration(TracingProperties properties, ThreadPoolTaskExecutor taskExecutor) {
        SystemNumberHelper.setSystemNumber(properties.getSystemNumber());
        this.taskExecutor = taskExecutor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor tracingAdvisor(ObjectProvider<TracingCustomizer> customizers) {
        Assert.isTrue(customizers.orderedStream().findFirst().isPresent(), () -> "TracingCustomizer must not be null");
        //限定类级别的切点
        Pointcut cpc = new AnnotationMatchingPointcut(TracingOperation.class, false);
        //限定方法级别的切点
        Pointcut mpc = new AnnotationMatchingPointcut(null, TracingOperation.class, false);
        //组合切面(并集)，即只要有一个切点的条件符合，则就拦截
        Pointcut pointcut = new ComposablePointcut(cpc).union(mpc);
        //切面增强类
        AnnotationPointcutAdvisor advisor = new AnnotationPointcutAdvisor(customizers.orderedStream().findFirst().get(), pointcut);
        //切面优先级顺序
        advisor.setOrder(AopOrderInfo.TRACING);
        return advisor;
    }

    @Bean
    @ConditionalOnMissingBean
    public TracingCustomizer tracingCustomizer() {
        return new DefaultTracingMethodInterceptor(taskExecutor);
    }

    @Override
    public void destroy() {
        logger.info("<== 【销毁--自动化配置】----全链路日志追踪组件【TracingAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        logger.info("==> 【初始化--自动化配置】----全链路日志追踪组件【TracingAutoConfiguration】");
    }
}
