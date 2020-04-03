package com.yaomy.security.oauth2.exception;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.exception.PasswordException
 * @Date: 2019/7/18 16:34
 * @Version: 1.0
 */
public class PasswordException extends OAuth2Exception {

    public PasswordException(String message, Throwable t) {
        super(message, t);
    }

    public PasswordException(String message) {
        super(message);
    }
    @Override
    public int getHttpErrorCode() {
        return 302;
    }
}
