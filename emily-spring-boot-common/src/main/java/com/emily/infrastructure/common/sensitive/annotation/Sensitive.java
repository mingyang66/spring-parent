package com.emily.infrastructure.common.sensitive.annotation;

import com.emily.infrastructure.common.sensitive.serializer.SensitiveJsonSerializer;
import com.emily.infrastructure.common.sensitive.strategy.SensitiveStrategy;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description :  自定义jackson注解，标注在属性上
 * @Author :  Emily
 * @CreateDate :  Created in 2022/7/19 5:22 下午
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveJsonSerializer.class)
public @interface Sensitive {
    /**
     * 脱敏策略
     */
    SensitiveStrategy strategy() default SensitiveStrategy.DEFAULT;
}
