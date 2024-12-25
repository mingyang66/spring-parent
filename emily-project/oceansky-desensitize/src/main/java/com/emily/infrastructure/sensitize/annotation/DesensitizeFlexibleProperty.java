package com.emily.infrastructure.sensitize.annotation;

import com.emily.infrastructure.sensitize.DesensitizeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义jackson注解，标注在属性上，实例如下：
 * <pre>{@code
 * @DesensitizeModel
 * public class JsonRequest extends Animal {
 *     @DesensitizeFlexibleProperty(value = {"email", "phone"}, target = "fieldValue", desensitizeType = {DesensitizeType.EMAIL, DesensitizeType.PHONE})
 *     private String fieldKey;
 *     private String fieldValue;
 *     @DesensitizeFlexibleProperty(value = {"email", "phone"}, target = "fieldValue1")
 *     private String fieldKey1;
 *     private String fieldValue1;
 *     }
 * }</pre>
 *
 * @author Emily
 * @since :  Created in 2022/7/19 10:22 下午
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DesensitizeFlexibleProperty {
    /**
     * 要隐藏的参数key名称
     *
     * @return 复杂类型字段名
     */
    String[] value() default {};

    /**
     * 要隐藏的参数值的key名称
     *
     * @return 值字段名
     */
    String target();

    /**
     * 脱敏类型，见枚举类型{@link DesensitizeType}
     *
     * @return 脱敏类型
     */
    DesensitizeType[] desensitizeType() default {};
}
