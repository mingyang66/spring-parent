package com.yaomy.control.test.consul;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletContextInitializerBeans;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Collection;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/11/20
 */
public class TestDoubleColon {
    private org.springframework.boot.web.servlet.ServletContextInitializer getSelfInitializer() {
        return this::selfInitialize;
    }

    private void selfInitialize(ServletContext servletContext) throws ServletException {
        WebApplicationContextUtils.registerEnvironmentBeans(getBeanFactory(), servletContext);
        for (ServletContextInitializer beans : getServletContextInitializerBeans()) {
            beans.onStartup(servletContext);
        }
    }
    protected Collection<ServletContextInitializer> getServletContextInitializerBeans() {
        return new ServletContextInitializerBeans(getBeanFactory());
    }
    public final ConfigurableListableBeanFactory getBeanFactory() {
        return new DefaultListableBeanFactory();
    }
    public void get(ServletContextInitializerBeans...initializerBeans){
        System.out.println(initializerBeans.length);
    }

    public static void main(String[] args) {
        TestDoubleColon testDoubleColon = new TestDoubleColon();
    }
}
