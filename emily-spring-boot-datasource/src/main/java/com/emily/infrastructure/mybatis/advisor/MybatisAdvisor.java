package com.emily.infrastructure.mybatis.advisor;

import com.emily.infrastructure.mybatis.interceptor.MybatisMethodInterceptor;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @program: spring-parent
 * @description: Mybatis切面
 * @author: Emily
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

    }
}
