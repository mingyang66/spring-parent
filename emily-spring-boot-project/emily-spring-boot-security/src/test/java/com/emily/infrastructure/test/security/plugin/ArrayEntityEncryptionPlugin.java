package com.emily.infrastructure.test.security.plugin;

import com.emily.infrastructure.security.plugin.SecurityPlugin;
import com.emily.infrastructure.test.security.entity.ArrayEntity;

/**
 * @author :  Emily
 * @since :  2025/4/22 下午1:51
 */
public class ArrayEntityEncryptionPlugin implements SecurityPlugin<ArrayEntity, Object> {
    @Override
    public Object getPlugin(ArrayEntity entity, Object value) {
        if (value instanceof String[] strings) {
            return new String[]{strings[0] + "-加密后", strings[1] + "-加密后"};
        }
        return value + "加密后";
    }
}
