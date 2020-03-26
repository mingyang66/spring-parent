package com.yaomy.sgrain.submit.config;

import com.yaomy.sgrain.common.enums.SgrainAopOrderEnum;
import com.yaomy.sgrain.submit.interceptor.RepeatSubmitMethodInterceptor;
import com.yaomy.sgrain.submit.properties.RepeatSubmitProperties;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: spring-parent
 * @description: 接口被指定的客户端调用频率限制自动化配置
 * @author: 姚明洋
 * @create: 2020/03/23
 */
@Configuration
@EnableConfigurationProperties(RepeatSubmitProperties.class)
@ConditionalOnProperty(prefix = "spring.sgrain.repeat-submit", name = "enable", havingValue = "true", matchIfMissing = true)
public class RepeatSubmitAutoConfiguration {
    /**
     * 在多个表达式之间使用  || , or 表示  或 ，使用  && , and 表示  与 ， ！ 表示 非
     */
    private static final String REPEAT_SUBMIT_POINT_CUT = StringUtils.join("@annotation(com.yaomy.sgrain.submit.annotation.NoRepeatSubmit) ");

    /**
     * 控制器AOP拦截处理
     */
    @Bean
    @ConditionalOnClass(value = {RepeatSubmitMethodInterceptor.class, RedissonClient.class})
    public DefaultPointcutAdvisor repeatSubmitPointCutAdvice(RedissonClient redissonClient) {
        //声明一个AspectJ切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //设置切点表达式
        pointcut.setExpression(REPEAT_SUBMIT_POINT_CUT);
        // 配置增强类advisor, 切面=切点+增强
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        //设置切点
        advisor.setPointcut(pointcut);
        //设置增强（Advice）
        advisor.setAdvice(new RepeatSubmitMethodInterceptor(redissonClient));
        //设置增强拦截器执行顺序
        advisor.setOrder(SgrainAopOrderEnum.REPEAT_SUBMIT.getOrder());

        return advisor;
    }

}
