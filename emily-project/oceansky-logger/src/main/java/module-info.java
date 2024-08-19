import com.emily.infrastructure.logback.configuration.spi.ContextProvider;
import com.emily.infrastructure.logback.configuration.spi.ContextServiceProvider;

/**
 * @author :  Emily
 * @since :  2024/6/14 下午2:00
 */
open module oceansky.logger {
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires org.slf4j;
    uses ContextProvider;
    provides ContextProvider with ContextServiceProvider;
    exports com.emily.infrastructure.logback;
    exports com.emily.infrastructure.logback.factory;
    exports com.emily.infrastructure.logback.common;
    exports com.emily.infrastructure.logback.configuration.context;
    exports com.emily.infrastructure.logback.configuration.type;
    exports com.emily.infrastructure.logback.configuration.spi;
    exports com.emily.infrastructure.logback.entity;
}