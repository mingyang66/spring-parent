package com.emily.infrastructure.sensitive.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，标注在属性上，字段属性值置为null
 * ---------------------------------------------
 * 生效规则：
 * 1.非int、double、float、byte、short、long、boolean、char八种基本数据类型字段才会生效；
 * 2.任何引用类型字段的值都会被设置为null,且优先级最高；
 * ---------------------------------------------
 *
 * @author Emily
 * @since :  Created in 2023/7/14 5:22 下午
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DesensitizeNullProperty {

}
