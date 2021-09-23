package com.emily.infrastructure.rpc.core.server.channel;

import com.emily.infrastructure.rpc.core.decoder.MyMessageDecoder;
import com.emily.infrastructure.rpc.core.encoder.MyMessageEncoder;
import com.emily.infrastructure.rpc.core.server.handler.RpcServerChannelHandler;
import com.emily.infrastructure.rpc.core.server.registry.RpcProviderRegistry;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/09/22
 */
public class RpcServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private RpcProviderRegistry registry;

    public RpcServerChannelInitializer(RpcProviderRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //给pipeline设置通道处理器
        ch.pipeline()
                .addLast(new MyMessageDecoder())
                .addLast(new MyMessageEncoder())
                .addLast(new RpcServerChannelHandler(registry));
    }
}
