package com.sgrain.boot.autoconfigure.bean.registry;

import com.sgrain.boot.common.utils.log.LoggerUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/09/27
 */
public class SmallGrainImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        registerBeanDefinitions(importingClassMetadata, registry);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        LoggerUtils.info(SmallGrainImportBeanDefinitionRegistrar.class, "--------ImportBeanDefinitionRegistrar-------");
    }
}
