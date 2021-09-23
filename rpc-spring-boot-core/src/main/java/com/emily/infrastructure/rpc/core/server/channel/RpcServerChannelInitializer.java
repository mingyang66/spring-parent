package com.emily.infrastructure.rpc.core.server.channel;

import com.emily.infrastructure.rpc.core.decoder.MyMessageDecoder;
import com.emily.infrastructure.rpc.core.encoder.MyMessageEncoder;
import com.emily.infrastructure.rpc.core.server.handler.RpcServerChannelHandler;
import com.emily.infrastructure.rpc.core.server.registry.RpcProviderRegistry;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

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
                .addLast(new ObjectDecoder(1024, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())))
                .addLast(new ObjectEncoder())
                .addLast(new RpcServerChannelHandler(registry));
    }
}
