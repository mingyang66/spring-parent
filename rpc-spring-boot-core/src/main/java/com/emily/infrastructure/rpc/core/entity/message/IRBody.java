package com.emily.infrastructure.rpc.core.entity.message;

import com.emily.infrastructure.common.utils.json.JSONUtils;

import java.io.Serializable;

/**
 * @program: spring-parent
 * @description: RPC响应数据
 * @author: Emily
 * @create: 2021/09/22
 */
public class IRBody implements Serializable {
    /**
     * 消息体长度
     */
    private int len;
    /**
     * 返回数据
     */
    private byte[] data;

    public IRBody() {
    }

    public IRBody(byte[] data) {
        this.data = data;
        this.len = data.length;
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

    public static IRBody toBody(Object data) {
        return new IRBody(JSONUtils.toByteArray(data));
    }

    public static IRBody toBody(byte[] data) {
        return new IRBody(data);
    }
}
