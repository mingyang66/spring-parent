package com.yaomy.sgrain.aop.config;

import com.yaomy.sgrain.aop.advice.LogAopMethodInterceptor;
import com.yaomy.sgrain.aop.properties.LogAopProperties;
import com.yaomy.sgrain.common.enums.SgrainAopOrderEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: 控制器切点配置
 * @Version: 1.0
 */
@Configuration
@EnableConfigurationProperties(LogAopProperties.class)
@ConditionalOnProperty(prefix = "spring.sgrain.log-aop", name = "enable", havingValue = "true", matchIfMissing = true)
public class LogAopAutoConfiguration {

    /**
     * 在多个表达式之间使用  || , or 表示  或 ，使用  && , and 表示  与 ， ！ 表示 非
     */
    private static final String DEFAULT_POINT_CUT = StringUtils.join("@annotation(org.springframework.web.bind.annotation.GetMapping) ",
                                                                            "or @annotation(org.springframework.web.bind.annotation.PostMapping) ",
                                                                            "or @annotation(org.springframework.web.bind.annotation.RequestMapping) ");
    private LogAopProperties properties;

    public LogAopAutoConfiguration(LogAopProperties properties){
        this.properties = properties;
    }
    /**
     * @Description 定义接口拦截器切点
     * @Version  1.0
     */
    @Bean
    @ConditionalOnClass(LogAopMethodInterceptor.class)
    public DefaultPointcutAdvisor logAopPointCutAdvice() {
        //声明一个AspectJ切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //设置切点表达式
        pointcut.setExpression(DEFAULT_POINT_CUT);
        // 配置增强类advisor, 切面=切点+增强
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        //设置切点
        advisor.setPointcut(pointcut);
        //设置增强（Advice）
        advisor.setAdvice(new LogAopMethodInterceptor(properties));
        //设置增强拦截器执行顺序
        advisor.setOrder(SgrainAopOrderEnum.CONTROLLER_ADVICE.getOrder());
        return advisor;
    }
}
