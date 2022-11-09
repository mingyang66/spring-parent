package com.emily.infrastructure.common.exception;


import com.emily.infrastructure.common.enums.HttpStatusType;
import com.emily.infrastructure.common.i18n.LanguageCache;

/**
 * @author Emily
 * @Description: 业务异常
 * @Version: 1.0
 */
public class BasicException extends RuntimeException {
    /**
     * 状态码
     */
    private int status;
    /**
     * 异常信息
     */
    private String message;
    /**
     * 是否是错误信息，默认：true
     */
    private boolean error = true;

    public BasicException() {
    }

    public BasicException(HttpStatusType httpStatus) {
        super(httpStatus.getMessage());
        this.status = httpStatus.getStatus();
        this.message = LanguageCache.peek(httpStatus.getMessage());
    }

    public BasicException(int status, String message) {
        super(message);
        this.status = status;
        this.message = LanguageCache.peek(message);
    }

    public BasicException(int status, String errorMessage, boolean error) {
        this(status, errorMessage);
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = LanguageCache.peek(message);
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
