package com.emily.infrastructure.test.security.entity;

import com.emily.infrastructure.security.annotation.SecurityModel;
import com.emily.infrastructure.security.annotation.SecurityProperty;
import com.emily.infrastructure.test.security.plugin.ArrayEntityEncryptionPlugin;

/**
 * @author :  姚明洋
 * @since :  2025/4/22 下午1:48
 */
@SecurityModel
public class ArrayEntity {
    public Integer[] integers;
    @SecurityProperty(ArrayEntityEncryptionPlugin.class)
    public String[] usernames;
    @SecurityProperty(ArrayEntityEncryptionPlugin.class)
    public ArraySubEntity[] arraySubEntity;
}
