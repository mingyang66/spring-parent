package com.emily.infrastructure.language.convert;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description :  标注实体类是否需要进行多语言解析
 * @Author :  Emily
 * @CreateDate :  Created in 2023/4/15 5:15 PM
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiI18n {
}
