package com.emily.infrastructure.core.aop.pointcut;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;

/**
 * @program: spring-parent
 * @description: 针对方法切点
 * @author: Emily
 * @create: 2022/01/14
 */
public class AnnotationMethodPointcut implements Pointcut {
    /**
     * 注释类型
     */
    private final Class<? extends Annotation> annotationType;
    /**
     * 是否还要检查注释类型标注的父类和接口
     */
    private final boolean checkInherited;

    /**
     * 基于注释的切点构造函数
     *
     * @param annotationType 注释类型
     */
    public AnnotationMethodPointcut(Class<? extends Annotation> annotationType) {
        this(annotationType, false);
    }

    /**
     * 基于注释的切点构造函数
     *
     * @param annotationType 注释类型
     * @param checkInherited 是否还要检查注释类型标注的父类和接口
     */
    public AnnotationMethodPointcut(Class<? extends Annotation> annotationType, boolean checkInherited) {
        Assert.notNull(annotationType, "Annotation type must not be null");
        this.annotationType = annotationType;
        this.checkInherited = checkInherited;
    }

    /**
     * 获取限制切入点或引入点与给定目标类集合匹配的筛选器
     *
     * @return 返回匹配所有类|接口的类筛选器的规范实例
     */
    @Override
    public ClassFilter getClassFilter() {
        return ClassFilter.TRUE;
    }

    /**
     * 属于Pointcut切点的一部分，判定目标方法是否符合判定条件
     *
     * @return
     */
    @Override
    public MethodMatcher getMethodMatcher() {
        return new AnnotationMethodMatcher(annotationType, checkInherited);
    }

}
