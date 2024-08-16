/**
 * @author :  Emily
 * @since :  2024/6/14 下午1:42
 */
module emily.spring.boot.core {
    requires spring.context;
    requires org.apache.commons.lang3;
    requires spring.core;
    requires org.slf4j;
    requires spring.beans;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires transmittable.thread.local;
    requires spring.aop;
    requires oceansky.common;
    requires oceansky.json;
    requires oceansky.sensitive;
    requires com.google.common;
    requires tomcat.embed.core;
    requires spring.web;

    exports com.emily.infrastructure.core.helper;
    exports com.emily.infrastructure.core.context;
    exports com.emily.infrastructure.core.aop.advisor;
    exports com.emily.infrastructure.core.aop.pointcut;
    exports com.emily.infrastructure.core.context.holder;
    exports com.emily.infrastructure.core.condition;
    exports com.emily.infrastructure.core.context.ioc;
    exports com.emily.infrastructure.core.utils;
}