package com.emily.infrastructure.autoconfigure.response.annotation;

import java.lang.annotation.*;

/**
 * @author Emily
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiWrapperIgnore {

}
