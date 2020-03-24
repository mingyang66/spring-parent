package com.yaomy.sgrain.ratelimiter.config;

import com.yaomy.sgrain.common.enums.AopOrderEnum;
import com.yaomy.sgrain.ratelimiter.interceptor.RateLimiterMethodInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: spring-parent
 * @description: 接口被指定的客户端调用频率限制自动化配置
 * @author: 姚明洋
 * @create: 2020/03/23
 */
@Configuration
public class RateLimiterAutoConfiguration {
    /**
     * 在多个表达式之间使用  || , or 表示  或 ，使用  && , and 表示  与 ， ！ 表示 非
     */
    private static final String TEST_POINT_CUT = StringUtils.join("@annotation(com.yaomy.sgrain.ratelimiter.annotation.RateLimiter) ");

    /**
     * 控制器AOP拦截处理
     */
    @Bean
    @ConditionalOnClass(RateLimiterMethodInterceptor.class)
    public DefaultPointcutAdvisor testPointCutAdvice() {
        //声明一个AspectJ切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //设置切点表达式
        pointcut.setExpression(TEST_POINT_CUT);
        // 配置增强类advisor, 切面=切点+增强
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        //设置切点
        advisor.setPointcut(pointcut);
        //设置增强（Advice）
        advisor.setAdvice(new RateLimiterMethodInterceptor());
        //设置增强拦截器执行顺序
        advisor.setOrder(AopOrderEnum.RATE_LIMITER.getOrder());

        return advisor;
    }
}
