package com.emily.infrastructure.desensitize;

/**
 * 脱敏类型
 *
 * @author Emily
 * @since :  Created in 2022/10/27 10:03 下午
 */
public enum DesensitizeType {
    DEFAULT,
    // 手机号
    PHONE,
    // 银行卡号
    BANK_CARD,
    // 身份证号
    ID_CARD,
    // 姓名
    USERNAME,
    // email
    EMAIL,
    //地址
    ADDRESS;
}
