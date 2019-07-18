package com.yaomy.common.po;

import lombok.Data;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.po.AjaxResponseBody
 * @Date: 2019/7/1 15:33
 * @Version: 1.0
 */
@Data
public class BaseResponse {
    private int status;
    private String message;
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
}
