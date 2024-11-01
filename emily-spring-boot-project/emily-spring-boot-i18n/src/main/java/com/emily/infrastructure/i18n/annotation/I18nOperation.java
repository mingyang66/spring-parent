package com.emily.infrastructure.i18n.annotation;


import com.emily.infrastructure.language.annotation.I18nModel;
import com.emily.infrastructure.language.annotation.I18nProperty;

import java.lang.annotation.*;

/**
 * I18N多语言标记，标记在具体方法上标识添加切面，和{@link I18nModel}、{@link I18nProperty}配合使用
 *
 * @author Emily
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface I18nOperation {

}

