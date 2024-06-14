/**
 * @author :  Emily
 * @since :  2024/6/14 下午2:00
 */
module oceansky.logger {
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    exports com.emily.infrastructure.logger;
}