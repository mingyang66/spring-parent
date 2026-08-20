/**
 * @author :  Emily
 * @since :  2024/6/18 上午10:41
 */
module emily.logger.test {
    requires emily.logback;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires org.junit.jupiter.api;
    requires org.slf4j;
    exports com.logback.test;
    opens com.logback.test to org.junit.platform.commons;
}
