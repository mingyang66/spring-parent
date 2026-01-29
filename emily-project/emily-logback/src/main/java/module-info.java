/**
 * @author :  Emily
 * @since :  2024/6/14 下午2:00
 */
module emily.logback {
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires org.slf4j;
    requires java.sql;
    exports com.emily.infrastructure.logback;
    exports com.emily.infrastructure.logback.factory;
    exports com.emily.infrastructure.logback.common;
    exports com.emily.infrastructure.logback.configuration.context;
    exports com.emily.infrastructure.logback.configuration.type;
    exports com.emily.infrastructure.logback.entity;
}