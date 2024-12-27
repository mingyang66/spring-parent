package com.emily.infrastructure.mybatis;

import com.emily.infrastructure.aop.advisor.AnnotationPointcutAdvisor;
import com.emily.infrastructure.aop.constant.AopOrderInfo;
import com.emily.infrastructure.logback.factory.LoggerFactory;
import com.emily.infrastructure.mybatis.interceptor.DefaultMybatisMethodInterceptor;
import com.emily.infrastructure.mybatis.interceptor.MybatisCustomizer;
import org.aopalliance.intercept.MethodInterceptor;
import org.apache.ibatis.annotations.Mapper;
import org.slf4j.Logger;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

/**
 * 控制器切点配置
 *
 * @author Emily
 * @since : 1.0
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration
@EnableConfigurationProperties(MybatisProperties.class)
@ConditionalOnProperty(prefix = MybatisProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class MybatisAutoConfiguration implements BeanFactoryPostProcessor, InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(MybatisAutoConfiguration.class);

    /**
     * Mybatis请求日志拦截切面增强类
     * checkInherited:是否验证父类或接口集成的注解，如果注解用@Inherited标注则自动集成
     *
     * @param mybatisCustomizers 扩展点
     * @param properties         属性配置
     * @return 组合切面增强类
     * @since 4.0.5
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor mybatisLogAdvisor(ObjectProvider<MybatisCustomizer> mybatisCustomizers, final MybatisProperties properties) {
        //限定类级别的切点
        Pointcut cpc = new AnnotationMatchingPointcut(Mapper.class, properties.isCheckInherited());
        //限定方法级别的切点
        Pointcut mpc = new AnnotationMatchingPointcut(null, Mapper.class, properties.isCheckInherited());
        //组合切面(并集)，即只要有一个切点的条件符合，则就拦截
        Pointcut pointcut = new ComposablePointcut(cpc).union(mpc);
        //mybatis日志拦截切面
        MethodInterceptor interceptor = mybatisCustomizers.orderedStream().findFirst().get();
        //切面增强类
        AnnotationPointcutAdvisor advisor = new AnnotationPointcutAdvisor(interceptor, pointcut);
        //切面优先级顺序
        advisor.setOrder(AopOrderInfo.MYBATIS);
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean
    public MybatisCustomizer mybatisCustomizer(ApplicationContext context) {
        return new DefaultMybatisMethodInterceptor(context);
    }

    /**
     * 将指定的bean 角色标记为基础设施类型，相关提示类在 org.springframework.context.support.PostProcessorRegistrationDelegate
     *
     * @param beanFactory 工厂类
     * @throws BeansException 抛出异常
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanNames = beanFactory.getBeanNamesForType(MybatisProperties.class);
        if (beanNames.length > 0 && beanFactory.containsBeanDefinition(beanNames[0])) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanNames[0]);
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
    }

    @Override
    public void destroy() throws Exception {
        logger.info("<== 【销毁--自动化配置】----Mybatis日志拦截组件【MybatisAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("==> 【初始化--自动化配置】----Mybatis日志拦截组件【MybatisAutoConfiguration】");
    }
}
