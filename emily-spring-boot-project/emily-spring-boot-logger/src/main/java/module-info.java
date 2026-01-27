/**
 * @author :  Emily
 * @since :  2024/6/14 下午5:40
 */
open module emily.spring.boot.logger {
    requires spring.boot;
    requires spring.context;
    requires spring.core;
    requires org.slf4j;
    requires spring.beans;
    requires spring.boot.autoconfigure;
    requires emily.logback;
    requires emily.json;
    requires org.jspecify;

    exports com.emily.infrastructure.logger;
    exports com.emily.infrastructure.logger.initializer;
    exports com.emily.infrastructure.logger.utils;
}