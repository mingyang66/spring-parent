package com.emily.infrastructure.rpc.core.message;

/**
 * @program: spring-parent
 * @description: Rpc消息尾部
 * @author: Emily
 * @create: 2021/10/09
 */
public class IRpcTail {
    public static final byte[] TAIL = new byte[]{'\r', '\n'};
}
