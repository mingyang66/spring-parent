package com.emily.infrastructure.test.sensitive;

import java.lang.annotation.*;

/**
 * @Description :  方法脱敏
 * @Author :  Emily
 * @CreateDate :  Created in 2022/7/20 1:08 下午
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FiledSensitive {
    /**
     * 脱敏字段名
     */
    String[] fields();
}
