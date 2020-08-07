package com.sgrain.boot.autoconfigure.aop.log;

import com.sgrain.boot.autoconfigure.aop.interceptor.LogAopMethodInterceptor;
import com.sgrain.boot.common.enums.AopOrderEnum;
import com.sgrain.boot.common.utils.LoggerUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: 控制器切点配置
 * @Version: 1.0
 */
@Configuration
@EnableConfigurationProperties(LogAopProperties.class)
@ConditionalOnProperty(prefix = "spring.sgrain.log", name = "enable", havingValue = "true", matchIfMissing = true)
public class LogAopAutoConfiguration implements InitializingBean {

    /**
     * 在多个表达式之间使用  || , or 表示  或 ，使用  && , and 表示  与 ， ！ 表示 非
     */
    private static final String DEFAULT_POINT_CUT = StringUtils.join("@annotation(org.springframework.web.bind.annotation.GetMapping) ",
            "or @annotation(org.springframework.web.bind.annotation.PostMapping) ",
            "or @annotation(org.springframework.web.bind.annotation.PutMapping) ",
            "or @annotation(org.springframework.web.bind.annotation.DeleteMapping) ",
            "or @annotation(org.springframework.web.bind.annotation.RequestMapping) ");
    @Autowired
    private LogAopProperties logAopProperties;
    @Autowired
    private ApplicationEventPublisher publisher;

    /**
     * @Description 定义接口拦截器切点
     * @Version 1.0
     */
    @Bean
    @ConditionalOnClass(LogAopMethodInterceptor.class)
    public DefaultPointcutAdvisor logAopPointCutAdvice() {
        //声明一个AspectJ切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //设置需要拦截的切点-用切点语言表达式
        pointcut.setExpression(DEFAULT_POINT_CUT);
        // 配置增强类advisor, 切面=切点+增强
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        //设置切点
        advisor.setPointcut(pointcut);
        //设置增强（Advice）
        advisor.setAdvice(new LogAopMethodInterceptor(publisher));
        //设置增强拦截器执行顺序
        advisor.setOrder(AopOrderEnum.LOG_AOP.getOrder());
        return advisor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.setDebug(logAopProperties.isDebug());
    }
}
