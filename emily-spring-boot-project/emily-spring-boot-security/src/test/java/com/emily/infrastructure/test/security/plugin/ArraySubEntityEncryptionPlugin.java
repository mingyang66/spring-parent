package com.emily.infrastructure.test.security.plugin;

import com.emily.infrastructure.security.plugin.SecurityPlugin;
import com.emily.infrastructure.test.security.entity.ArraySubEntity;

/**
 * @author :  Emily
 * @since :  2025/4/22 下午1:51
 */
public class ArraySubEntityEncryptionPlugin implements SecurityPlugin<ArraySubEntity, String> {
    @Override
    public String getPlugin(ArraySubEntity entity, String value) {
        return value + "-加密后";
    }
}
