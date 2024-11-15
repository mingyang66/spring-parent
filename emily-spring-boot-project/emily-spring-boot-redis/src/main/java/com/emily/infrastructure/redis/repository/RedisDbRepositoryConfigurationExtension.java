package com.emily.infrastructure.redis.repository;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.*;
import org.springframework.data.keyvalue.repository.config.KeyValueRepositoryConfigurationExtension;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.convert.MappingConfiguration;
import org.springframework.data.redis.core.convert.MappingRedisConverter;
import org.springframework.data.redis.core.convert.RedisCustomConversions;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

/**
 * RedisRepositoryConfigurationExtension for Redis
 *
 * @author :  Emily
 * @since :  2024/7/8 上午20:00
 */
public class RedisDbRepositoryConfigurationExtension extends KeyValueRepositoryConfigurationExtension {

    private static final String REDIS_CONVERTER_BEAN_NAME = "redisConverter";
    private static final String REDIS_REFERENCE_RESOLVER_BEAN_NAME = "redisReferenceResolver";
    private static final String REDIS_ADAPTER_BEAN_NAME = "redisKeyValueAdapter";
    private static final String REDIS_CUSTOM_CONVERSIONS_BEAN_NAME = "redisCustomConversions";
    private static final String REDIS_MAPPING_CONFIG_BEAN_NAME = "redisMappingConfiguration";

    private static AbstractBeanDefinition createRedisKeyValueAdapter(RepositoryConfigurationSource configuration) {

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(RedisDbKeyValueAdapter.class) //
                .addConstructorArgReference(configuration.getRequiredAttribute("redisTemplateRef", String.class)) //
                .addConstructorArgReference(REDIS_CONVERTER_BEAN_NAME) //
                .addPropertyValue("enableKeyspaceEvents",
                        configuration.getRequiredAttribute("enableKeyspaceEvents", RedisDbKeyValueAdapter.EnableKeyspaceEvents.class)) //
                .addPropertyValue("keyspaceNotificationsConfigParameter",
                        configuration.getAttribute("keyspaceNotificationsConfigParameter", String.class).orElse("")) //
                .addPropertyValue("shadowCopy", configuration.getRequiredAttribute("shadowCopy", RedisDbKeyValueAdapter.ShadowCopy.class));

        configuration.getAttribute("messageListenerContainerRef")
                .ifPresent(it -> builder.addPropertyReference("messageListenerContainer", it));

        return builder.getBeanDefinition();
    }

    private static AbstractBeanDefinition createRedisReferenceResolverDefinition(String redisTemplateRef) {

        return BeanDefinitionBuilder.rootBeanDefinition("org.springframework.data.redis.core.convert.ReferenceResolverImpl") //
                .addConstructorArgReference(redisTemplateRef) //
                .getBeanDefinition();
    }

    private static AbstractBeanDefinition createRedisMappingContext(String mappingConfigRef) {

        return BeanDefinitionBuilder.rootBeanDefinition(RedisMappingContext.class) //
                .addConstructorArgReference(mappingConfigRef).getBeanDefinition();
    }

    private static AbstractBeanDefinition createMappingConfigBeanDef(String indexConfigRef, String keyspaceConfigRef) {

        return BeanDefinitionBuilder.genericBeanDefinition(MappingConfiguration.class) //
                .addConstructorArgReference(indexConfigRef) //
                .addConstructorArgReference(keyspaceConfigRef) //
                .getBeanDefinition();
    }

    private static AbstractBeanDefinition createRedisConverterDefinition() {

        return BeanDefinitionBuilder.rootBeanDefinition(MappingRedisConverter.class) //
                .addConstructorArgReference(MAPPING_CONTEXT_BEAN_NAME) //
                .addPropertyReference("referenceResolver", REDIS_REFERENCE_RESOLVER_BEAN_NAME) //
                .addPropertyReference("customConversions", REDIS_CUSTOM_CONVERSIONS_BEAN_NAME) //
                .getBeanDefinition();
    }

    @Override
    public String getModuleName() {
        return "Redis";
    }

    @Override
    protected String getModulePrefix() {
        return this.getModuleIdentifier();
    }

    @Override
    protected String getDefaultKeyValueTemplateRef() {
        return "redisKeyValueTemplate";
    }

    @Override
    public void registerBeansForRoot(BeanDefinitionRegistry registry, RepositoryConfigurationSource configuration) {

        String redisTemplateRef = configuration.getAttribute("redisTemplateRef").get();

        if (!StringUtils.hasText(redisTemplateRef)) {
            throw new IllegalStateException(
                    "@EnableRedisRepositories(redisTemplateRef = … ) must be configured to a non empty value");
        }

        // Mapping config

        String mappingConfigBeanName = BeanDefinitionReaderUtils.uniqueBeanName(REDIS_MAPPING_CONFIG_BEAN_NAME, registry);
        String indexConfigurationBeanName = BeanDefinitionReaderUtils.uniqueBeanName("redisIndexConfiguration", registry);
        String keyspaceConfigurationBeanName = BeanDefinitionReaderUtils.uniqueBeanName("redisKeyspaceConfiguration",
                registry);

        registerIfNotAlreadyRegistered(() -> BeanDefinitionBuilder
                .rootBeanDefinition(configuration.getRequiredAttribute("indexConfiguration", Class.class)) //
                .setRole(BeanDefinition.ROLE_INFRASTRUCTURE) //
                .getBeanDefinition(), registry, indexConfigurationBeanName, configuration.getSource());

        registerIfNotAlreadyRegistered(() -> BeanDefinitionBuilder
                .rootBeanDefinition(configuration.getRequiredAttribute("keyspaceConfiguration", Class.class)) //
                .setRole(BeanDefinition.ROLE_INFRASTRUCTURE) //
                .getBeanDefinition(), registry, keyspaceConfigurationBeanName, configuration.getSource());

        registerIfNotAlreadyRegistered(
                () -> createMappingConfigBeanDef(indexConfigurationBeanName, keyspaceConfigurationBeanName), registry,
                mappingConfigBeanName, configuration.getSource());

        registerIfNotAlreadyRegistered(() -> createRedisMappingContext(mappingConfigBeanName), registry,
                MAPPING_CONTEXT_BEAN_NAME, configuration.getSource());

        // Register custom conversions
        registerIfNotAlreadyRegistered(() -> new RootBeanDefinition(RedisCustomConversions.class), registry,
                REDIS_CUSTOM_CONVERSIONS_BEAN_NAME, configuration.getSource());

        // Register referenceResolver
        registerIfNotAlreadyRegistered(() -> createRedisReferenceResolverDefinition(redisTemplateRef), registry,
                REDIS_REFERENCE_RESOLVER_BEAN_NAME, configuration.getSource());

        // Register converter
        registerIfNotAlreadyRegistered(() -> createRedisConverterDefinition(), registry, REDIS_CONVERTER_BEAN_NAME,
                configuration.getSource());

        registerIfNotAlreadyRegistered(() -> createRedisKeyValueAdapter(configuration), registry, REDIS_ADAPTER_BEAN_NAME,
                configuration.getSource());

        super.registerBeansForRoot(registry, configuration);
    }

    @Override
    protected AbstractBeanDefinition getDefaultKeyValueTemplateBeanDefinition(
            RepositoryConfigurationSource configurationSource) {

        return BeanDefinitionBuilder.rootBeanDefinition(RedisDbKeyValueTemplate.class) //
                .addConstructorArgReference(REDIS_ADAPTER_BEAN_NAME) //
                .addConstructorArgReference(MAPPING_CONTEXT_BEAN_NAME) //
                .getBeanDefinition();
    }

    @Override
    protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
        return Collections.singleton(RedisHash.class);
    }
}
