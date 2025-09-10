package com.emily.infrastructure.web.exception.handler;


import com.emily.infrastructure.web.exception.entity.BasicException;
import com.emily.infrastructure.web.response.enums.ApplicationStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.UnknownContentTypeException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Objects;

/**
 * 控制并统一处理异常类 @ExceptionHandler标注的方法优先级问题，它会找到异常的最近继承关系，也就是继承关系最浅的注解方法
 *
 * @author Emily
 * @since 1.0
 */
@RestControllerAdvice
public class DefaultGlobalExceptionHandler extends GlobalExceptionCustomizer {

    public DefaultGlobalExceptionHandler(ApplicationContext context) {
        super(context);
    }

    /**
     * 未知异常
     *
     * @param e             异常
     * @param request       请求对象
     * @param handlerMethod 方法对象
     * @return 异常处理后返回给用户的对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = {
            Exception.class,
            RuntimeException.class,
            IOException.class,
    })
    public Object exceptionHandler(Exception e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        String message = e.getMessage();
        if (e instanceof IOException) {
            message = ApplicationStatus.EXCEPTION.getMessage();
        }
        return getApiResponseWrapper(handlerMethod, ApplicationStatus.EXCEPTION.getStatus(), message);
    }

    /**
     * 业务异常
     *
     * @param e             异常
     * @param request       请求对象
     * @param handlerMethod 方法对象
     * @return 异常处理后返回给用户的对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BasicException.class)
    public Object basicException(BasicException e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        return getApiResponseWrapper(handlerMethod, e.getStatus(), e.getMessage());
    }

    /**
     * 非法代理
     *
     * @param e             异常
     * @param request       请求对象
     * @param handlerMethod 方法对象
     * @return 异常处理后返回给用户的对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(UndeclaredThrowableException.class)
    public Object undeclaredThrowableException(UndeclaredThrowableException e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        Throwable throwable = e.getUndeclaredThrowable();
        if (throwable != null) {
            return getApiResponseWrapper(handlerMethod, ApplicationStatus.ILLEGAL_PROXY.getStatus(), throwable.getMessage());
        }
        return getApiResponseWrapper(handlerMethod, ApplicationStatus.ILLEGAL_PROXY);
    }

    /**
     * API-控制器方法参数Validated参数绑定异常
     * 1. BindException[MethodArgumentNotValidException]示例如下：
     * <pre>{@code
     * public class Job implements Serializable {
     *     //@NotNull(message = "不可为空")
     *     private Long id;
     *     private Long jobNumber;
     *     //@NotEmpty(message = "描述不可以为空")
     *     private String jobDesc;
     *     public String a;
     * }
     * }</pre>
     * 2. ValidationException[ConstraintViolationException](Get请求参数校验，如@NotEmpty、@NotNull等等)示例如下：
     * <pre>{@code
     *     @GetMapping("validParam")
     *     public String validParam(@Validated @NotEmpty(message = "不可为空") String username){
     *         return username;
     *     }
     * }</pre>
     *
     * @param e             异常
     * @param request       请求对象
     * @param handlerMethod 方法对象
     * @return 异常处理后返回给用户的对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            HttpMessageNotReadableException.class,
            ErrorResponseException.class,
            MissingRequestValueException.class,
            TypeMismatchException.class,
            BindException.class,
            ValidationException.class
    })
    public Object illegalArgumentException(Exception e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        String message = ApplicationStatus.ILLEGAL_ARGUMENT.getMessage();
        try {
            //当用@Valid注释的参数验证失败时引发异常。从5.3起扩展BindException。
            if (e instanceof MethodArgumentNotValidException ex) {
                // ex.getFieldError() == null时，表示校验参数的注解标注在类上，否则就在实体类字段上
                message = ex.getFieldError() == null ? Objects.requireNonNull(ex.getGlobalError()).getDefaultMessage() : ex.getFieldError().getDefaultMessage();
            } else if (e instanceof BindException ex) {
                message = ex.getFieldError() == null ? Objects.requireNonNull(ex.getGlobalError()).getDefaultMessage() : ex.getFieldError().getDefaultMessage();
            } else if (e instanceof ConstraintViolationException ex) {
                // ValidationException的子类
                message = ex.getConstraintViolations().stream().findFirst().get().getMessageTemplate();
            } else if (e instanceof IllegalArgumentException ex) {
                message = ex.getMessage();
            }
        } catch (Exception ignored) {
        }
        return getApiResponseWrapper(handlerMethod, ApplicationStatus.ILLEGAL_ARGUMENT.getStatus(), message);
    }


    /**
     * 数字格式异常
     *
     * @param e             异常
     * @param request       请求对象
     * @param handlerMethod 方法对象
     * @return 异常处理后返回给用户的对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = {
            NumberFormatException.class,
            ArithmeticException.class,
            IndexOutOfBoundsException.class,
            NullPointerException.class,
            ClassCastException.class
    })
    public Object numberFormatException(Exception e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        return getApiResponseWrapper(handlerMethod, ApplicationStatus.ILLEGAL_DATA);
    }

    /**
     * 非法访问
     *
     * @param e             异常
     * @param request       请求对象
     * @param handlerMethod 方法对象
     * @return 异常处理后返回给用户的对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = {
            UnknownContentTypeException.class,
            ResourceAccessException.class
    })
    public Object unknownContentTypeException(Exception e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        return getApiResponseWrapper(handlerMethod, ApplicationStatus.ILLEGAL_ACCESS);
    }

    /**
     * API-请求method不匹配
     * 1. 不支持HandlerMethod handlerMethod参数
     *
     * @param e       异常
     * @param request 请求对象
     * @return 异常处理后返回给用户的对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return getApiResponseWrapper(null, ApplicationStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * API-接口资源不存在
     * 1.不支持HandlerMethod handlerMethod参数
     *
     * @param e       异常
     * @param request 请求对象
     * @return 异常处理后返回给用户的对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public Object noResourceFoundException(NoResourceFoundException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return getApiResponseWrapper(null, ApplicationStatus.NOT_FOUND);
    }
}

