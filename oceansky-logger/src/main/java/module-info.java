/**
 * @author :  Emily
 * @since :  2024/6/14 下午2:00
 */
module oceansky.logger {
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires org.slf4j;
    uses com.emily.infrastructure.logback.configuration.context.Context;
    exports com.emily.infrastructure.logback;
    exports com.emily.infrastructure.logback.factory;
    exports com.emily.infrastructure.logback.common;
    exports com.emily.infrastructure.logback.configuration.context;
}