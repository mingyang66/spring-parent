package com.emily.infrastructure.autoconfigure.response.annotation;

import java.lang.annotation.*;

/**
 * 标记控制器方法忽略返回值包装注解
 *
 * @author Emily
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiResponsePackIgnore {

}
