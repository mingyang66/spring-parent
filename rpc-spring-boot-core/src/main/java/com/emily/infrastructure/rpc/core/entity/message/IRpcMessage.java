package com.emily.infrastructure.rpc.core.entity.message;

/**
 * @program: spring-parent
 * @description: Rpc客户端及服务端交互消息
 * @author: Emily
 * @create: 2021/10/09
 */
public class IRpcMessage {
    /**
     * 请求头
     */
    private IRpcHead head;
    /**
     * 消息
     */
    private IRpcBody body;
    /**
     * 消息尾部
     */
    private IRpcTail tail;

    public IRpcMessage() {
        this.head = new IRpcHead();
        this.tail = new IRpcTail();
    }

    public IRpcMessage(IRpcBody body) {
        this.head = new IRpcHead();
        this.body = body;
        this.tail = new IRpcTail();
    }
    public IRpcMessage(IRpcHead head, IRpcBody body) {
        this.head = head;
        this.body = body;
        this.tail = new IRpcTail();
    }
    public IRpcHead getHead() {
        return head;
    }

    public void setHead(IRpcHead head) {
        this.head = head;
    }

    public IRpcBody getBody() {
        return body;
    }

    public void setBody(IRpcBody body) {
        this.body = body;
    }

    public IRpcTail getTail() {
        return tail;
    }

    public void setTail(IRpcTail tail) {
        this.tail = tail;
    }
}
