package com.emily.infrastructure.cloud.feign;

import com.emily.infrastructure.cloud.feign.interceptor.FeignLoggerMethodInterceptor;
import com.emily.infrastructure.cloud.feign.interceptor.FeignLoggerThrowsAdvice;
import com.emily.infrastructure.cloud.feign.interceptor.FeignRequestInterceptor;
import com.emily.infrastructure.cloud.feign.loadbalancer.FeignLoggerLoadBalancerLifecycle;
import com.emily.infrastructure.cloud.feign.logger.FeignLogger;
import com.emily.infrastructure.common.enums.AopOrderEnum;
import com.emily.infrastructure.logback.common.LoggerUtils;
import com.emily.infrastructure.context.logger.LoggerService;
import com.emily.infrastructure.context.logger.impl.LoggerServiceImpl;
import feign.Logger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.function.Supplier;

/**
 * @author Emily
 * @Description: 控制器切点配置
 * @Version: 1.0
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FeignLoggerProperties.class)
@ConditionalOnProperty(prefix = "spring.emily.feign.logger", name = "enabled", havingValue = "true", matchIfMissing = false)
@Import(LoggerServiceImpl.class)
public class FeignLoggerAutoConfiguration implements InitializingBean, DisposableBean {

    public static final String FEIGN_LOGGER_NORMAL_BEAN_NAME = "feignLoggerNormalPointCutAdvice";
    public static final String FEIGN_LOGGER_EXCEPTION_BEAN_NAME = "feignLoggerExceptionPointCutAdvice";
    /**
     * 在多个表达式之间使用  || , or 表示  或 ，使用  && , and 表示  与 ， ！ 表示 非
     */
    private static final String DEFAULT_POINT_CUT = StringUtils.join("(@target(org.springframework.cloud.openfeign.FeignClient)) ",
            "and (@annotation(org.springframework.web.bind.annotation.GetMapping) ",
            "or @annotation(org.springframework.web.bind.annotation.PostMapping) ",
            "or @annotation(org.springframework.web.bind.annotation.PutMapping) ",
            "or @annotation(org.springframework.web.bind.annotation.DeleteMapping) ",
            "or @annotation(org.springframework.web.bind.annotation.RequestMapping))");


    /**
     * @Description 定义接口拦截器切点
     * @Version 1.0
     */
    @Bean(FEIGN_LOGGER_NORMAL_BEAN_NAME)
    @ConditionalOnClass(FeignLoggerMethodInterceptor.class)
    public DefaultPointcutAdvisor apiLogNormalPointCutAdvice(LoggerService loggerService) {
        //声明一个AspectJ切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //设置需要拦截的切点-用切点语言表达式
        pointcut.setExpression(DEFAULT_POINT_CUT);
        // 配置增强类advisor, 切面=切点+增强
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        //设置切点
        advisor.setPointcut(pointcut);
        //设置增强（Advice）
        advisor.setAdvice(new FeignLoggerMethodInterceptor(loggerService));
        //设置增强拦截器执行顺序
        advisor.setOrder(AopOrderEnum.FEIGN_LOG_NORMAL.getOrder());
        return advisor;
    }

    /**
     * API异常日志处理增强
     *
     * @return
     */
    @Bean(FEIGN_LOGGER_EXCEPTION_BEAN_NAME)
    @ConditionalOnClass(FeignLoggerThrowsAdvice.class)
    public DefaultPointcutAdvisor apiLogExceptionPointCutAdvice(LoggerService loggerService) {
        //声明一个AspectJ切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //设置需要拦截的切点-用切点语言表达式
        pointcut.setExpression(DEFAULT_POINT_CUT);
        // 配置增强类advisor, 切面=切点+增强
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        //设置切点
        advisor.setPointcut(pointcut);
        //设置增强（Advice）
        advisor.setAdvice(new FeignLoggerThrowsAdvice(loggerService));
        //设置增强拦截器执行顺序
        advisor.setOrder(AopOrderEnum.FEIGN_LOG_EXCEPTION.getOrder());
        return advisor;
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
    public void destroy() throws Exception {
        LoggerUtils.info(FeignLoggerAutoConfiguration.class, "【销毁--自动化配置】----Feign日志记录组件【FeignLoggerAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(FeignLoggerAutoConfiguration.class, "【初始化--自动化配置】----Feign日志记录组件【FeignLoggerAutoConfiguration】");
    }
}
