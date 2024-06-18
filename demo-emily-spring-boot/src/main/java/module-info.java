/**
 * @author :  Emily
 * @since :  2024/6/14 下午6:45
 */
module demo.emily.spring.boot {
    requires jakarta.validation;
    requires oceansky.common;
    requires org.apache.tomcat.embed.core;
    requires emily.spring.boot.core;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires com.google.common;
    requires oceansky.json;
    requires spring.beans;
    requires emily.spring.boot.starter;
    requires org.hibernate.validator;
    requires spring.web;
    requires oceansky.date;
    requires oceansky.sensitive;
    requires com.fasterxml.jackson.databind;
    requires org.mybatis;
    requires com.github.benmanes.caffeine;
    requires oceansky.language;
    requires spring.plugin.core;
    requires emily.spring.boot.redis;
    requires org.apache.commons.lang3;
    requires spring.core;
    requires spring.data.redis;
    requires spring.tx;
    requires otp.java;
    requires googleauth;
    requires com.eatthepath.otp;
    requires oceansky.logger;
    requires org.slf4j;
    requires oceansky.captcha;
    requires jsr305;
    requires spring.aop;
    requires emily.spring.boot.datasource;
}
