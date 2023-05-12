package com.emily.infrastructure.sensitive;

/**
 * @Description :  脱敏类型
 * @Author :  Emily
 * @CreateDate :  Created in 2022/10/27 10:03 上午
 */
public enum SensitiveType {
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
    EMAIL;
}
