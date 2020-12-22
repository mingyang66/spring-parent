package com.emily.framework.autoconfigure.idempotent;

import com.emily.framework.autoconfigure.idempotent.interceptor.IdempotentMethodBeforeAdvice;
import com.emily.framework.common.enums.AopOrderEnum;
import com.emily.framework.common.utils.log.LoggerUtils;
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
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @program: spring-parent
 * @description: 防止接口重复提交自动化配置
 * @create: 2020/03/23
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(StringRedisTemplate.class)
@EnableConfigurationProperties(IdempotentProperties.class)
@ConditionalOnProperty(prefix = "spring.emily.idempotent", name = "enable", havingValue = "true", matchIfMissing = false)
public class IdempotentAutoConfiguration implements InitializingBean, DisposableBean {
    /**
     * 在多个表达式之间使用  || , or 表示  或 ，使用  && , and 表示  与 ， ！ 表示 非
     */
    private static final String REPEAT_SUBMIT_POINT_CUT = StringUtils.join("@annotation(com.emily.framework.autoconfigure.idempotent.annotation.ApiIdempotent)");


    /**
     * 控制器AOP拦截处理
     */
    @Bean
    public DefaultPointcutAdvisor repeatSubmitPointCutAdvice(StringRedisTemplate stringRedisTemplate) {
        //声明一个AspectJ切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //设置切点表达式
        pointcut.setExpression(REPEAT_SUBMIT_POINT_CUT);
        // 配置增强类advisor, 切面=切点+增强
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        //设置切点
        advisor.setPointcut(pointcut);
        //设置增强（Advice）
        advisor.setAdvice(new IdempotentMethodBeforeAdvice(stringRedisTemplate));
        //设置增强拦截器执行顺序
        advisor.setOrder(AopOrderEnum.IDEMPOTENT.getOrder());

        return advisor;
    }

    @Override
    public void destroy() throws Exception {
        LoggerUtils.info(IdempotentAutoConfiguration.class, "【销毁--自动化配置】----防止接口重复提交组件【IdempotentAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(IdempotentAutoConfiguration.class, "【初始化--自动化配置】----防止接口重复提交组件【IdempotentAutoConfiguration】");
    }
}
