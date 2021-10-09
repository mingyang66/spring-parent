package com.emily.infrastructure.rpc.core.protocol;

import com.emily.infrastructure.common.utils.json.JSONUtils;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * @program: spring-parent
 * @description: RPC响应数据
 * @author: Emily
 * @create: 2021/09/22
 */
public class RpcBody implements Serializable {
    /**
     * 事务唯一标识
     */
    private byte[] traceId;
    /**
     * 消息体长度
     */
    private int len;
    /**
     * 返回数据
     */
    private byte[] data;

    public RpcBody() {
    }

    public RpcBody(byte[] traceId, byte[] data) {
        this.traceId = traceId;
        this.data = data;
        this.len = data.length;
    }

    public byte[] getTraceId() {
        return traceId;
    }

    public void setTraceId(byte[] traceId) {
        this.traceId = traceId;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public static RpcBody toBody(String tradeId, Object data) {
        return new RpcBody(tradeId.getBytes(StandardCharsets.UTF_8), JSONUtils.toByteArray(data));
    }
}
