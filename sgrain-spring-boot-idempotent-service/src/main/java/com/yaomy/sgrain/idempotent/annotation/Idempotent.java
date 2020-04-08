package com.yaomy.sgrain.idempotent.annotation;

import java.lang.annotation.*;
/**
* @Description: 限制重复提交注解
* @create: 2020/3/26
*/
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
    /**
     * 启动幂等性功能
     * @return
     */
    boolean enable() default true;

    /**
     * 定义幂等性组件校验规则
     */
    Type type() default Type.TOKEN_AND_URL;

    /**
     * 枚举类 TOKEN 是指自定义令牌,通过接口/token/generation接口获取token令牌，并且通过验证token的有效性来判断是否重复提交；
     * TOKEN_AND_URL是通过令牌和URL组合的方式作为主键创建分布式锁的模式，这种模式适合用户已经登录，存在用户token令牌的模式
     */
    enum Type{
        TOKEN,TOKEN_AND_URL;
    }
}
