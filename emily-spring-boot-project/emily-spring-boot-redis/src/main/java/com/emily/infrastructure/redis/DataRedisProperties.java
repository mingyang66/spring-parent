package com.emily.infrastructure.redis;

import java.time.Duration;

/**
 * 自定义属性配置
 *
 * @author :  Emily
 * @since :  2023/10/25 11:11 PM
 */
public class DataRedisProperties extends org.springframework.boot.data.redis.autoconfigure.DataRedisProperties {
    /**
     * 基于jedis连接配置
     */
    private Jedis jedis = new Jedis();
    /**
     * 基于lettuce连接配置
     */
    private Lettuce lettuce = new Lettuce();

    @Override
    public Jedis getJedis() {
        return jedis;
    }

    public void setJedis(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public Lettuce getLettuce() {
        return lettuce;
    }

    public void setLettuce(Lettuce lettuce) {
        this.lettuce = lettuce;
    }

    public static class Pool extends org.springframework.boot.data.redis.autoconfigure.DataRedisProperties.Pool {
        /**
         * 对象在池中最小可空闲时间, 默认：30分钟
         * 它指定了一个对象在池中保持空闲的最小时间，超过这个时间后，如果池中的对象数量超过了BaseObjectPoolConfig.minIdle设置的最小空闲对象数量，就会触发空闲对象的逐出操作
         */
        private Duration minEvictableIdleDuration = Duration.ofMinutes(30);

        public Duration getMinEvictableIdleDuration() {
            return minEvictableIdleDuration;
        }

        public void setMinEvictableIdleDuration(Duration minEvictableIdleDuration) {
            this.minEvictableIdleDuration = minEvictableIdleDuration;
        }
    }

    public static class Jedis extends org.springframework.boot.data.redis.autoconfigure.DataRedisProperties.Jedis {
        /**
         * 连接池配置
         */
        private Pool pool = new Pool();

        @Override
        public Pool getPool() {
            return pool;
        }

        public void setPool(Pool pool) {
            this.pool = pool;
        }
    }

    public static class Lettuce extends org.springframework.boot.data.redis.autoconfigure.DataRedisProperties.Lettuce {
        /**
         * 是否开启连接校验，默认：false
         */
        private boolean validateConnection = false;
        /**
         * 是否开启共享本地物理连接，默认：true
         */
        private boolean shareNativeConnection = true;
        /**
         * 是否提前初始化连接，默认：false
         * 属性shareNativeConnection为true时才生效
         */
        private boolean eagerInitialization = false;
        /**
         * Lettuce pool configuration.
         */
        private Pool pool = new Pool();

        public boolean isValidateConnection() {
            return validateConnection;
        }

        public void setValidateConnection(boolean validateConnection) {
            this.validateConnection = validateConnection;
        }

        public boolean isShareNativeConnection() {
            return shareNativeConnection;
        }

        public void setShareNativeConnection(boolean shareNativeConnection) {
            this.shareNativeConnection = shareNativeConnection;
        }

        public boolean isEagerInitialization() {
            return eagerInitialization;
        }

        public void setEagerInitialization(boolean eagerInitialization) {
            this.eagerInitialization = eagerInitialization;
        }

        @Override
        public Pool getPool() {
            return pool;
        }

        public void setPool(Pool pool) {
            this.pool = pool;
        }
    }
}
