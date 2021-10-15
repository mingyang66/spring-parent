package com.emily.infrastructure.rpc.core.entity.message;

/**
 * @program: spring-parent
 * @description: Rpc消息尾部
 * @author: Emily
 * @create: 2021/10/09
 */
public class IRTail {
    public static final byte[] TAIL = new byte[]{'\r', '\n'};
    /**
     * 消息尾部长度
     */
    private int len;
    /**
     * 消息尾部分隔符
     */
    private byte[] tail;

    public IRTail() {
        this.tail = TAIL;
        this.len = tail.length;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getTail() {
        return tail;
    }

    public void setTail(byte[] tail) {
        this.tail = tail;
    }
}
