package com.emily.infrastructure.autoconfigure.valid.annotation;

import com.emily.infrastructure.autoconfigure.valid.IsAccountCodeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 判断是否符合判定条件的账号类型，如果为空则不校验
 * 1. ElementType.ANNOTATION_TYPE 用户其它约束的约束注解
 * 2. ElementType.FIELD 受约束的属性字段
 * 3. ElementType.PARAMETER 用于受约束的方法和构造函数参数
 * 4. ElementType.METHOD 用于受约束的getter和受约束的方法返回值
 * <pre>{@code
 * // 实体类
 * public class AccountCodeEntity {
 *     @IsAccountCode(minLength = 8, maxLength = 10, prefixes = {"10","20"}, suffixes = {"99"}, type = Long.class)
 *     public String accountCode;
 * }
 * }</pre>
 *
 * @author :  Emily
 * @since :  2023/12/24 1:35 PM
 */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {IsAccountCodeValidator.class})
public @interface IsAccountCode {
    /**
     * 提示信息
     *
     * @return 提示信息
     */
    String message() default "非法账号";

    /**
     * 校验分组
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 前缀
     *
     * @return 前缀
     */
    String[] prefixes() default {};

    /**
     * 后缀
     *
     * @return 后缀
     */
    String[] suffixes() default {};

    /**
     * 最小长度
     *
     * @return 最小长度
     */
    int minLength() default 0;

    /**
     * 最大长度
     *
     * @return 最大长度
     */
    int maxLength() default 0;

    /**
     * 账号类型，如：Long.class, String.class, Integer.class
     */
    Class<?> type() default String.class;
}
