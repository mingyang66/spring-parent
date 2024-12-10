/**
 * @author :  Emily
 * @since :  2024/6/18 上午9:35
 */
module oceansky.sensitive.test {
    requires oceansky.sensitive;
    requires org.junit.jupiter.api;
    requires org.junit.platform.engine;
    requires org.junit.platform.commons;
    opens com.emily.infrastructure.sensitize.test.entity;
    exports com.emily.infrastructure.sensitize.test;
}