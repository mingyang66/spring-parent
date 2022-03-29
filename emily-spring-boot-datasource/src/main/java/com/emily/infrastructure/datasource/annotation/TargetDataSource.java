package com.emily.infrastructure.datasource.annotation;

import com.emily.infrastructure.datasource.DataSourceProperties;

import java.lang.annotation.*;

/**
 * 核心注解，用来切换数据源，可以用来标注在类上、接口上、类方法上、接口方法上
 *
 * @Inherited 注解允许标注的注解标注在类上时其子类可以继承注解，如果标注在非类上在继承作用无效
 * @Author Emily
 * @Version: 1.0
 * @since(4.0.6)
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TargetDataSource {
    /**
     * 指定要切换的数据库标识，默认是：default
     *
     * @return 要切换的数据库标识
     */
    String value() default DataSourceProperties.DEFAULT_CONFIG;
}

