/**
 * @author :  Emily
 * @since :  2024/6/14 下午5:55
 */
module emily.spring.boot.starter {
    requires jakarta.validation;
    requires org.apache.commons.lang3;
    requires spring.boot;
    requires emily.spring.boot.core;
    requires org.slf4j;
    requires spring.beans;
    requires spring.boot.autoconfigure;
    requires spring.core;
    requires spring.webmvc;
    requires org.apache.tomcat.embed.core;
    requires spring.web;
    requires jsr305;
    requires spring.context;
    requires com.google.common;
    requires spring.aop;
    requires oceansky.common;
    requires oceansky.sensitive;
    requires oceansky.language;
    requires oceansky.json;
    requires oceansky.logger;
    requires oceansky.date;

    exports com.emily.infrastructure.autoconfigure.valid.annotation;
    exports com.emily.infrastructure.autoconfigure.httpclient.annotation;
    exports com.emily.infrastructure.autoconfigure.response.annotation;
    exports com.emily.infrastructure.autoconfigure.exception.entity;
    exports com.emily.infrastructure.autoconfigure.exception.type;

}