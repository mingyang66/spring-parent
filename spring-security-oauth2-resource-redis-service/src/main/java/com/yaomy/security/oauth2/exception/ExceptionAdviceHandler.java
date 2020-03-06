package com.yaomy.security.oauth2.exception;

import com.yaomy.sgrain.common.control.po.BaseResponse;
import com.yaomy.sgrain.common.enums.HttpStatusMsg;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;

/**
 * @Description: 控制并统一处理异常类
 * @ExceptionHandler标注的方法优先级问题，它会找到异常的最近继承关系，也就是继承关系最浅的注解方法
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.exception.ExceptionAdviceHandler
 * @Date: 2019/7/18 11:03
 * @Version: 1.0
 */
@RestControllerAdvice
public final class ExceptionAdviceHandler {

    /**
     * 未知异常
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse unKnowExceptionHandler(Exception e) {
        e.printStackTrace();
        return BaseResponse.createResponse(HttpStatusMsg.UNKNOW_EXCEPTION.getStatus(), HttpStatusMsg.UNKNOW_EXCEPTION.getMessage()+e.toString());
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(value = RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse runtimeExceptionHandler(RuntimeException e) {
        e.printStackTrace();
        return BaseResponse.createResponse(HttpStatusMsg.RUNTIME_EXCEPTION.getStatus(), HttpStatusMsg.RUNTIME_EXCEPTION.getMessage()+e.toString());
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse nullPointerExceptionHandler(NullPointerException e) {
        e.printStackTrace();
        return BaseResponse.createResponse(HttpStatusMsg.NULL_POINTER_EXCEPTION.getStatus(), e.toString());
    }

    /**
     * 类型转换异常
     */
    @ExceptionHandler(ClassCastException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse classCastExceptionHandler(ClassCastException e) {
        e.printStackTrace();
        return BaseResponse.createResponse(HttpStatusMsg.CLASS_CAST_EXCEPTION.getStatus(), e.toString());
    }

    /**
     * IO异常
     */
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse iOExceptionHandler(IOException e) {
        e.printStackTrace();
        return BaseResponse.createResponse(HttpStatusMsg.IO_EXCEPTION.getStatus(), e.toString());
    }

    /**
     * 数组越界异常
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse indexOutOfBoundsExceptionHandler(IndexOutOfBoundsException e) {
        e.printStackTrace();
        return BaseResponse.createResponse(HttpStatusMsg.INDEX_OUTOF_BOUNDS_EXCEPTION.getStatus(), e.toString());
    }

    /**
     * 参数类型不匹配
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse requestTypeMismatch(MethodArgumentTypeMismatchException e){
        return BaseResponse.createResponse(HttpStatusMsg.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTIION.getStatus(), "参数类型不匹配，参数"+e.getName()+"类型必须为"+e.getRequiredType());
    }
    /**
     * 缺少参数
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse requestMissingServletRequest(MissingServletRequestParameterException e) {
        return BaseResponse.createResponse(HttpStatusMsg.MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION.getStatus(), "缺少必要参数，参数名称为"+e.getParameterName());
    }

}

