package com.emily.infrastructure.common.sensitive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description :  Json脱敏标记，必须指定此标记，否则脱敏无效
 * @Author :  Emily
 * @CreateDate :  Created in 2022/11/4 11:23 上午
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonSerialize {
    /**
     * 序列化脱敏是否包含嵌套内部类
     *
     * @return
     */
    boolean include() default false;
}
