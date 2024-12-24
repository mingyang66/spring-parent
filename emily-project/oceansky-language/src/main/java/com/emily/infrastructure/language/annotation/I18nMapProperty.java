package com.emily.infrastructure.language.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注需要做多语言支持的Map字段；
 * 标记到Map上时优先级高于{@link I18nProperty}
 * <pre>{@code
 * @I18nModel
 * public class Teacher {
 * 		//优先级高于 @I18nProperty注解
 *     @I18nProperty
 *     @I18nMapProperty(value = {"test1"})
 *     private Map<String, Object> mapObj;
 * }
 * }</pre>
 *
 * @author Emily
 * @since Created in 2023/4/15 5:17 PM
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface I18nMapProperty {
    /**
     * 期望做翻译的key值
     */
    String[] value() default {};
}
