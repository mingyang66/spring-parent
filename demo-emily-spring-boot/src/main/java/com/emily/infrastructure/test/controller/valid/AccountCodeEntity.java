package com.emily.infrastructure.test.controller.valid;

import com.emily.infrastructure.autoconfigure.valid.annotation.IsAccountCode;

/**
 * @author :  Emily
 * @since :  2024/2/23 3:21 PM
 */
public class AccountCodeEntity {
    @IsAccountCode(minLength = 8, maxLength = 10, prefixes = {"10", "20"}, suffixes = {"99"}, type = Long.class)
    public String accountCode;
}
