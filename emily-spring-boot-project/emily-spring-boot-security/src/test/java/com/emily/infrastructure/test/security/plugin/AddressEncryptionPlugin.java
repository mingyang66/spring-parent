package com.emily.infrastructure.test.security.plugin;

import com.emily.infrastructure.security.plugin.ComplexSecurityPlugin;
import com.emily.infrastructure.test.security.entity.Address;

/**
 * @author :  Emily
 * @since :  2025/2/8 下午4:34
 */
public class AddressEncryptionPlugin implements ComplexSecurityPlugin<Address, String> {
    @Override
    public String getPlugin(Address entity, String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value + "-加密后";
    }
}
