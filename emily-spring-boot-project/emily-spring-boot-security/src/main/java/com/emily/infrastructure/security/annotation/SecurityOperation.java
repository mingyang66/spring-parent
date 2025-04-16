package com.emily.infrastructure.security.annotation;

import com.emily.infrastructure.security.type.SecurityType;

import java.lang.annotation.*;

/**
 * 标记在控制器方法，标识对请求入参、响应数据进行解密、加密
 *
 * @author :  Emily
 * @since :  2025/2/7 下午7:31
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SecurityOperation {
    /**
     * 加解密类型，默认参数解密
     */
    SecurityType[] value() default SecurityType.PARAM_DECRYPTION;

    /**
     * 外层包装类不进行条件判断、不进行加解密处理、只对内层数据进行处理
     */
    Class<?>[] removePackClass() default void.class;
}
