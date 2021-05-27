package com.emily.infrastructure.autoconfigure.bean.registry;

import com.emily.infrastructure.logback.common.LoggerUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Emily
 * @program: spring-parent
 * @description:
 * @create: 2020/09/27
 */
public class EmilyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        registerBeanDefinitions(importingClassMetadata, registry);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        LoggerUtils.info(EmilyImportBeanDefinitionRegistrar.class, "--------ImportBeanDefinitionRegistrar-------");
    }
}
