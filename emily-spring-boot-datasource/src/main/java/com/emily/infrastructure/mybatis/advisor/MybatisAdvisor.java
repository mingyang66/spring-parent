package com.emily.infrastructure.mybatis.advisor;

import com.emily.infrastructure.mybatis.interceptor.MybatisMethodInterceptor;
import org.aopalliance.aop.Advice;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @program: spring-parent
 * @description:
 * @author: 姚明洋
 * @create: 2022/01/12
 */
public class MybatisAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    private final Advice advice;

    private Pointcut pointcut;

    public MybatisAdvisor(MybatisMethodInterceptor interceptor) {
        this.advice = interceptor;
        this.pointcut = getPointcut();
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    public void setPointcut(Pointcut pointcut) {
        this.pointcut = pointcut;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println(beanFactory);
    }
}
