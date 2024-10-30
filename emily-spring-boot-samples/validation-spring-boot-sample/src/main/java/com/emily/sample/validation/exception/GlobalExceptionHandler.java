package com.emily.sample.validation.exception;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * @author :  Emily
 * @since :  2024/10/29 上午10:42
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public String exceptionHandler(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult.hasFieldErrors()) {
            return Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
        }
        return e.getMessage();
    }
}
