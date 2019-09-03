package com.yaomy.control.aop.config;

import com.yaomy.control.aop.advice.ControllerAdviceInterceptor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @Description: 控制器切点配置
 * @Version: 1.0
 */
@Configuration
public class ControllerConfig {
    @Autowired
    private Environment env;
    @Autowired
    private ControllerAdviceInterceptor interceptor;

    private static final String POINT_CUT = "spring.aop.control.expression";

    private static final String DEFAULT_POINT_CUT = "execution(public * com.yaomy.control.test.api..*.*(..))";

    /**
     * @Description 定义接口拦截器切点
     * @Version  1.0
     */
    @Bean
    public DefaultPointcutAdvisor defaultPointCutAdvice() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(env.getProperty(POINT_CUT, DEFAULT_POINT_CUT));

        // 配置增强类advisor
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(interceptor);
        return advisor;
    }
}
