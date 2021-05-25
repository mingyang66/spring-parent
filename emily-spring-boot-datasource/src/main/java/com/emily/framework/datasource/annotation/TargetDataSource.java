package com.emily.framework.datasource.annotation;

import com.emily.framework.datasource.DataSourceProperties;

import java.lang.annotation.*;

/**
 * @Description: 自定义注解，切换数据源,默认数据源为主数据源
 * @Author Emily
 * @Version: 1.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
    String value() default DataSourceProperties.DEFAULT_CONFIG;
}

