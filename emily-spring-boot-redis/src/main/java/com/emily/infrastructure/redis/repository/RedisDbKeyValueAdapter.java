package com.emily.infrastructure.redis.repository;


import com.emily.infrastructure.redis.RedisDbProperties;
import com.emily.infrastructure.redis.RedisProperties;
import com.emily.infrastructure.redis.factory.BeanFactoryProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.keyvalue.core.AbstractKeyValueAdapter;
import org.springframework.data.keyvalue.core.KeyValueAdapter;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.PartialUpdate.PropertyUpdate;
import org.springframework.data.redis.core.PartialUpdate.UpdateCommand;
import org.springframework.data.redis.core.convert.*;
import org.springframework.data.redis.core.convert.MappingRedisConverter.BinaryKeyspaceIdentifier;
import org.springframework.data.redis.core.convert.MappingRedisConverter.KeyspaceIdentifier;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.data.redis.core.mapping.RedisPersistentEntity;
import org.springframework.data.redis.core.mapping.RedisPersistentProperty;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.util.ByteUtils;
import org.springframework.data.util.CloseableIterator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Redis specific {@link KeyValueAdapter} implementation. Uses binary codec to read/write data from/to Redis. Objects
 * are stored in a Redis Hash using the value of {@link RedisHash}, the {@link KeyspaceConfiguration} or just
 * {@link Class#getName()} as a prefix. <br />
 * <strong>Example</strong>
 *
 * <pre>
 * <code>
 * &#64;RedisHash("persons")
 * class Person {
 *   &#64;Id String id;
 *   String name;
 * }
 *
 *
 *         prefix              ID
 *           |                 |
 *           V                 V
 * hgetall persons:5d67b7e1-8640-4475-beeb-c666fab4c0e5
 * 1) id
 * 2) 5d67b7e1-8640-4475-beeb-c666fab4c0e5
 * 3) name
 * 4) Rand al'Thor
 * </code>
 * </pre>
 * <p>
 * <br />
 * The {@link KeyValueAdapter} is <strong>not</strong> intended to store simple types such as {@link String} values.
 * Please use {@link RedisTemplate} for this purpose.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @author Andrey Muchnik
 * @author John Blum
 * @since 1.7
 */
public class RedisDbKeyValueAdapter extends AbstractKeyValueAdapter
        implements InitializingBean, ApplicationContextAware, ApplicationListener<RedisKeyspaceEvent> {

    /**
     * Time To Live in seconds that phantom keys should live longer than the actual key.
     */
    private static final int PHANTOM_KEY_TTL = 300;

    private RedisOperations<?, ?> redisOps;
    private RedisConverter converter;
    private @Nullable RedisMessageListenerContainer messageListenerContainer;
    private boolean managedListenerContainer = true;
    private final AtomicReference<KeyExpirationEventMessageListener> expirationListener = new AtomicReference<>(null);
    private @Nullable ApplicationEventPublisher eventPublisher;

    private RedisDbKeyValueAdapter.EnableKeyspaceEvents enableKeyspaceEvents = RedisDbKeyValueAdapter.EnableKeyspaceEvents.OFF;
    private @Nullable String keyspaceNotificationsConfigParameter = null;
    private RedisDbKeyValueAdapter.ShadowCopy shadowCopy = RedisDbKeyValueAdapter.ShadowCopy.DEFAULT;

    /**
     * Creates new {@link RedisDbKeyValueAdapter} with default {@link RedisMappingContext} and default
     * {@link RedisCustomConversions}.
     *
     * @param redisOps must not be {@literal null}.
     */
    public RedisDbKeyValueAdapter(RedisOperations<?, ?> redisOps) {
        this(redisOps, new RedisMappingContext());
    }

    /**
     * Creates new {@link RedisDbKeyValueAdapter} with default {@link RedisCustomConversions}.
     *
     * @param redisOps       must not be {@literal null}.
     * @param mappingContext must not be {@literal null}.
     */
    public RedisDbKeyValueAdapter(RedisOperations<?, ?> redisOps, RedisMappingContext mappingContext) {
        this(redisOps, mappingContext, new RedisCustomConversions());
    }

    /**
     * Creates new {@link RedisDbKeyValueAdapter}.
     *
     * @param redisOps          must not be {@literal null}.
     * @param mappingContext    must not be {@literal null}.
     * @param customConversions can be {@literal null}.
     * @since 2.0
     */
    public RedisDbKeyValueAdapter(RedisOperations<?, ?> redisOps, RedisMappingContext mappingContext,
                                  @Nullable org.springframework.data.convert.CustomConversions customConversions) {

        super(new RedisDbQueryEngine());

        Assert.notNull(redisOps, "RedisOperations must not be null");
        Assert.notNull(mappingContext, "RedisMappingContext must not be null");

        MappingRedisConverter mappingConverter = new MappingRedisConverter(mappingContext,
                new PathIndexResolver(mappingContext), new ReferenceResolverImpl(redisOps));

        mappingConverter.setCustomConversions(customConversions == null ? new RedisCustomConversions() : customConversions);
        mappingConverter.afterPropertiesSet();

        this.converter = mappingConverter;
        this.redisOps = redisOps;
        initMessageListenerContainer();
    }

    /**
     * Creates new {@link RedisDbKeyValueAdapter} with specific {@link RedisConverter}.
     *
     * @param redisOps       must not be {@literal null}.
     * @param redisConverter must not be {@literal null}.
     */
    public RedisDbKeyValueAdapter(RedisOperations<?, ?> redisOps, RedisConverter redisConverter) {

        super(new RedisDbQueryEngine());

        Assert.notNull(redisOps, "RedisOperations must not be null");

        this.converter = redisConverter;
        this.redisOps = redisOps;
    }

    /**
     * Default constructor.
     */
    protected RedisDbKeyValueAdapter() {
    }

    @Override
    public Object put(Object id, Object item, String keyspace) {

        RedisData rdo = item instanceof RedisData ? (RedisData) item : new RedisData();

        if (!(item instanceof RedisData)) {
            converter.write(item, rdo);
        }

        if (ObjectUtils.nullSafeEquals(RedisDbKeyValueAdapter.EnableKeyspaceEvents.ON_DEMAND, enableKeyspaceEvents)
                && this.expirationListener.get() == null) {

            if (rdo.getTimeToLive() != null && rdo.getTimeToLive() > 0) {
                initKeyExpirationListener();
            }
        }

        if (rdo.getId() == null) {
            rdo.setId(converter.getConversionService().convert(id, String.class));
        }

        redisOps.execute((RedisCallback<Object>) connection -> {

            byte[] key = toBytes(rdo.getId());
            byte[] objectKey = createKey(rdo.getKeyspace(), rdo.getId());

            boolean isNew = connection.del(objectKey) == 0;

            connection.hMSet(objectKey, rdo.getBucket().rawMap());

            if (isNew) {
                connection.sAdd(toBytes(rdo.getKeyspace()), key);
            }

            if (expires(rdo)) {
                connection.expire(objectKey, rdo.getTimeToLive());
            }

            if (keepShadowCopy()) { // add phantom key so values can be restored

                byte[] phantomKey = ByteUtils.concat(objectKey, BinaryKeyspaceIdentifier.PHANTOM_SUFFIX);

                if (expires(rdo)) {
                    connection.del(phantomKey);
                    connection.hMSet(phantomKey, rdo.getBucket().rawMap());
                    connection.expire(phantomKey, rdo.getTimeToLive() + PHANTOM_KEY_TTL);
                } else if (!isNew) {
                    connection.del(phantomKey);
                }
            }

            IndexDbWriter indexWriter = new IndexDbWriter(connection, converter);

            if (isNew) {
                indexWriter.createIndexes(key, rdo.getIndexedData());
            } else {
                indexWriter.deleteAndUpdateIndexes(key, rdo.getIndexedData());
            }

            return null;
        });

        return item;
    }

    @Override
    public boolean contains(Object id, String keyspace) {

        RedisCallback<Boolean> command = connection -> connection.sIsMember(toBytes(keyspace), toBytes(id));

        return Boolean.TRUE.equals(this.redisOps.execute(command));
    }

    @Nullable
    @Override
    public Object get(Object id, String keyspace) {
        return get(id, keyspace, Object.class);
    }

    @Nullable
    @Override
    public <T> T get(Object id, String keyspace, Class<T> type) {

        String stringId = toString(id);
        byte[] binId = createKey(keyspace, stringId);

        RedisCallback<Map<byte[], byte[]>> command = connection -> connection.hGetAll(binId);

        Map<byte[], byte[]> raw = redisOps.execute(command);

        if (CollectionUtils.isEmpty(raw)) {
            return null;
        }

        RedisData data = new RedisData(raw);

        data.setId(stringId);
        data.setKeyspace(keyspace);

        return readBackTimeToLiveIfSet(binId, converter.read(type, data));
    }

    @Override
    public Object delete(Object id, String keyspace) {
        return delete(id, keyspace, Object.class);
    }

    @Override
    public <T> T delete(Object id, String keyspace, Class<T> type) {

        byte[] binId = toBytes(id);
        byte[] binKeyspace = toBytes(keyspace);

        T value = get(id, keyspace, type);

        if (value != null) {

            byte[] keyToDelete = createKey(keyspace, toString(id));

            redisOps.execute((RedisCallback<Void>) connection -> {

                connection.del(keyToDelete);
                connection.sRem(binKeyspace, binId);
                new IndexDbWriter(connection, converter).removeKeyFromIndexes(keyspace, binId);

                if (RedisDbKeyValueAdapter.this.keepShadowCopy()) {

                    RedisPersistentEntity<?> persistentEntity = converter.getMappingContext().getPersistentEntity(type);

                    if (persistentEntity != null && persistentEntity.isExpiring()) {

                        byte[] phantomKey = ByteUtils.concat(keyToDelete, BinaryKeyspaceIdentifier.PHANTOM_SUFFIX);

                        connection.del(phantomKey);
                    }
                }
                return null;
            });
        }

        return value;
    }

    @Override
    public List<?> getAllOf(String keyspace) {
        return getAllOf(keyspace, Object.class, -1, -1);
    }

    @Override
    public <T> Iterable<T> getAllOf(String keyspace, Class<T> type) {
        return getAllOf(keyspace, type, -1, -1);
    }

    /**
     * Get all elements for given keyspace.
     *
     * @param keyspace the keyspace to fetch entities from.
     * @param type     the desired target type.
     * @param offset   index value to start reading.
     * @param rows     maximum number or entities to return.
     * @return never {@literal null}.
     * @since 2.5
     */
    public <T> List<T> getAllOf(String keyspace, Class<T> type, long offset, int rows) {

        byte[] binKeyspace = toBytes(keyspace);

        Set<byte[]> ids = redisOps.execute((RedisCallback<Set<byte[]>>) connection -> connection.sMembers(binKeyspace));

        List<T> result = new ArrayList<>();
        List<byte[]> keys = new ArrayList<>(ids);

        if (keys.isEmpty() || keys.size() < offset) {
            return Collections.emptyList();
        }

        offset = Math.max(0, offset);

        if (rows > 0) {
            keys = keys.subList((int) offset, Math.min((int) offset + rows, keys.size()));
        }

        for (byte[] key : keys) {
            result.add(get(key, keyspace, type));
        }
        return result;
    }

    @Override
    public void deleteAllOf(String keyspace) {

        redisOps.execute((RedisCallback<Void>) connection -> {

            connection.del(toBytes(keyspace));
            new IndexDbWriter(connection, converter).removeAllIndexes(keyspace);

            return null;
        });
    }

    @Override
    public CloseableIterator<Entry<Object, Object>> entries(String keyspace) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public long count(String keyspace) {

        Long count = redisOps.execute((RedisCallback<Long>) connection -> connection.sCard(toBytes(keyspace)));

        return count != null ? count : 0;
    }

    public void update(PartialUpdate<?> update) {

        RedisPersistentEntity<?> entity = this.converter.getMappingContext()
                .getRequiredPersistentEntity(update.getTarget());

        String keyspace = entity.getKeySpace();
        Object id = update.getId();

        byte[] redisKey = createKey(keyspace, converter.getConversionService().convert(id, String.class));

        RedisData rdo = new RedisData();
        this.converter.write(update, rdo);

        redisOps.execute((RedisCallback<Void>) connection -> {

            RedisDbKeyValueAdapter.RedisUpdateObject redisUpdateObject = new RedisDbKeyValueAdapter.RedisUpdateObject(redisKey, keyspace, id);

            for (PropertyUpdate pUpdate : update.getPropertyUpdates()) {

                String propertyPath = pUpdate.getPropertyPath();

                if (UpdateCommand.DEL.equals(pUpdate.getCmd()) || pUpdate.getValue() instanceof Collection
                        || pUpdate.getValue() instanceof Map
                        || (pUpdate.getValue() != null && pUpdate.getValue().getClass().isArray()) || (pUpdate.getValue() != null
                        && !converter.getConversionService().canConvert(pUpdate.getValue().getClass(), byte[].class))) {

                    redisUpdateObject = fetchDeletePathsFromHashAndUpdateIndex(redisUpdateObject, propertyPath, connection);
                }
            }

            if (!redisUpdateObject.fieldsToRemove.isEmpty()) {
                connection.hDel(redisKey,
                        redisUpdateObject.fieldsToRemove.toArray(new byte[redisUpdateObject.fieldsToRemove.size()][]));
            }

            for (RedisUpdateObject.Index index : redisUpdateObject.indexesToUpdate) {

                if (ObjectUtils.nullSafeEquals(DataType.ZSET, index.type)) {
                    connection.zRem(index.key, toBytes(redisUpdateObject.targetId));
                } else {
                    connection.sRem(index.key, toBytes(redisUpdateObject.targetId));
                }
            }

            if (!rdo.getBucket().isEmpty()) {
                if (rdo.getBucket().size() > 1
                        || (rdo.getBucket().size() == 1 && !rdo.getBucket().asMap().containsKey("_class"))) {
                    connection.hMSet(redisKey, rdo.getBucket().rawMap());
                }
            }

            if (update.isRefreshTtl()) {

                if (expires(rdo)) {

                    connection.expire(redisKey, rdo.getTimeToLive());

                    if (keepShadowCopy()) { // add phantom key so values can be restored

                        byte[] phantomKey = ByteUtils.concat(redisKey, BinaryKeyspaceIdentifier.PHANTOM_SUFFIX);

                        connection.hMSet(phantomKey, rdo.getBucket().rawMap());
                        connection.expire(phantomKey, rdo.getTimeToLive() + PHANTOM_KEY_TTL);
                    }

                } else {

                    connection.persist(redisKey);

                    if (keepShadowCopy()) {
                        connection.del(ByteUtils.concat(redisKey, BinaryKeyspaceIdentifier.PHANTOM_SUFFIX));
                    }
                }
            }

            new IndexDbWriter(connection, converter).updateIndexes(toBytes(id), rdo.getIndexedData());
            return null;
        });
    }

    private RedisDbKeyValueAdapter.RedisUpdateObject fetchDeletePathsFromHashAndUpdateIndex(RedisDbKeyValueAdapter.RedisUpdateObject redisUpdateObject, String path,
                                                                                            RedisConnection connection) {

        redisUpdateObject.addFieldToRemove(toBytes(path));

        byte[] value = connection.hGet(redisUpdateObject.targetKey, toBytes(path));

        if (value != null && value.length > 0) {

            byte[] existingValueIndexKey = ByteUtils.concatAll(toBytes(redisUpdateObject.keyspace), toBytes(":" + path),
                    toBytes(":"), value);

            if (connection.exists(existingValueIndexKey)) {
                redisUpdateObject.addIndexToUpdate(new RedisDbKeyValueAdapter.RedisUpdateObject.Index(existingValueIndexKey, DataType.SET));
            }

            return redisUpdateObject;
        }

        Set<byte[]> existingFields = connection.hKeys(redisUpdateObject.targetKey);

        for (byte[] field : existingFields) {

            if (toString(field).startsWith(path + ".")) {

                redisUpdateObject.addFieldToRemove(field);
                value = connection.hGet(redisUpdateObject.targetKey, toBytes(field));

                if (value != null) {

                    byte[] existingValueIndexKey = ByteUtils.concatAll(toBytes(redisUpdateObject.keyspace), toBytes(":"), field,
                            toBytes(":"), value);

                    if (connection.exists(existingValueIndexKey)) {
                        redisUpdateObject.addIndexToUpdate(new RedisDbKeyValueAdapter.RedisUpdateObject.Index(existingValueIndexKey, DataType.SET));
                    }
                }
            }
        }

        String pathToUse = GeoIndexedPropertyValue.geoIndexName(path);
        byte[] existingGeoIndexKey = ByteUtils.concatAll(toBytes(redisUpdateObject.keyspace), toBytes(":"),
                toBytes(pathToUse));

        if (connection.zRank(existingGeoIndexKey, toBytes(redisUpdateObject.targetId)) != null) {
            redisUpdateObject.addIndexToUpdate(new RedisDbKeyValueAdapter.RedisUpdateObject.Index(existingGeoIndexKey, DataType.ZSET));
        }

        return redisUpdateObject;
    }

    /**
     * Execute {@link RedisCallback} via underlying {@link RedisOperations}.
     *
     * @param callback must not be {@literal null}.
     * @see RedisOperations#execute(RedisCallback)
     */
    @Nullable
    public <T> T execute(RedisCallback<T> callback) {
        return redisOps.execute(callback);
    }

    /**
     * Get the {@link RedisConverter} in use.
     *
     * @return never {@literal null}.
     */
    public RedisConverter getConverter() {
        return this.converter;
    }

    public void clear() {
        // nothing to do
    }

    /**
     * Creates a new {@link byte[] key} using the given {@link String keyspace} and {@link String id}.
     *
     * @param keyspace {@link String name} of the Redis {@literal keyspace}.
     * @param id       {@link String} identifying the key.
     * @return a {@link byte[]} constructed from the {@link String keyspace} and {@link String id}.
     */
    public byte[] createKey(String keyspace, String id) {
        return toBytes(keyspace + ":" + id);
    }

    /**
     * Convert given source to binary representation using the underlying {@link ConversionService}.
     */
    public byte[] toBytes(Object source) {
        return source instanceof byte[] bytes ? bytes
                : getConverter().getConversionService().convert(source, byte[].class);
    }

    private String toString(Object value) {
        return value instanceof String stringValue ? stringValue
                : getConverter().getConversionService().convert(value, String.class);
    }

    /**
     * Read back and set {@link TimeToLive} for the property.
     */
    @Nullable
    private <T> T readBackTimeToLiveIfSet(@Nullable byte[] key, @Nullable T target) {

        if (target == null || key == null) {
            return target;
        }

        RedisPersistentEntity<?> entity = this.converter.getMappingContext().getRequiredPersistentEntity(target.getClass());

        if (entity.hasExplictTimeToLiveProperty()) {

            RedisPersistentProperty ttlProperty = entity.getExplicitTimeToLiveProperty();

            if (ttlProperty == null) {
                return target;
            }

            TimeToLive ttl = ttlProperty.findAnnotation(TimeToLive.class);

            Long timeout = redisOps.execute((RedisCallback<Long>) connection -> {

                if (ObjectUtils.nullSafeEquals(TimeUnit.SECONDS, ttl.unit())) {
                    return connection.ttl(key);
                }

                return connection.pTtl(key, ttl.unit());
            });

            if (timeout != null || !ttlProperty.getType().isPrimitive()) {

                PersistentPropertyAccessor<T> propertyAccessor = entity.getPropertyAccessor(target);

                propertyAccessor.setProperty(ttlProperty,
                        converter.getConversionService().convert(timeout, ttlProperty.getType()));

                target = propertyAccessor.getBean();
            }
        }

        return target;
    }

    /**
     * @param data must not be {@literal null}.
     * @return {@literal true} if {@link RedisData#getTimeToLive()} has a positive value.
     * @since 2.3.7
     */
    private boolean expires(RedisData data) {
        return data.getTimeToLive() != null && data.getTimeToLive() > 0;
    }

    /**
     * Configure usage of {@link KeyExpirationEventMessageListener}.
     *
     * @since 1.8
     */
    public void setEnableKeyspaceEvents(RedisDbKeyValueAdapter.EnableKeyspaceEvents enableKeyspaceEvents) {
        this.enableKeyspaceEvents = enableKeyspaceEvents;
    }

    /**
     * Configure a {@link RedisMessageListenerContainer} to listen for Keyspace expiry events. The container can only be
     * set when this bean hasn't been yet {@link #afterPropertiesSet() initialized}.
     *
     * @param messageListenerContainer the container to use.
     * @throws IllegalStateException when trying to set a {@link RedisMessageListenerContainer} after
     *                               {@link #afterPropertiesSet()} has been called to initialize a managed container instance.
     * @since 2.7.2
     */
    public void setMessageListenerContainer(RedisMessageListenerContainer messageListenerContainer) {

        Assert.notNull(messageListenerContainer, "RedisMessageListenerContainer must not be null");

        if (this.managedListenerContainer && this.messageListenerContainer != null) {
            throw new IllegalStateException(
                    "Cannot set RedisMessageListenerContainer after initializing a managed RedisMessageListenerContainer instance");
        }

        this.managedListenerContainer = false;
        this.messageListenerContainer = messageListenerContainer;
    }

    /**
     * Configure the {@literal notify-keyspace-events} property if not already set. Use an empty {@link String} or
     * {@literal null} to retain existing server settings.
     *
     * @param keyspaceNotificationsConfigParameter can be {@literal null}.
     * @since 1.8
     */
    public void setKeyspaceNotificationsConfigParameter(String keyspaceNotificationsConfigParameter) {
        this.keyspaceNotificationsConfigParameter = keyspaceNotificationsConfigParameter;
    }

    /**
     * Configure storage of phantom keys (shadow copies) of expiring entities.
     *
     * @param shadowCopy must not be {@literal null}.
     * @since 2.3
     */
    public void setShadowCopy(RedisDbKeyValueAdapter.ShadowCopy shadowCopy) {
        this.shadowCopy = shadowCopy;
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     * @since 1.8
     */
    @Override
    public void afterPropertiesSet() {

        if (this.managedListenerContainer) {
            initMessageListenerContainer();
        }

        if (ObjectUtils.nullSafeEquals(RedisDbKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP, this.enableKeyspaceEvents)) {
            initKeyExpirationListener();
        }
    }

    public void destroy() throws Exception {

        if (this.expirationListener.get() != null) {
            this.expirationListener.get().destroy();
        }

        if (this.managedListenerContainer && this.messageListenerContainer != null) {
            RedisDbProperties redisDbProperties = BeanFactoryProvider.getBean(RedisDbProperties.class);
            String defaultConfig = Objects.requireNonNull(redisDbProperties.getDefaultConfig(), "Redis默认标识不可为空");
            for (String key : redisDbProperties.getConfig().keySet()) {
                if (defaultConfig.equals(key)) {
                    this.messageListenerContainer.destroy();
                    this.messageListenerContainer = null;
                } else {
                    RedisMessageListenerContainer redisMessageListenerContainer = BeanFactoryProvider.getBean(key + "RedisMessageListenerContainer", RedisMessageListenerContainer.class);
                    redisMessageListenerContainer.destroy();
                }
            }
        }
    }

    @Override
    public void onApplicationEvent(RedisKeyspaceEvent event) {
        // just a customization hook
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.eventPublisher = applicationContext;
    }

    private void initMessageListenerContainer() {
        RedisDbProperties redisDbProperties = BeanFactoryProvider.getBean(RedisDbProperties.class);
        String defaultConfig = Objects.requireNonNull(redisDbProperties.getDefaultConfig(), "Redis默认标识不可为空");
        for (Map.Entry<String, RedisProperties> entry : redisDbProperties.getConfig().entrySet()) {
            String key = entry.getKey();
            RedisMessageListenerContainer messageListenerContainer = new RedisMessageListenerContainer();
            messageListenerContainer.setConnectionFactory(BeanFactoryProvider.getBean(key + "RedisConnectionFactory", RedisConnectionFactory.class));
            messageListenerContainer.afterPropertiesSet();
            messageListenerContainer.start();
            if (defaultConfig.equals(key)) {
                this.messageListenerContainer = messageListenerContainer;
            }
            BeanFactoryProvider.registerSingleton(key + "RedisMessageListenerContainer", messageListenerContainer);
        }
    }

    private void initKeyExpirationListener() {

        if (this.expirationListener.get() == null) {

            RedisDbKeyValueAdapter.MappingExpirationListener listener = new RedisDbKeyValueAdapter.MappingExpirationListener(this.messageListenerContainer, this.redisOps,
                    this.converter);

            listener.setKeyspaceNotificationsConfigParameter(keyspaceNotificationsConfigParameter);

            if (this.eventPublisher != null) {
                listener.setApplicationEventPublisher(this.eventPublisher);
            }

            if (this.expirationListener.compareAndSet(null, listener)) {
                listener.init();
            }
        }
    }

    /**
     * {@link MessageListener} implementation used to capture Redis keyspace notifications. Tries to read a previously
     * created phantom key {@code keyspace:id:phantom} to provide the expired object as part of the published
     * {@link RedisKeyExpiredEvent}.
     *
     * @author Christoph Strobl
     * @since 1.7
     */
    static class MappingExpirationListener extends KeyExpirationEventMessageListener {

        private final RedisOperations<?, ?> ops;
        private final RedisConverter converter;

        /**
         * Creates new {@link RedisDbKeyValueAdapter.MappingExpirationListener}.
         */
        MappingExpirationListener(RedisMessageListenerContainer listenerContainer, RedisOperations<?, ?> ops,
                                  RedisConverter converter) {

            super(listenerContainer);
            this.ops = ops;
            this.converter = converter;
        }

        @Override
        public void onMessage(Message message, @Nullable byte[] pattern) {

            if (!isKeyExpirationMessage(message)) {
                return;
            }

            byte[] key = message.getBody();

            byte[] phantomKey = ByteUtils.concat(key,
                    converter.getConversionService().convert(KeyspaceIdentifier.PHANTOM_SUFFIX, byte[].class));

            Map<byte[], byte[]> hash = ops.execute((RedisCallback<Map<byte[], byte[]>>) connection -> {

                Map<byte[], byte[]> phantomValue = connection.hGetAll(phantomKey);

                if (!CollectionUtils.isEmpty(phantomValue)) {
                    connection.del(phantomKey);
                }

                return phantomValue;
            });

            Object value = CollectionUtils.isEmpty(hash) ? null : converter.read(Object.class, new RedisData(hash));

            byte[] channelAsBytes = message.getChannel();

            String channel = !ObjectUtils.isEmpty(channelAsBytes)
                    ? converter.getConversionService().convert(channelAsBytes, String.class)
                    : null;

            RedisKeyExpiredEvent<?> event = new RedisKeyExpiredEvent<>(channel, key, value);

            ops.execute((RedisCallback<Void>) connection -> {

                connection.sRem(converter.getConversionService().convert(event.getKeyspace(), byte[].class), event.getId());
                new IndexDbWriter(connection, converter).removeKeyFromIndexes(event.getKeyspace(), event.getId());
                return null;
            });

            publishEvent(event);
        }

        private boolean isKeyExpirationMessage(Message message) {
            return BinaryKeyspaceIdentifier.isValid(message.getBody());
        }
    }

    private boolean keepShadowCopy() {

        return switch (shadowCopy) {
            case OFF -> false;
            case ON -> true;
            default -> this.expirationListener.get() != null;
        };
    }

    /**
     * @author Christoph Strobl
     * @since 1.8
     */
    public enum EnableKeyspaceEvents {

        /**
         * Initializes the {@link KeyExpirationEventMessageListener} on startup.
         */
        ON_STARTUP,

        /**
         * Initializes the {@link KeyExpirationEventMessageListener} on first insert having expiration time set.
         */
        ON_DEMAND,

        /**
         * Turn {@link KeyExpirationEventMessageListener} usage off. No expiration events will be received.
         */
        OFF
    }

    /**
     * Configuration flag controlling storage of phantom keys (shadow copies) of expiring entities to read them later when
     * publishing {@link RedisKeyspaceEvent}.
     *
     * @author Christoph Strobl
     * @since 2.4
     */
    public enum ShadowCopy {

        /**
         * Store shadow copies of expiring entities depending on the {@link RedisDbKeyValueAdapter.EnableKeyspaceEvents}.
         */
        DEFAULT,

        /**
         * Store shadow copies of expiring entities.
         */
        ON,

        /**
         * Do not store shadow copies.
         */
        OFF
    }

    /**
     * Container holding update information like fields to remove from the Redis Hash.
     *
     * @author Christoph Strobl
     */
    static class RedisUpdateObject {

        private final String keyspace;
        private final Object targetId;
        private final byte[] targetKey;

        private final Set<byte[]> fieldsToRemove = new LinkedHashSet<>();
        private final Set<RedisDbKeyValueAdapter.RedisUpdateObject.Index> indexesToUpdate = new LinkedHashSet<>();

        RedisUpdateObject(byte[] targetKey, String keyspace, Object targetId) {

            this.targetKey = targetKey;
            this.keyspace = keyspace;
            this.targetId = targetId;
        }

        void addFieldToRemove(byte[] field) {
            fieldsToRemove.add(field);
        }

        void addIndexToUpdate(RedisDbKeyValueAdapter.RedisUpdateObject.Index index) {
            indexesToUpdate.add(index);
        }

        static class Index {

            final DataType type;
            final byte[] key;

            public Index(byte[] key, DataType type) {
                this.key = key;
                this.type = type;
            }
        }
    }
}
