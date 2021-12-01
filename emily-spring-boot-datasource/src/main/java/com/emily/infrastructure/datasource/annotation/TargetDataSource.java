package com.emily.infrastructure.datasource.annotation;

import com.emily.infrastructure.datasource.DataSourceProperties;

import java.lang.annotation.*;

/**
 * @Description: 自定义注解，切换数据源,默认数据源为主数据源
 * @Inherited 注解允许标注的注解标注在类上时其子类可以继承注解，如果标注在非类上在继承作用无效
 * @Author Emily
 * @Version: 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TargetDataSource {
    String value() default DataSourceProperties.DEFAULT_CONFIG;
}

