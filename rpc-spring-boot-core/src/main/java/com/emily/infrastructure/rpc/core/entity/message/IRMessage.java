package com.emily.infrastructure.rpc.core.entity.message;

/**
 * @program: spring-parent
 * @description: Rpc客户端及服务端交互消息
 * @author: Emily
 * @create: 2021/10/09
 */
public class IRMessage {
    /**
     * 请求头
     */
    private IRHead head;
    /**
     * 消息
     */
    private IRBody body;
    /**
     * 消息尾部
     */
    private IRTail tail;

    public IRMessage() {
        this.head = new IRHead();
        this.tail = new IRTail();
    }

    public IRMessage(IRBody body) {
        this.head = new IRHead();
        this.body = body;
        this.tail = new IRTail();
    }
    public IRMessage(IRHead head, IRBody body) {
        this.head = head;
        this.body = body;
        this.tail = new IRTail();
    }
    public IRHead getHead() {
        return head;
    }

    public void setHead(IRHead head) {
        this.head = head;
    }

    public IRBody getBody() {
        return body;
    }

    public void setBody(IRBody body) {
        this.body = body;
    }

    public IRTail getTail() {
        return tail;
    }

    public void setTail(IRTail tail) {
        this.tail = tail;
    }
}
