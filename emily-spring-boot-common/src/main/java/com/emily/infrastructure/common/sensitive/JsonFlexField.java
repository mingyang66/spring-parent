package com.emily.infrastructure.common.sensitive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description :  自定义jackson注解，标注在属性上，实例如下：
 * @JsonSensitive(include = true)
 * public class JsonRequest {
 * @JsonFlexField(fieldNames = {"email","phone"}, fieldValue = "fieldValue", types = {SensitiveType.EMAIL, SensitiveType.PHONE})
 * private String fieldKey;
 * private String fieldValue;
 * @JsonFlexField(fieldNames = {"email","phone"}, fieldValue = "fieldValue1")
 * private String fieldKey1;
 * }
 * @Author :  Emily
 * @CreateDate :  Created in 2022/7/19 5:22 下午
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonFlexField {
    /**
     * 要隐藏的参数key名称
     *
     * @return
     */
    String[] fieldNames() default {};

    /**
     * 要隐藏的参数值的key名称
     *
     * @return
     */
    String fieldValue();

    /**
     * 脱敏类型，见枚举类型{@link SensitiveType}
     *
     * @return
     */
    SensitiveType[] types() default {};
}
