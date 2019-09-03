package com.yaomy.control.aop.config;

import com.yaomy.control.aop.advice.ControllerAdviceInterceptor;
import com.yaomy.control.common.control.conf.PropertyService;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: 控制器切点配置
 * @Version: 1.0
 */
@Configuration
public class ControllerConfig {
    @Autowired
    private ControllerAdviceInterceptor interceptor;
    /**
     * 在多个表达式之间使用  || , or 表示  或 ，使用  && , and 表示  与 ， ！ 表示 非
     */
    private static final String DEFAULT_POINT_CUT = "@annotation(org.springframework.web.bind.annotation.GetMapping) or @annotation(org.springframework.web.bind.annotation.PostMapping) or @annotation(org.springframework.web.bind.annotation.RequestMapping)";

    /**
     * @Description 定义接口拦截器切点
     * @Version  1.0
     */
    @Bean
    public DefaultPointcutAdvisor defaultPointCutAdvice() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //设置切点表达式
        pointcut.setExpression(DEFAULT_POINT_CUT);

        // 配置增强类advisor
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(interceptor);
        return advisor;
    }
}
