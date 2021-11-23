package com.emily.infrastructure.mybatis;

import com.emily.infrastructure.common.enums.AopOrder;
import com.emily.infrastructure.mybatis.interceptor.MybatisMethodInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: 控制器切点配置
 * @Author Emily
 * @Version: 1.0
 */
@Configuration
@EnableConfigurationProperties(MybatisProperties.class)
@ConditionalOnProperty(prefix = MybatisProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class MybatisAutoConfiguration {

    public static final String DATA_SOURCE_BEAN_NAME = "mybatisPointcutAdvisor";
    /**
     * 在多个表达式之间使用  || , or 表示  或 ，使用  && , and 表示  与 ， ！ 表示 非
     */
    private static final String DEFAULT_POINT_CUT = StringUtils.join("@within(org.apache.ibatis.annotations.Mapper) ",
            "or @annotation(org.apache.ibatis.annotations.Mapper)");

    /**
     * 方法切入点函数：execution(<修饰符模式>? <返回类型模式> <方法名模式>(<参数模式>) <异常模式>?)  除了返回类型模式、方法名模式和参数模式外，其它项都是可选的
     * 切入点表达式：
     * 第一个*号：表示返回类型，*号表示所有的类型
     * 包名：表示需要拦截的包名，后面的两个句点表示当前包和当前包下的所有子包
     * 第二个*号：表示类名，*号表示所有的类名
     * 第三个*号：表示方法名，*号表示所有的方法，后面的括弧表示方法里面的参数，两个句点表示任意参数
     */
    @Bean(DATA_SOURCE_BEAN_NAME)
    public DefaultPointcutAdvisor mybatisPointcutAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //获取切面表达式
        pointcut.setExpression(DEFAULT_POINT_CUT);
        // 配置增强类advisor
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(new MybatisMethodInterceptor());
        advisor.setOrder(AopOrder.MYBATIS_AOP);
        return advisor;
    }

}
