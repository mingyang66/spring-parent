package com.sgrain.boot.autoconfigure.aop.ratelimit;

import com.sgrain.boot.autoconfigure.aop.interceptor.RateLimitMethodInterceptor;
import com.sgrain.boot.autoconfigure.aop.log.LogAopAutoConfiguration;
import com.sgrain.boot.common.enums.AopOrderEnum;
import com.sgrain.boot.common.utils.LoggerUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * @program: spring-parent
 * @description: 接口被指定的客户端调用频率限制自动化配置
 * @create: 2020/03/23
 */
@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
@ConditionalOnProperty(prefix = "spring.sgrain.rate-limit", name = "enable", havingValue = "true", matchIfMissing = false)
public class RateLimitAutoConfiguration implements CommandLineRunner {
    /**
     * 在多个表达式之间使用  || , or 表示  或 ，使用  && , and 表示  与 ， ！ 表示 非
     */
    private static final String RATE_LIMIT_POINT_CUT = StringUtils.join("@annotation(com.sgrain.boot.autoconfigure.aop.annotation.RateLimit)");

    /**
     * 控制器AOP拦截处理
     */
    @Bean
    public DefaultPointcutAdvisor rateLimitPointCutAdvice(RedisTemplate redisTemplate) {
        //声明一个AspectJ切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //设置切点表达式
        pointcut.setExpression(RATE_LIMIT_POINT_CUT);
        // 配置增强类advisor, 切面=切点+增强
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        //设置切点
        advisor.setPointcut(pointcut);
        //设置增强（Advice）
        advisor.setAdvice(new RateLimitMethodInterceptor(redisTemplate));
        //设置增强拦截器执行顺序
        advisor.setOrder(AopOrderEnum.RATE_LIMITER.getOrder());

        return advisor;
    }

    @Override
    public void run(String... args) throws Exception {
        LoggerUtils.info(RateLimitAutoConfiguration.class, "自动化配置----限流组件初始化完成...");
    }
}
