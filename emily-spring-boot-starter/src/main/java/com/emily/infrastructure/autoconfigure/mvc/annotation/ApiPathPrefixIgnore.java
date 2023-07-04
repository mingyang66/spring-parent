package com.emily.infrastructure.autoconfigure.mvc.annotation;

import java.lang.annotation.*;

/**
 * 控制器API前缀添加注解忽略注解
 *
 * @author Emily
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiPathPrefixIgnore {

}
