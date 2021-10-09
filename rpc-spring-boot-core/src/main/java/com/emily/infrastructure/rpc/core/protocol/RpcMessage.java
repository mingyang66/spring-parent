package com.emily.infrastructure.rpc.core.protocol;

import java.nio.charset.StandardCharsets;

/**
 * @program: spring-parent
 * @description: Rpc客户端及服务端交互消息
 * @author: Emily
 * @create: 2021/10/09
 */
public class RpcMessage {
    /**
     * 标记
     */
    private byte tag;
    /**
     * 包长度
     */
    private int length;
    /**
     * 包内容
     */
    private String body;

    public RpcMessage() {
    }

    public RpcMessage(String body) {
        this.length = body.getBytes(StandardCharsets.UTF_8).length;
        this.body = body;
    }

    public byte getTag() {
        return tag;
    }

    public void setTag(byte tag) {
        this.tag = tag;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
