package com.emily.infrastructure.redis.repository;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.keyvalue.core.KeyValueOperations;
import org.springframework.data.keyvalue.repository.config.QueryCreatorType;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.data.redis.core.index.IndexConfiguration;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.repository.query.RedisPartTreeQuery;
import org.springframework.data.redis.repository.query.RedisQueryCreator;
import org.springframework.data.redis.repository.support.RedisRepositoryFactoryBean;
import org.springframework.data.repository.config.DefaultRepositoryBaseClass;
import org.springframework.data.repository.query.QueryLookupStrategy;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(RedisDbRepositoriesRegistrar.class)
@QueryCreatorType(value = RedisQueryCreator.class, repositoryQueryType = RedisPartTreeQuery.class)
public @interface EnableRedisDbRepositories {

    /**
     * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation declarations e.g.:
     * {@code @EnableRedisRepositories("org.my.pkg")} instead of
     * {@code @EnableRedisRepositories(basePackages="org.my.pkg")}.
     */
    String[] value() default {};

    /**
     * Base packages to scan for annotated components. {@link #value()} is an alias for (and mutually exclusive with) this
     * attribute. Use {@link #basePackageClasses()} for a type-safe alternative to String-based package names.
     */
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages to scan for annotated components. The
     * package of each class specified will be scanned. Consider creating a special no-op marker class or interface in
     * each package that serves no purpose other than being referenced by this attribute.
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * Specifies which types are not eligible for component scanning.
     */
    ComponentScan.Filter[] excludeFilters() default {};

    /**
     * Specifies which types are eligible for component scanning. Further narrows the set of candidate components from
     * everything in {@link #basePackages()} to everything in the base packages that matches the given filter or filters.
     */
    ComponentScan.Filter[] includeFilters() default {};

    /**
     * Returns the postfix to be used when looking up custom repository implementations. Defaults to {@literal Impl}. So
     * for a repository named {@code PersonRepository} the corresponding implementation class will be looked up scanning
     * for {@code PersonRepositoryImpl}.
     *
     * @return
     */
    String repositoryImplementationPostfix() default "Impl";

    /**
     * Configures the location of where to find the Spring Data named queries properties file.
     *
     * @return
     */
    String namedQueriesLocation() default "";

    /**
     * Returns the key of the {@link QueryLookupStrategy} to be used for lookup queries for query methods. Defaults to
     * {@link QueryLookupStrategy.Key#CREATE_IF_NOT_FOUND}.
     *
     * @return
     */
    QueryLookupStrategy.Key queryLookupStrategy() default QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND;

    /**
     * Returns the {@link FactoryBean} class to be used for each repository instance. Defaults to
     * {@link RedisRepositoryFactoryBean}.
     *
     * @return
     */
    Class<?> repositoryFactoryBeanClass() default RedisRepositoryFactoryBean.class;

    /**
     * Configure the repository base class to be used to create repository proxies for this particular configuration.
     *
     * @return
     */
    Class<?> repositoryBaseClass() default DefaultRepositoryBaseClass.class;

    /**
     * Configures the name of the {@link KeyValueOperations} bean to be used with the repositories detected.
     *
     * @return
     */
    String keyValueTemplateRef() default "redisKeyValueTemplate";

    /**
     * Configures whether nested repository-interfaces (e.g. defined as inner classes) should be discovered by the
     * repositories infrastructure.
     */
    boolean considerNestedRepositories() default false;

    /**
     * Configures the bean name of the {@link RedisOperations} to be used. Defaulted to {@literal redisTemplate}.
     *
     * @return
     */
    String redisTemplateRef() default "redisTemplate";

    /**
     * Set up index patterns using simple configuration class.
     *
     * @return
     */
    Class<? extends IndexConfiguration> indexConfiguration() default IndexConfiguration.class;

    /**
     * Set up keyspaces for specific types.
     *
     * @return
     */
    Class<? extends KeyspaceConfiguration> keyspaceConfiguration() default KeyspaceConfiguration.class;

    /**
     * Configure usage of {@link KeyExpirationEventMessageListener}.
     *
     * @return
     * @since 1.8
     */
    RedisDbKeyValueAdapter.EnableKeyspaceEvents enableKeyspaceEvents() default RedisDbKeyValueAdapter.EnableKeyspaceEvents.OFF;

    /**
     * Configure the name of the {@link org.springframework.data.redis.listener.RedisMessageListenerContainer} bean to be
     * used for keyspace event subscriptions. Defaults to use an anonymous managed instance by
     * {@linK RedisDbKeyValueAdapter}.
     *
     * @return
     * @since 2.7.2
     */
    String messageListenerContainerRef() default "";

    /**
     * Configuration flag controlling storage of phantom keys (shadow copies) of expiring entities to read them later when
     * publishing {@link org.springframework.data.redis.core.RedisKeyspaceEvent keyspace events}.
     *
     * @return
     * @since 2.4
     */
    RedisDbKeyValueAdapter.ShadowCopy shadowCopy() default RedisDbKeyValueAdapter.ShadowCopy.DEFAULT;

    /**
     * Configure the {@literal notify-keyspace-events} property if not already set. <br />
     * Use an empty {@link String} to keep (<b>not</b> alter) existing server configuration.
     *
     * @return {@literal Ex} by default.
     * @since 1.8
     */
    String keyspaceNotificationsConfigParameter() default "Ex";

}
