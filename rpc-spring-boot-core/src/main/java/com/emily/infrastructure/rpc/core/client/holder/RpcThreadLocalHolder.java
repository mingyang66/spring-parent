package com.emily.infrastructure.rpc.core.client.holder;

import io.netty.channel.ChannelHandlerContext;

/**
 * @program: spring-parent
 * @description: Rpc请求上下文持有容器类
 * @author: 姚明洋
 * @create: 2021/09/22
 */
public class RpcThreadLocalHolder {
    private static final ThreadLocal<ChannelHandlerContext> threadLocal = new ThreadLocal<>();

    public static void set(ChannelHandlerContext channelHandlerContext) {
        threadLocal.set(channelHandlerContext);
    }

    public static ChannelHandlerContext get() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }
}
