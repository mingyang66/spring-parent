package com.yaomy.security.oauth2.exception;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * @Description: 异常处理类
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.exception.UserOAuth2Exception
 * @Date: 2019/7/17 15:29
 * @Version: 1.0
 */
@JsonSerialize(using = UserOAuth2ExceptionSerializer.class)
public class UserOAuth2Exception extends OAuth2Exception {
    private Integer status = 400;

    public UserOAuth2Exception(String message, Throwable t) {
        super(message, t);
        status = ((OAuth2Exception)t).getHttpErrorCode();
    }

    public UserOAuth2Exception(String message) {
        super(message);
    }
    @Override
    public int getHttpErrorCode() {
        return status;
    }

}
