package com.yaomy.control.aop.annotation;

import com.yaomy.control.aop.constant.DbType;

import java.lang.annotation.*;

/**
 * @Description: 自定义注解，切换数据源,默认主数据源primary
 * @Version: 1.0
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetDataSource {
    String value() default DbType.DEFAULT_DATASOURCE;
}



