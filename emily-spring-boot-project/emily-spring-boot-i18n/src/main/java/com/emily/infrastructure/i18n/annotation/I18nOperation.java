package com.emily.infrastructure.i18n.annotation;


import java.lang.annotation.*;

/**
 * I18N多语言标记，标记在实体类上
 * @author Emily
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface I18nOperation {

}

