package com.emily.infrastructure.test.test.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author :  Emily
 * @since :  2023/8/18 10:26 AM
 */
public class MyValidator implements ConstraintValidator<MyValidation, String> {
    @Override
    public void initialize(MyValidation constraintAnnotation) {
        // 初始化操作
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 自定义的参数校验逻辑
        // 返回 true 表示校验通过，返回 false 表示校验失败
        return true;
    }
}