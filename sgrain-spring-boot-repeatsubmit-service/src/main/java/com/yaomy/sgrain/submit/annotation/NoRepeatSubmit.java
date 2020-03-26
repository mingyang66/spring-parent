package com.yaomy.sgrain.submit.annotation;

import java.lang.annotation.*;
/**
* @Description: 限制重复提交注解
* @Author: 姚明洋
* @create: 2020/3/26
*/
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoRepeatSubmit {
    boolean enable() default true;
}
