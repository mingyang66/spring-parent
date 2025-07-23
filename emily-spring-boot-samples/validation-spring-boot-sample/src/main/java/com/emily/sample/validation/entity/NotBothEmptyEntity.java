package com.emily.sample.validation.entity;

import com.emily.infrastructure.validation.annotation.NotBothEmpty;

/**
 * @author :  Emily
 * @since :  2025/7/20 上午19:29
 */
@NotBothEmpty(value = {"username", "password"})
public class NotBothEmptyEntity {
    public String username;
    public String password;
}
