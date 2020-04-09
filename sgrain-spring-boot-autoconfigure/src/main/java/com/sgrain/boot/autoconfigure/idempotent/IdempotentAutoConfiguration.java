package com.sgrain.boot.autoconfigure.idempotent;

import com.sgrain.boot.autoconfigure.aop.interceptor.IdempotentMethodInterceptor;
import com.sgrain.boot.common.enums.AopOrderEnum;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfiguration;
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
@EnableConfigurationProperties(IdempotentProperties.class)
@ConditionalOnClass(value = {RedissonAutoConfiguration.class, RedisTemplate.class, RedissonClient.class})
@ConditionalOnProperty(prefix = "spring.sgrain.idempotent", name = "enable", havingValue = "true", matchIfMissing = true)
public class IdempotentAutoConfiguration {
    /**
     * 在多个表达式之间使用  || , or 表示  或 ，使用  && , and 表示  与 ， ！ 表示 非
     */
    private static final String REPEAT_SUBMIT_POINT_CUT = StringUtils.join("@annotation(com.sgrain.boot.autoconfigure.aop.annotation.Idempotent) ");

    /**
     * 控制器AOP拦截处理
     */
    @Bean
    @ConditionalOnClass(value = {IdempotentMethodInterceptor.class})
    public DefaultPointcutAdvisor repeatSubmitPointCutAdvice(RedisTemplate redisTemplate, RedissonClient redissonClient) {
        //声明一个AspectJ切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //设置切点表达式
        pointcut.setExpression(REPEAT_SUBMIT_POINT_CUT);
        // 配置增强类advisor, 切面=切点+增强
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        //设置切点
        advisor.setPointcut(pointcut);
        //设置增强（Advice）
        advisor.setAdvice(new IdempotentMethodInterceptor(redissonClient, redisTemplate, delLuaScript()));
        //设置增强拦截器执行顺序
        advisor.setOrder(AopOrderEnum.IDEMPOTENT.getOrder());

        return advisor;
    }
    /**
     * 加载lua脚本
     */
    @Bean
    public DefaultRedisScript<Long> delLuaScript(){
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("del.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
