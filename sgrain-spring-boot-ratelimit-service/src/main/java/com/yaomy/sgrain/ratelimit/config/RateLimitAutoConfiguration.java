package com.yaomy.sgrain.ratelimit.config;

import com.yaomy.sgrain.common.enums.AopOrderEnum;
import com.yaomy.sgrain.ratelimit.interceptor.RateLimitMethodInterceptor;
import com.yaomy.sgrain.ratelimit.properties.RateLimitProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @program: spring-parent
 * @description: 接口被指定的客户端调用频率限制自动化配置
 * @create: 2020/03/23
 */
@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
@ConditionalOnClass(RedisTemplate.class)
@ConditionalOnProperty(prefix = "spring.sgrain.rate-limit", name = "enable", havingValue = "true", matchIfMissing = true)
public class RateLimitAutoConfiguration {
    /**
     * 在多个表达式之间使用  || , or 表示  或 ，使用  && , and 表示  与 ， ！ 表示 非
     */
    private static final String TEST_POINT_CUT = StringUtils.join("@annotation(com.yaomy.sgrain.ratelimit.annotation.RateLimit) ");

    /**
     * 控制器AOP拦截处理
     */
    @Bean
    @ConditionalOnClass(value = {RateLimitMethodInterceptor.class})
    public DefaultPointcutAdvisor rateLimitPointCutAdvice(RedisTemplate redisTemplate) {
        //声明一个AspectJ切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //设置切点表达式
        pointcut.setExpression(TEST_POINT_CUT);
        // 配置增强类advisor, 切面=切点+增强
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        //设置切点
        advisor.setPointcut(pointcut);
        //设置增强（Advice）
        advisor.setAdvice(new RateLimitMethodInterceptor(redisTemplate, redisLuaScript()));
        //设置增强拦截器执行顺序
        advisor.setOrder(AopOrderEnum.RATE_LIMITER.getOrder());

        return advisor;
    }

    /**
     * 加载lua脚本
     */
    @Bean
    public DefaultRedisScript<Long> redisLuaScript(){
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("limit.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
