package com.emily.framework.cloud.feign;

import com.emily.framework.cloud.feign.interceptor.FeignLogMethodInterceptor;
import com.emily.framework.cloud.feign.interceptor.FeignLogThrowsAdvice;
import com.emily.framework.cloud.feign.interceptor.FeignRequestInterceptor;
import com.emily.framework.cloud.feign.loadbalancer.FeignLogLoadBalancerLifecycle;
import com.emily.framework.common.enums.AopOrderEnum;
import com.emily.framework.common.utils.log.LoggerUtils;
import com.emily.framework.context.apilog.service.AsyncLogAopService;
import com.emily.framework.context.apilog.service.impl.AsyncLogAopServiceImpl;
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

/**
 * @Description: 控制器切点配置
 * @Version: 1.0
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FeignLogProperties.class)
@ConditionalOnProperty(prefix = "spring.emily.feign.http-log", name = "enable", havingValue = "true", matchIfMissing = true)
@Import(AsyncLogAopServiceImpl.class)
public class FeignLogAutoConfiguration implements InitializingBean, DisposableBean {

    public static final String HTTP_LOG_NORMAL_BEAN_NAME = "feignLogNormalPointCutAdvice";
    public static final String HTTP_LOG_EXCEPTION_BEAN_NAME = "feignLogExceptionPointCutAdvice";
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
    @Bean(HTTP_LOG_NORMAL_BEAN_NAME)
    @ConditionalOnClass(FeignLogMethodInterceptor.class)
    public DefaultPointcutAdvisor apiLogNormalPointCutAdvice(AsyncLogAopService asyncLogAopService) {
        //声明一个AspectJ切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //设置需要拦截的切点-用切点语言表达式
        pointcut.setExpression(DEFAULT_POINT_CUT);
        // 配置增强类advisor, 切面=切点+增强
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        //设置切点
        advisor.setPointcut(pointcut);
        //设置增强（Advice）
        advisor.setAdvice(new FeignLogMethodInterceptor(asyncLogAopService));
        //设置增强拦截器执行顺序
        advisor.setOrder(AopOrderEnum.FEIGN_LOG_NORMAL.getOrder());
        return advisor;
    }

    /**
     * API异常日志处理增强
     *
     * @return
     */
    @Bean(HTTP_LOG_EXCEPTION_BEAN_NAME)
    @ConditionalOnClass(FeignLogThrowsAdvice.class)
    public DefaultPointcutAdvisor apiLogExceptionPointCutAdvice(AsyncLogAopService asyncLogAopService) {
        //声明一个AspectJ切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //设置需要拦截的切点-用切点语言表达式
        pointcut.setExpression(DEFAULT_POINT_CUT);
        // 配置增强类advisor, 切面=切点+增强
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        //设置切点
        advisor.setPointcut(pointcut);
        //设置增强（Advice）
        advisor.setAdvice(new FeignLogThrowsAdvice(asyncLogAopService));
        //设置增强拦截器执行顺序
        advisor.setOrder(AopOrderEnum.FEIGN_LOG_EXCEPTION.getOrder());
        return advisor;
    }

    @Bean
    public FeignRequestInterceptor feignRequestInterceptor() {
        return new FeignRequestInterceptor();
    }

    @Bean
    public FeignLogLoadBalancerLifecycle feignLogLoadBalancerLifecycle() {
        return new FeignLogLoadBalancerLifecycle();
    }

    @Override
    public void destroy() throws Exception {
        LoggerUtils.info(FeignLogAutoConfiguration.class, "【销毁--自动化配置】----Feign日志记录组件【HttpLogAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(FeignLogAutoConfiguration.class, "【初始化--自动化配置】----Feign日志记录组件【HttpLogAutoConfiguration】");
    }
}
