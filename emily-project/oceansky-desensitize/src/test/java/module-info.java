/**
 * @author :  Emily
 * @since :  2024/6/18 上午9:35
 */
module oceansky.sensitize.test {
    requires oceansky.sensitize;
    requires org.junit.jupiter.api;
    requires org.junit.platform.engine;
    requires org.junit.platform.commons;
    opens com.emily.infrastructure.desensitize.test.entity;
    exports com.emily.infrastructure.desensitize.test;
    exports com.emily.infrastructure.desensitize.test.plugin;
}