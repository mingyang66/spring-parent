/**
 * @author :  Emily
 * @since :  2024/6/25 下午10:28
 */
module emily.spring.boot.redis {
    requires jakarta.annotation;
    requires spring.context;
    requires spring.core;
    requires spring.boot;
    requires emily.spring.boot.core;
    requires spring.boot.autoconfigure;
    requires spring.data.redis;
    requires reactor.core;
    requires org.apache.commons.pool2;
    requires spring.beans;
    requires redis.clients.jedis;
    requires lettuce.core;
    requires oceansky.date;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires oceansky.common;
    requires oceansky.json;
    requires oceansky.logger;
    requires org.slf4j;
    exports com.emily.infrastructure.redis.factory;
    exports com.emily.infrastructure.redis.common;
}