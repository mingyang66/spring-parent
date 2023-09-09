package com.emily.infrastructure.autoconfigure.exception.handler;


import com.emily.infrastructure.core.exception.BasicException;
import com.emily.infrastructure.core.exception.HttpStatusType;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.UnknownContentTypeException;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * 控制并统一处理异常类 @ExceptionHandler标注的方法优先级问题，它会找到异常的最近继承关系，也就是继承关系最浅的注解方法
 *
 * @author Emily
 * @since 1.0
 */
@RestControllerAdvice
public class DefaultGlobalExceptionHandler extends GlobalExceptionCustomizer {
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
    @ExceptionHandler(value = Exception.class)
    public Object exceptionHandler(Exception e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        return getApiResponseWrapper(handlerMethod, HttpStatusType.EXCEPTION.getStatus(), e.getMessage());
    }

    /**
     * 运行时异常
     *
     * @param e             异常
     * @param request       请求对象
     * @param handlerMethod 方法对象
     * @return 异常处理后返回给用户的对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = RuntimeException.class)
    public Object runtimeExceptionHandler(RuntimeException e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        return getApiResponseWrapper(handlerMethod, HttpStatusType.EXCEPTION.getStatus(), e.getMessage());
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
        return getApiResponseWrapper(handlerMethod, HttpStatusType.ILLEGAL_PROXY);
    }

    /**
     * 空指针异常
     *
     * @param e             异常
     * @param request       请求对象
     * @param handlerMethod 方法对象
     * @return 异常处理后返回给用户的对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(NullPointerException.class)
    public Object nullPointerExceptionHandler(NullPointerException e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        return getApiResponseWrapper(handlerMethod, HttpStatusType.ILLEGAL_DATA);
    }

    /**
     * 类型转换异常
     *
     * @param e             异常
     * @param request       请求对象
     * @param handlerMethod 方法对象
     * @return 异常处理后返回给用户的对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ClassCastException.class)
    public Object classCastExceptionHandler(ClassCastException e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        return getApiResponseWrapper(handlerMethod, HttpStatusType.ILLEGAL_DATA);
    }

    /**
     * IO异常
     *
     * @param e             异常
     * @param request       请求对象
     * @param handlerMethod 方法对象
     * @return 异常处理后返回给用户的对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(IOException.class)
    public Object ioExceptionHandler(IOException e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        return getApiResponseWrapper(handlerMethod, HttpStatusType.EXCEPTION);
    }

    /**
     * 数组越界异常
     *
     * @param e             异常
     * @param request       请求对象
     * @param handlerMethod 方法对象
     * @return 异常处理后返回给用户的对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public Object indexOutOfBoundsException(IndexOutOfBoundsException e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        return getApiResponseWrapper(handlerMethod, HttpStatusType.ILLEGAL_DATA);
    }

    /**
     * API-参数类型不匹配
     *
     * @param e             异常
     * @param request       请求对象
     * @param handlerMethod 方法对象
     * @return 异常处理后返回给用户的对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(TypeMismatchException.class)
    public Object requestTypeMismatch(TypeMismatchException e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        return getApiResponseWrapper(handlerMethod, HttpStatusType.ILLEGAL_ARGUMENT);
    }

    /**
     * API-缺少参数，如Get请求@RequestParam注解
     *
     * @param e             异常
     * @param request       请求对象
     * @param handlerMethod 方法对象
     * @return 异常处理后返回给用户的对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MissingRequestValueException.class)
    public Object missingRequestValueException(MissingRequestValueException e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        return getApiResponseWrapper(handlerMethod, HttpStatusType.ILLEGAL_ARGUMENT);
    }


    /**
     * API-控制器方法参数Validated参数绑定异常
     * 示例如下：
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
     *
     * @param e             异常
     * @param request       请求对象
     * @param handlerMethod 方法对象
     * @return 异常处理后返回给用户的对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({BindException.class})
    public Object bindException(BindException e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        return getApiResponseWrapper(handlerMethod, HttpStatusType.ILLEGAL_ARGUMENT.getStatus(), e.getFieldError().getDefaultMessage());
    }

    /**
     * Get请求参数校验，如@NotEmpty、@NotNull等等
     * 示例如下：
     * <pre>{@code
     *     //@GetMapping("validParam")
     *     public String validParam(@Validated @NotEmpty(message = "不可为空") String username){
     *         return username;
     *     }
     * }</pre>
     *
     * @param e             异常对象
     * @param request       请求对象
     * @param handlerMethod 方法对象
     * @return 异常处理后返回给用户的对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ValidationException.class)
    public Object validationException(ValidationException e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        String message = HttpStatusType.ILLEGAL_ARGUMENT.getMessage();
        // 提取默认消息
        if (e instanceof ConstraintViolationException) {
            message = ((ConstraintViolationException) e).getConstraintViolations().stream().findFirst().get().getMessageTemplate();
        }
        return getApiResponseWrapper(handlerMethod, HttpStatusType.ILLEGAL_ARGUMENT.getStatus(), message);
    }

    /**
     * @param e             异常
     * @param request       请求对象
     * @param handlerMethod 方法对象
     * @return 异常处理后返回给用户的对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object httpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        return getApiResponseWrapper(handlerMethod, HttpStatusType.ILLEGAL_ARGUMENT);
    }


    /**
     * 非法参数异常捕获
     *
     * @param e             异常
     * @param request       请求对象
     * @param handlerMethod 方法对象
     * @return 异常处理后返回给用户的对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(IllegalArgumentException.class)
    public Object illegalArgumentException(IllegalArgumentException e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        return getApiResponseWrapper(handlerMethod, HttpStatusType.ILLEGAL_ARGUMENT);
    }

    /**
     * API-请求method不匹配
     * 不支持HandlerMethod handlerMethod参数
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
        return getApiResponseWrapper(null, HttpStatusType.METHOD_NOT_ALLOWED);
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
    @ExceptionHandler(NumberFormatException.class)
    public Object numberFormatException(NumberFormatException e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        return getApiResponseWrapper(handlerMethod, HttpStatusType.ILLEGAL_DATA);
    }

    /**
     * 非法计算异常
     *
     * @param e             异常
     * @param request       请求对象
     * @param handlerMethod 方法对象
     * @return 异常处理后返回给用户的对象
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ArithmeticException.class)
    public Object arithmeticException(ArithmeticException e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        return getApiResponseWrapper(handlerMethod, HttpStatusType.ILLEGAL_DATA);
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
    @ExceptionHandler(UnknownContentTypeException.class)
    public Object unknownContentTypeException(UnknownContentTypeException e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        return getApiResponseWrapper(handlerMethod, HttpStatusType.ILLEGAL_ACCESS);
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
    @ExceptionHandler(ResourceAccessException.class)
    public Object resourceAccessException(ResourceAccessException e, HttpServletRequest request, HandlerMethod handlerMethod) {
        recordErrorMsg(e, request);
        return getApiResponseWrapper(handlerMethod, HttpStatusType.ILLEGAL_ACCESS);
    }
}

