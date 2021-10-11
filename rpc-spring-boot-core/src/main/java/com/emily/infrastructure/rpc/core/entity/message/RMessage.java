package com.emily.infrastructure.rpc.core.entity.message;

/**
 * @program: spring-parent
 * @description: Rpc客户端及服务端交互消息
 * @author: Emily
 * @create: 2021/10/09
 */
public class RMessage {
    /**
     * 请求头
     */
    private RHead head;
    /**
     * 消息
     */
    private RBody body;
    /**
     * 消息尾部
     */
    private RTail tail;

    public RMessage() {
        this.head = new RHead();
        this.tail = new RTail();
    }

    public RMessage(RBody body) {
        this.head = new RHead();
        this.body = body;
        this.tail = new RTail();
    }
    public RMessage(RHead head, RBody body) {
        this.head = head;
        this.body = body;
        this.tail = new RTail();
    }
    public RHead getHead() {
        return head;
    }

    public void setHead(RHead head) {
        this.head = head;
    }

    public RBody getBody() {
        return body;
    }

    public void setBody(RBody body) {
        this.body = body;
    }

    public RTail getTail() {
        return tail;
    }

    public void setTail(RTail tail) {
        this.tail = tail;
    }
}
