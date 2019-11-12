package com.yaomy.security.oauth2.po;

import lombok.Data;

/**
 * @Description: 用户令牌
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
@Data
public class MyToken {
    private String accessToken;
    private String refreshToken;
}
