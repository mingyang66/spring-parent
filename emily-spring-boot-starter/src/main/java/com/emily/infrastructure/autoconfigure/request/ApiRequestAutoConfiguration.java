package com.emily.infrastructure.autoconfigure.request;

import com.emily.infrastructure.autoconfigure.request.interceptor.ApiRequestCustomizer;
import com.emily.infrastructure.autoconfigure.request.interceptor.DefaultApiRequestMethodInterceptor;
import com.emily.infrastructure.common.constant.AopOrderInfo;
import com.emily.infrastructure.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @author Emily
 * @Description: 请求日志拦截AOP切面
 * @Version: 1.0
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ApiRequestProperties.class)
@ConditionalOnProperty(prefix = ApiRequestProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class ApiRequestAutoConfiguration implements BeanFactoryPostProcessor, InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(ApiRequestAutoConfiguration.class);

    /**
     * 在多个表达式之间使用  || , or 表示  或 ，使用  && , and 表示  与 ， ！ 表示 非
     *
     * @target()可以标注在目标类对象上，但是不可以标注在接口上
     * @within()可以标注在目标类对象上、也可以标注在接口上
     * @annotation()可以标注在目标方法上
     */
    private static final String DEFAULT_POINT_CUT = StringUtils.join("(@target(org.springframework.stereotype.Controller) ",
            "or @target(org.springframework.web.bind.annotation.RestController)) ",
            "and (@annotation(org.springframework.web.bind.annotation.GetMapping) ",
            "or @annotation(org.springframework.web.bind.annotation.PostMapping) ",
            "or @annotation(org.springframework.web.bind.annotation.PutMapping) ",
            "or @annotation(org.springframework.web.bind.annotation.DeleteMapping) ",
            "or @annotation(org.springframework.web.bind.annotation.RequestMapping))");


    /**
     * @Description 定义接口拦截器切点
     * @Version 1.0
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor apiAdvisor(ObjectProvider<ApiRequestCustomizer> apiRequestCustomizers) {
        //声明一个AspectJ切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //设置需要拦截的切点-用切点语言表达式
        pointcut.setExpression(DEFAULT_POINT_CUT);
        // 配置增强类advisor, 切面=切点+增强
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        //设置切点
        advisor.setPointcut(pointcut);
        //设置增强（Advice）
        advisor.setAdvice(apiRequestCustomizers.orderedStream().findFirst().get());
        //设置增强拦截器执行顺序
        advisor.setOrder(AopOrderInfo.REQUEST);
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public DefaultApiRequestMethodInterceptor apiRequestMethodInterceptor() {
        return new DefaultApiRequestMethodInterceptor();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }

    @Override
    public void destroy() {
        logger.info("<== 【销毁--自动化配置】----RequestLogger日志记录组件【RequestLoggerAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        logger.info("==> 【初始化--自动化配置】----RequestLogger日志记录组件【RequestLoggerAutoConfiguration】");
    }
}
