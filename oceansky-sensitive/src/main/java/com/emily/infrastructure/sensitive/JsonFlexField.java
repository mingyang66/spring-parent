package com.emily.infrastructure.sensitive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义jackson注解，标注在属性上，实例如下：
 * * @JsonSensitive(include = true)
 * * public class JsonRequest {
 * * @JsonFlexField(fieldNames = {"email","phone"}, fieldValue = "fieldValue", types = {SensitiveType.EMAIL, SensitiveType.PHONE})
 * * private String fieldKey;
 * * private String fieldValue;
 * * @JsonFlexField(fieldNames = {"email","phone"}, fieldValue = "fieldValue1")
 * * private String fieldKey1;
 * * }
 *
 * @author :  Emily
 * @since :  Created in 2022/7/19 5:22 下午
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonFlexField {
    /**
     * 要隐藏的参数key名称
     *
     * @return 复杂类型字段名
     */
    String[] fieldKeys() default {};

    /**
     * 要隐藏的参数值的key名称
     *
     * @return 值字段名
     */
    String fieldValue();

    /**
     * 脱敏类型，见枚举类型{@link SensitiveType}
     *
     * @return 脱敏类型
     */
    SensitiveType[] types() default {};
}
