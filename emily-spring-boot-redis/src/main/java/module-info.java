/**
 * @author :  Emily
 * @since :  2024/6/17 下午3:17
 */
module emily.spring.boot.redis {
    requires spring.core;
    requires spring.context;
    requires spring.boot.autoconfigure;
    requires emily.spring.boot.core;
    requires spring.data.redis;
    requires reactor.core;
    requires lettuce.core;
    requires org.apache.commons.pool2;
    requires spring.beans;
    requires spring.boot;
    requires redis.clients.jedis;
    requires oceansky.date;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires oceansky.common;
    requires oceansky.json;
    requires oceansky.logger;
    requires org.slf4j;
    exports com.emily.infrastructure.redis.common;
    exports com.emily.infrastructure.redis.factory;
}