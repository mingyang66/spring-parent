package com.emily.infrastructure.redis.repository.data;

import com.emily.infrastructure.redis.repository.EnableRedisDbRepositories;
import com.emily.infrastructure.redis.repository.RedisDbRepositoryConfigurationExtension;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

/**
 * ImportBeanDefinitionRegistrar used to auto-configure Spring Data Redis Repositories.
 *
 * @author :  Emily
 * @since :  2024/7/8 上午19:39
 */
public class RedisDbRepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableRedisDbRepositories.class;
    }

    @Override
    protected Class<?> getConfiguration() {
        return EnableRedisDbRepositoriesConfiguration.class;
    }

    @Override
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new RedisDbRepositoryConfigurationExtension();
    }

    @EnableRedisDbRepositories
    private static final class EnableRedisDbRepositoriesConfiguration {

    }

}
