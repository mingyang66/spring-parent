package com.emily.infrastructure.language.i18n.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注需要做多语言支持的字段，标记到Map集合上时优先级低于{@link I18nMapProperty}
 * 1. 标记到实体类字符串上
 * <pre>{@code
 * @I18nModel
 * public class Teacher {
 *     @I18nProperty
 *     private String name;
 *     }
 * }</pre>
 * 2. 标记到实体类List字符串上
 * <pre>{@code
 *     @I18nProperty
 *     private List<String> stringList;
 * }</pre>
 * 3. 标记到实体类Map<String,String>上</>
 * <pre>{@code
 * @I18nModel
 * public class Teacher {
 *
 *     @I18nProperty
 *     private Map<String,String> names;
 * }
 * }</pre>
 * 4. 标记到实体类字符串数组上
 * <pre>{@code
 * @I18nModel
 * public class Teacher {
 *     @I18nProperty
 *     private String[] names;
 * }
 * }</pre>
 *
 * @author Emily
 * @since Created in 2023/4/15 11:17 PM
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface I18nProperty {
}
