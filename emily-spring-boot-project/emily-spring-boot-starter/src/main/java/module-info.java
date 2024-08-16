/**
 * @author :  Emily
 * @since :  2024/6/14 下午5:55
 */
module emily.spring.boot.starter {
    requires org.apache.commons.lang3;
    requires spring.boot;
    requires emily.spring.boot.core;
    requires org.slf4j;
    requires spring.beans;
    requires spring.boot.autoconfigure;
    requires spring.core;
    requires spring.webmvc;
    requires spring.web;
    requires jsr305;
    requires spring.context;
    requires com.google.common;
    requires spring.aop;
    requires oceansky.common;
    requires oceansky.sensitive;
    requires oceansky.json;
    requires oceansky.logger;
    requires oceansky.date;
    requires jakarta.annotation;
    requires emily.spring.boot.logger;
    requires otter.spring.servlet;
    requires tomcat.embed.core;

    exports com.emily.infrastructure.autoconfigure.bean.factory;
    exports com.emily.infrastructure.autoconfigure.bean.registry;
    exports com.emily.infrastructure.autoconfigure.listener;

}
