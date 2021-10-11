package com.emily.infrastructure.rpc.core.entity.message;


/**
 * @program: spring-parent
 * @description: 请求header
 * @author: Emily
 * @create: 2021/10/09
 */
public class RHead {
    /**
     * 连接超时时间
     */
    private int keepAlive = 60;

    public RHead() {

    }

    public RHead(int keepAlive) {
        this.keepAlive = keepAlive;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }
}
