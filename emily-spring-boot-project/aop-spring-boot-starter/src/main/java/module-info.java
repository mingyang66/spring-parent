/**
 * @author :  Emily
 * @since :  2024/6/14 下午1:42
 */
module emily.spring.boot.core {
    requires spring.context;
    requires spring.core;
    requires org.slf4j;
    requires spring.beans;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.aop;
    requires jakarta.annotation;

    exports com.emily.infrastructure.aop.advisor;
    exports com.emily.infrastructure.aop.pointcut;
}