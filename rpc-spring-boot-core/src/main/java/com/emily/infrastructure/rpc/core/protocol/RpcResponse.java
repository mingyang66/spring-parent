package com.emily.infrastructure.rpc.core.protocol;

import java.io.Serializable;

/**
 * @program: spring-parent
 * @description: RPC响应数据
 * @author: Emily
 * @create: 2021/09/22
 */
public class RpcResponse implements Serializable {
    private String traceId;
    private Object data;

    public RpcResponse() {
    }

    public RpcResponse(String traceId, Object data) {
        this.traceId = traceId;
        this.data = data;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
