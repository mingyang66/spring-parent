package com.emily.infrastructure.redis.repository;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.convert.*;
import org.springframework.data.redis.util.ByteUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Set;

/**
 * @author :  姚明洋
 * @since :  2024/7/8 上午11:20
 */
public class IndexDbWriter {

    private final RedisConnection connection;
    private final RedisConverter converter;

    /**
     * Creates new {@link IndexDbWriter}.
     *
     * @param keyspace   The key space to write index values to. Must not be {@literal null}.
     * @param connection must not be {@literal null}.
     * @param converter  must not be {@literal null}.
     */
    public IndexDbWriter(RedisConnection connection, RedisConverter converter) {

        Assert.notNull(connection, "RedisConnection cannot be null");
        Assert.notNull(converter, "RedisConverter cannot be null");

        this.connection = connection;
        this.converter = converter;
    }

    /**
     * Initially creates indexes.
     *
     * @param key         must not be {@literal null}.
     * @param indexValues can be {@literal null}.
     */
    public void createIndexes(Object key, Iterable<IndexedData> indexValues) {
        createOrUpdateIndexes(key, indexValues, IndexDbWriter.IndexWriteMode.CREATE);
    }

    /**
     * Updates indexes by first removing key from existing one and then persisting new index data.
     *
     * @param key         must not be {@literal null}.
     * @param indexValues can be {@literal null}.
     */
    public void updateIndexes(Object key, Iterable<IndexedData> indexValues) {
        createOrUpdateIndexes(key, indexValues, IndexDbWriter.IndexWriteMode.PARTIAL_UPDATE);
    }

    /**
     * Updates indexes by first removing key from existing one and then persisting new index data.
     *
     * @param key         must not be {@literal null}.
     * @param indexValues can be {@literal null}.
     */
    public void deleteAndUpdateIndexes(Object key, @Nullable Iterable<IndexedData> indexValues) {
        createOrUpdateIndexes(key, indexValues, IndexDbWriter.IndexWriteMode.UPDATE);
    }

    private void createOrUpdateIndexes(Object key, @Nullable Iterable<IndexedData> indexValues,
                                       IndexDbWriter.IndexWriteMode writeMode) {

        Assert.notNull(key, "Key must not be null");
        if (indexValues == null) {
            return;
        }

        byte[] binKey = toBytes(key);

        if (ObjectUtils.nullSafeEquals(IndexDbWriter.IndexWriteMode.UPDATE, writeMode)) {

            if (indexValues.iterator().hasNext()) {
                IndexedData data = indexValues.iterator().next();
                if (data != null) {
                    removeKeyFromIndexes(data.getKeyspace(), binKey);
                }
            }
        } else if (ObjectUtils.nullSafeEquals(IndexDbWriter.IndexWriteMode.PARTIAL_UPDATE, writeMode)) {
            removeKeyFromExistingIndexes(binKey, indexValues);
        }

        addKeyToIndexes(binKey, indexValues);
    }

    /**
     * Removes a key from all available indexes.
     *
     * @param key must not be {@literal null}.
     */
    public void removeKeyFromIndexes(String keyspace, Object key) {

        Assert.notNull(key, "Key must not be null");

        byte[] binKey = toBytes(key);
        byte[] indexHelperKey = ByteUtils.concatAll(toBytes(keyspace + ":"), binKey, toBytes(":idx"));

        for (byte[] indexKey : connection.sMembers(indexHelperKey)) {

            DataType type = connection.type(indexKey);
            if (DataType.ZSET.equals(type)) {
                connection.zRem(indexKey, binKey);
            } else {
                connection.sRem(indexKey, binKey);
            }
        }

        connection.del(indexHelperKey);
    }

    /**
     * Removes all indexes.
     */
    public void removeAllIndexes(String keyspace) {

        Set<byte[]> potentialIndex = connection.keys(toBytes(keyspace + ":*"));

        if (!potentialIndex.isEmpty()) {
            connection.del(potentialIndex.toArray(new byte[potentialIndex.size()][]));
        }
    }

    private void removeKeyFromExistingIndexes(byte[] key, Iterable<IndexedData> indexValues) {

        for (IndexedData indexData : indexValues) {
            removeKeyFromExistingIndexes(key, indexData);
        }
    }

    /**
     * Remove given key from all indexes matching {@link IndexedData#getIndexName()}:
     *
     * @param key
     * @param indexedData
     */
    protected void removeKeyFromExistingIndexes(byte[] key, IndexedData indexedData) {

        Assert.notNull(indexedData, "IndexedData must not be null");

        Set<byte[]> existingKeys = connection
                .keys(toBytes(indexedData.getKeyspace() + ":" + indexedData.getIndexName() + ":*"));

        if (!CollectionUtils.isEmpty(existingKeys)) {
            for (byte[] existingKey : existingKeys) {

                if (indexedData instanceof GeoIndexedPropertyValue) {
                    connection.geoRemove(existingKey, key);
                } else {
                    connection.sRem(existingKey, key);
                }
            }
        }
    }

    private void addKeyToIndexes(byte[] key, Iterable<IndexedData> indexValues) {

        for (IndexedData indexData : indexValues) {
            addKeyToIndex(key, indexData);
        }
    }

    /**
     * Adds a given key to the index for {@link IndexedData#getIndexName()}.
     *
     * @param key         must not be {@literal null}.
     * @param indexedData must not be {@literal null}.
     */
    protected void addKeyToIndex(byte[] key, IndexedData indexedData) {

        Assert.notNull(key, "Key must not be null");
        Assert.notNull(indexedData, "IndexedData must not be null");

        if (indexedData instanceof RemoveIndexedData) {
            return;
        }

        if (indexedData instanceof SimpleIndexedPropertyValue propertyValue) {

            Object value = propertyValue.getValue();

            if (value == null) {
                return;
            }

            byte[] indexKey = toBytes(indexedData.getKeyspace() + ":" + indexedData.getIndexName() + ":");
            indexKey = ByteUtils.concat(indexKey, toBytes(value));
            connection.sAdd(indexKey, key);

            // keep track of indexes used for the object
            connection.sAdd(ByteUtils.concatAll(toBytes(indexedData.getKeyspace() + ":"), key, toBytes(":idx")), indexKey);
        } else if (indexedData instanceof GeoIndexedPropertyValue propertyValue) {

            Object value = propertyValue.getValue();
            if (value == null) {
                return;
            }

            byte[] indexKey = toBytes(indexedData.getKeyspace() + ":" + indexedData.getIndexName());
            connection.geoAdd(indexKey, propertyValue.getPoint(), key);

            // keep track of indexes used for the object
            connection.sAdd(ByteUtils.concatAll(toBytes(indexedData.getKeyspace() + ":"), key, toBytes(":idx")), indexKey);
        } else {
            throw new IllegalArgumentException(
                    String.format("Cannot write index data for unknown index type %s", indexedData.getClass()));
        }
    }

    private byte[] toBytes(@Nullable Object source) {

        if (source == null) {
            return new byte[]{};
        }

        if (source instanceof byte[] bytes) {
            return bytes;
        }

        if (converter.getConversionService().canConvert(source.getClass(), byte[].class)) {
            return converter.getConversionService().convert(source, byte[].class);
        }

        throw new InvalidDataAccessApiUsageException(String.format(
                "Cannot convert %s to binary representation for index key generation; "
                        + "Are you missing a Converter; Did you register a non PathBasedRedisIndexDefinition that might apply to a complex type",
                source.getClass()));
    }

    /**
     * @author Christoph Strobl
     * @since 1.8
     */
    private enum IndexWriteMode {

        CREATE, UPDATE, PARTIAL_UPDATE
    }
}
