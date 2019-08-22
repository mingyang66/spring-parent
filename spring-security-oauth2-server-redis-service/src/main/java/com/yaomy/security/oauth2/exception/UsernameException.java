package com.yaomy.security.oauth2.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @Description: 用户名不存在异常类
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.exception.UsernameNotFundException
 * @Date: 2019/7/18 16:43
 * @Version: 1.0
 */
public class UsernameException extends AuthenticationException {
    public UsernameException(String message, Throwable t) {
        super(message, t);
    }

    public UsernameException(String message) {
        super(message);
    }
   /* @Override
    public int getHttpErrorCode() {
        return HttpStatusMsg.USERNAME_EXCEPTION.getStatus();
    }*/
}
