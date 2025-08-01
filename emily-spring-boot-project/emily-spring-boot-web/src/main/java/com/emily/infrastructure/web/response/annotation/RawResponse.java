package com.emily.infrastructure.web.response.annotation;

import java.lang.annotation.*;

/**
 * 标记返回原始返回值
 *
 * @author Emily
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RawResponse {

}
