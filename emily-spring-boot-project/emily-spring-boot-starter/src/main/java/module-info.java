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
    requires org.apache.commons.io;
    requires tomcat.embed.core;
    requires jakarta.annotation;
    requires emily.spring.boot.logger;
    requires oceansky.language;

    exports com.emily.infrastructure.autoconfigure.bean.factory;
    exports com.emily.infrastructure.autoconfigure.bean.registry;
    exports com.emily.infrastructure.autoconfigure.httpclient.annotation;
    exports com.emily.infrastructure.autoconfigure.httpclient;
    exports com.emily.infrastructure.autoconfigure.httpclient.handler;
    exports com.emily.infrastructure.autoconfigure.exception;
    exports com.emily.infrastructure.autoconfigure.exception.entity;
    exports com.emily.infrastructure.autoconfigure.exception.type;
    exports com.emily.infrastructure.autoconfigure.exception.handler;
    exports com.emily.infrastructure.autoconfigure.listener;
    exports com.emily.infrastructure.autoconfigure.request;
    exports com.emily.infrastructure.autoconfigure.request.interceptor;
    exports com.emily.infrastructure.autoconfigure.response;
    exports com.emily.infrastructure.autoconfigure.response.handler;
    exports com.emily.infrastructure.autoconfigure.response.annotation;
    exports com.emily.infrastructure.autoconfigure.servlet;
    exports com.emily.infrastructure.autoconfigure.servlet.annotation;
    exports com.emily.infrastructure.autoconfigure.servlet.interceptor;
    exports com.emily.infrastructure.autoconfigure.valid;
    exports com.emily.infrastructure.autoconfigure.valid.annotation;

}
