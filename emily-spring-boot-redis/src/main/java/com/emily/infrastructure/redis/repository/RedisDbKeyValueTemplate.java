package com.emily.infrastructure.redis.repository;

import org.springframework.data.keyvalue.core.KeyValueAdapter;
import org.springframework.data.keyvalue.core.KeyValueCallback;
import org.springframework.data.keyvalue.core.KeyValueTemplate;
import org.springframework.data.keyvalue.core.mapping.KeyValuePersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.redis.core.PartialUpdate;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.RedisConverter;
import org.springframework.data.redis.core.convert.RedisData;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.data.redis.core.mapping.RedisPersistentEntity;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author : Emily
 * @since :  2024/7/8 下午21:37
 */
public class RedisDbKeyValueTemplate extends KeyValueTemplate {

    private final RedisDbKeyValueAdapter adapter;

    /**
     * Create new {@link RedisDbKeyValueTemplate}.
     *
     * @param adapter        must not be {@literal null}.
     * @param mappingContext must not be {@literal null}.
     */
    public RedisDbKeyValueTemplate(RedisDbKeyValueAdapter adapter, RedisMappingContext mappingContext) {
        super(adapter, mappingContext);
        this.adapter = adapter;
    }

    /**
     * Obtain the underlying redis specific {@link org.springframework.data.convert.EntityConverter}.
     *
     * @return never {@literal null}.
     * @since 2.1
     */
    public RedisConverter getConverter() {
        return adapter.getConverter();
    }

    @Override
    public RedisMappingContext getMappingContext() {
        return (RedisMappingContext) super.getMappingContext();
    }

    /**
     * Retrieve entities by resolving their {@literal id}s and converting them into required type. <br />
     * The callback provides either a single {@literal id} or an {@link Iterable} of {@literal id}s, used for retrieving
     * the actual domain types and shortcuts manual retrieval and conversion of {@literal id}s via {@link RedisTemplate}.
     *
     * <pre>
     * <code>
     * List&#60;RedisSession&#62; sessions = template.find(new RedisCallback&#60;Set&#60;byte[]&#62;&#62;() {
     *   public Set&#60;byte[]&#60; doInRedis(RedisConnection connection) throws DataAccessException {
     *     return connection
     *       .sMembers("spring:session:sessions:securityContext.authentication.principal.username:user"
     *         .getBytes());
     *   }
     * }, RedisSession.class);
     * </code>
     * </pre>
     *
     * @param callback provides the to retrieve entity ids. Must not be {@literal null}.
     * @param type     must not be {@literal null}.
     * @return empty list if not elements found.
     */
    public <T> List<T> find(RedisCallback<?> callback, Class<T> type) {

        Assert.notNull(callback, "Callback must not be null");

        return execute(new RedisDbKeyValueTemplate.RedisKeyValueCallback<List<T>>() {

            @Override
            public List<T> doInRedis(RedisDbKeyValueAdapter adapter) {

                Object callbackResult = adapter.execute(callback);

                if (callbackResult == null) {
                    return Collections.emptyList();
                }

                Iterable<?> ids = ClassUtils.isAssignable(Iterable.class, callbackResult.getClass())
                        ? (Iterable<?>) callbackResult : Collections.singleton(callbackResult);

                List<T> result = new ArrayList<>();
                for (Object id : ids) {

                    String idToUse = adapter.getConverter().getConversionService().canConvert(id.getClass(), String.class)
                            ? adapter.getConverter().getConversionService().convert(id, String.class) : id.toString();

                    findById(idToUse, type).ifPresent(result::add);
                }

                return result;
            }
        });
    }

    @Override
    public <T> T insert(Object id, T objectToInsert) {

        if (objectToInsert instanceof PartialUpdate) {
            doPartialUpdate((PartialUpdate<?>) objectToInsert);
            return objectToInsert;
        }

        if (!(objectToInsert instanceof RedisData)) {

            RedisConverter converter = adapter.getConverter();

            RedisPersistentEntity<?> entity = converter.getMappingContext()
                    .getRequiredPersistentEntity(objectToInsert.getClass());

            KeyValuePersistentProperty idProperty = entity.getRequiredIdProperty();
            PersistentPropertyAccessor<T> propertyAccessor = entity.getPropertyAccessor(objectToInsert);

            if (propertyAccessor.getProperty(idProperty) == null) {

                propertyAccessor.setProperty(idProperty, id);
                return super.insert(id, propertyAccessor.getBean());
            }
        }

        return super.insert(id, objectToInsert);
    }

    @Override
    public <T> T update(T objectToUpdate) {

        if (objectToUpdate instanceof PartialUpdate<?> partialUpdate) {
            doPartialUpdate(partialUpdate);

            return objectToUpdate;
        }

        return super.update(objectToUpdate);
    }

    @Override
    public <T> T update(Object id, T objectToUpdate) {
        return super.update(id, objectToUpdate);
    }

    protected void doPartialUpdate(final PartialUpdate<?> update) {

        execute(new RedisDbKeyValueTemplate.RedisKeyValueCallback<Void>() {

            @Override
            public Void doInRedis(RedisDbKeyValueAdapter adapter) {

                adapter.update(update);
                return null;
            }
        });
    }

    /**
     * Redis specific {@link KeyValueCallback}.
     *
     * @param <T>
     * @author Christoph Strobl
     * @since 1.7
     */
    public static abstract class RedisKeyValueCallback<T> implements KeyValueCallback<T> {

        @Override
        public T doInKeyValue(KeyValueAdapter adapter) {
            return doInRedis((RedisDbKeyValueAdapter) adapter);
        }

        public abstract T doInRedis(RedisDbKeyValueAdapter adapter);
    }

}
