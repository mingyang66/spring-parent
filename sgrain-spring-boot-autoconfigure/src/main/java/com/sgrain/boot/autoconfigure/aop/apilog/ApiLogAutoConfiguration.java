package com.sgrain.boot.autoconfigure.aop.apilog;

import com.sgrain.boot.autoconfigure.aop.advice.ApiLogMethodInterceptor;
import com.sgrain.boot.autoconfigure.aop.advice.ApiLogThrowsAdvice;
import com.sgrain.boot.autoconfigure.aop.apilog.service.AsyncLogAopService;
import com.sgrain.boot.autoconfigure.aop.apilog.service.impl.AsyncLogAopServiceImpl;
import com.sgrain.boot.common.enums.AopOrderEnum;
import com.sgrain.boot.common.utils.LoggerUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Description: 控制器切点配置
 * @Version: 1.0
 */
@Configuration(proxyBeanMethods = false)
@Import(value = AsyncLogAopServiceImpl.class)
@EnableConfigurationProperties(ApiLogProperties.class)
@ConditionalOnProperty(prefix = "spring.sgrain.api-log", name = "enable", havingValue = "true", matchIfMissing = true)
public class ApiLogAutoConfiguration implements CommandLineRunner {

    public static final String API_LOG_NORMAL_BEAN_NAME = "apiLogNormalPointCutAdvice";
    public static final String API_LOG_EXCEPTION_BEAN_NAME = "apiLogExceptionPointCutAdvice";
    /**
     * 在多个表达式之间使用  || , or 表示  或 ，使用  && , and 表示  与 ， ！ 表示 非
     */
    private static final String DEFAULT_POINT_CUT = StringUtils.join("@annotation(org.springframework.web.bind.annotation.GetMapping) ",
            "or @annotation(org.springframework.web.bind.annotation.PostMapping) ",
            "or @annotation(org.springframework.web.bind.annotation.PutMapping) ",
            "or @annotation(org.springframework.web.bind.annotation.DeleteMapping) ",
            "or @annotation(org.springframework.web.bind.annotation.RequestMapping) ");

    private ApiLogProperties apiLogProperties;

    public ApiLogAutoConfiguration(ApiLogProperties apiLogProperties) {
        this.apiLogProperties = apiLogProperties;
    }

    /**
     * @Description 定义接口拦截器切点
     * @Version 1.0
     */
    @Bean(API_LOG_NORMAL_BEAN_NAME)
    @ConditionalOnClass(ApiLogMethodInterceptor.class)
    public DefaultPointcutAdvisor apiLogNormalPointCutAdvice(AsyncLogAopService asyncLogAopService) {
        //声明一个AspectJ切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //设置需要拦截的切点-用切点语言表达式
        pointcut.setExpression(DEFAULT_POINT_CUT);
        // 配置增强类advisor, 切面=切点+增强
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        //设置切点
        advisor.setPointcut(pointcut);
        //设置增强（Advice）
        advisor.setAdvice(new ApiLogMethodInterceptor(asyncLogAopService));
        //设置增强拦截器执行顺序
        advisor.setOrder(AopOrderEnum.API_LOG_NORMAL.getOrder());
        return advisor;
    }

    /**
     * API异常日志处理增强
     *
     * @return
     */
    @Bean(API_LOG_EXCEPTION_BEAN_NAME)
    @ConditionalOnClass(ApiLogThrowsAdvice.class)
    public DefaultPointcutAdvisor apiLogExceptionPointCutAdvice(AsyncLogAopService asyncLogAopService) {
        //声明一个AspectJ切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //设置需要拦截的切点-用切点语言表达式
        pointcut.setExpression(DEFAULT_POINT_CUT);
        // 配置增强类advisor, 切面=切点+增强
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        //设置切点
        advisor.setPointcut(pointcut);
        //设置增强（Advice）
        advisor.setAdvice(new ApiLogThrowsAdvice(asyncLogAopService));
        //设置增强拦截器执行顺序
        advisor.setOrder(AopOrderEnum.API_LOG_EXCEPTION.getOrder());
        return advisor;
    }

    @Override
    public void run(String... args) throws Exception {
        LoggerUtils.setDebug(apiLogProperties.isDebug());
        LoggerUtils.info(ApiLogAutoConfiguration.class, "【自动化配置】----API日志记录组件初始化完成...");
    }
}
