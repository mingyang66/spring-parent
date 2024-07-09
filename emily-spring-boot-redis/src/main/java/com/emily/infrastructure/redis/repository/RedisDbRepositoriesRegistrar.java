package com.emily.infrastructure.redis.repository;

import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.repository.configuration.RedisRepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

/**
 * @author :  Emily
 * @since :  2024/7/9 下午9:40
 */
public class RedisDbRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableRedisDbRepositories.class;
    }

    @Override
    protected RepositoryConfigurationExtension getExtension() {
        return new RedisDbRepositoryConfigurationExtension();
    }
}
