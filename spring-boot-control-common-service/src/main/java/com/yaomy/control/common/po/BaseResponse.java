package com.yaomy.control.common.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yaomy.control.common.enums.HttpStatusMsg;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

/**
 * @Description: 控制器返回结果
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.po.AjaxResponseBody
 * @Date: 2019/7/1 15:33
 * @Version: 1.0
 */
@SuppressWarnings("all")
@Data
public class BaseResponse implements Serializable {
    private int status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;
    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version  1.0
     */
    public static BaseResponse createResponse(int status, String message){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(status);
        baseResponse.setMessage(message);
        return baseResponse;
    }
    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version  1.0
     */
    public static ResponseEntity<BaseResponse> createResponseEntity(int status, String message){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(status);
        baseResponse.setMessage(message);
        return ResponseEntity.ok(baseResponse);
    }
    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version  1.0
     */
    public static BaseResponse createResponse(int status, String message, Object data){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(status);
        baseResponse.setMessage(message);
        baseResponse.setData(data);
        return baseResponse;
    }
    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version  1.0
     */
    public static ResponseEntity<BaseResponse> createResponseEntity(int status, String message, Object data){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(status);
        baseResponse.setMessage(message);
        baseResponse.setData(data);
        return ResponseEntity.ok(baseResponse);
    }
    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version  1.0
     */
    public static BaseResponse createResponse(HttpStatusMsg httpStatusMsg){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(httpStatusMsg.getStatus());
        baseResponse.setMessage(httpStatusMsg.getMessage());
        return baseResponse;
    }
    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version  1.0
     */
    public static ResponseEntity<BaseResponse> createResponseEntity(HttpStatusMsg httpStatusMsg){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(httpStatusMsg.getStatus());
        baseResponse.setMessage(httpStatusMsg.getMessage());
        return ResponseEntity.ok(baseResponse);
    }
    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version  1.0
     */
    public static BaseResponse createResponse(HttpStatusMsg httpStatusMsg, Object data){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(httpStatusMsg.getStatus());
        baseResponse.setMessage(httpStatusMsg.getMessage());
        baseResponse.setData(data);
        return baseResponse;
    }
    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version  1.0
     */
    public static ResponseEntity<BaseResponse> createResponseEntity(HttpStatusMsg httpStatusMsg, Object data){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(httpStatusMsg.getStatus());
        baseResponse.setMessage(httpStatusMsg.getMessage());
        baseResponse.setData(data);
        return ResponseEntity.ok(baseResponse);
    }
}
