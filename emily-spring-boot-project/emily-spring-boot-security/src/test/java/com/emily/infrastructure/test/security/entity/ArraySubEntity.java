package com.emily.infrastructure.test.security.entity;

import com.emily.infrastructure.security.annotation.SecurityModel;
import com.emily.infrastructure.security.annotation.SecurityProperty;
import com.emily.infrastructure.test.security.plugin.ArraySubEntityEncryptionPlugin;

/**
 * @author :  姚明洋
 * @since :  2025/4/22 下午1:49
 */
@SecurityModel
public class ArraySubEntity {
    @SecurityProperty(ArraySubEntityEncryptionPlugin.class)
    public String username;
}
