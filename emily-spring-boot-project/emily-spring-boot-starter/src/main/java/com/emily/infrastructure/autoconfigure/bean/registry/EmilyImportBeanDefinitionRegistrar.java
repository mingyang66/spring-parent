package com.emily.infrastructure.autoconfigure.bean.registry;

import com.emily.infrastructure.logback.factory.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Emily
 * @since 2020/09/27
 */
public class EmilyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    private static final Logger logger = LoggerFactory.getLogger(EmilyImportBeanDefinitionRegistrar.class);

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        registerBeanDefinitions(importingClassMetadata, registry);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        logger.info("--------ImportBeanDefinitionRegistrar-------");
    }
}
