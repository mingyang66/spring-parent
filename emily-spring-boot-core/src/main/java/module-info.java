/**
 * @author :  Emily
 * @since :  2024/6/14 下午1:42
 */
module emily.spring.boot.core {
    requires spring.context;
    requires org.apache.commons.lang3;
    requires spring.core;
    requires org.apache.tomcat.embed.core;
    requires org.slf4j;
    requires spring.beans;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires transmittable.thread.local;
    requires oceansky.language;
    requires spring.web;
    requires spring.aop;
    requires com.google.common;
    requires org.apache.commons.io;
    requires oceansky.logger;
    requires oceansky.common;
    requires oceansky.json;
    requires oceansky.sensitive;

    exports com.emily.infrastructure.core.utils;
    exports com.emily.infrastructure.core.helper;
    exports com.emily.infrastructure.core.entity;
    exports com.emily.infrastructure.core.context;
    exports com.emily.infrastructure.core.constant;
    exports com.emily.infrastructure.core.aop.advisor;
    exports com.emily.infrastructure.core.aop.pointcut;
}