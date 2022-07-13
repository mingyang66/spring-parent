package com.emily.infrastructure.rpc.core.message;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.core.context.holder.ContextHolder;

import java.io.Serializable;

/**
 * @program: spring-parent
 * @description: RPC调用响应数据
 * @author: Emily
 * @create: 2021/10/30
 */
public class IRpcResponse<T> implements Serializable {
    /**
     * 事务唯一标识, 36位
     */
    private String traceId;
    /**
     * 状态码
     */
    private int status;
    /**
     * 响应消息
     */
    private String message;
    /**
     * 响应结果
     */
    private T data;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public static <T> IRpcResponse<T> buildResponse(T data) {
        return buildResponse(AppHttpStatus.OK.getStatus(), AppHttpStatus.OK.getMessage(), data);
    }

    public static <T> IRpcResponse<T> buildResponse(int status, String message, T data) {
        IRpcResponse response = new IRpcResponse<>();
        response.setTraceId(ContextHolder.peek().getTraceId());
        response.setStatus(status);
        response.setMessage(message);
        response.setData(data);
        return response;
    }
}
