package com.emily.infrastructure.rpc.core.entity.message;


import com.emily.infrastructure.core.holder.ContextHolder;

import java.nio.charset.StandardCharsets;

/**
 * @program: spring-parent
 * @description: 请求header
 * @author: Emily
 * @create: 2021/10/09
 */
public class IRpcHead {
    /**
     * 包类型，0-正常RPC请求，1-心跳包
     */
    private int packageType;
    /**
     * 事务唯一标识, 36位
     */
    private byte[] traceId;

    public IRpcHead() {
        this.traceId = ContextHolder.get().getTraceId().getBytes(StandardCharsets.UTF_8);
    }

    public IRpcHead(byte[] traceId) {
        this.traceId = traceId;
    }

    public IRpcHead(int packageType) {
        this.packageType = packageType;
    }

    public int getPackageType() {
        return packageType;
    }

    public void setPackageType(int packageType) {
        this.packageType = packageType;
    }

    public byte[] getTraceId() {
        return traceId;
    }

    public void setTraceId(byte[] traceId) {
        this.traceId = traceId;
    }
}
