package com.sgrain.boot.autoconfigure.returnvalue.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiWrapperIgnore {

}
