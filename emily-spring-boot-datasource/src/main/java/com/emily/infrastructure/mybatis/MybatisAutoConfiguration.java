package com.emily.infrastructure.mybatis;

import com.emily.infrastructure.common.constant.AopOrderInfo;
import com.emily.infrastructure.mybatis.advisor.MybatisAdvisor;
import com.emily.infrastructure.mybatis.interceptor.MybatisMethodInterceptor;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
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

    /**
     * Mybatis请求日志拦截切面
     *
     * @return
     */
    @Bean
    public Advisor mybatisLogAdvisor(MybatisProperties properties) {
        MybatisAdvisor advisor = new MybatisAdvisor(new MybatisMethodInterceptor());
        advisor.setPointcut(new AnnotationMatchingPointcut(Mapper.class, properties.isCheckInherited()));
        advisor.setOrder(AopOrderInfo.MYBATIS_AOP);
        return advisor;
    }

}
