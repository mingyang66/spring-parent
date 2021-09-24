package com.emily.infrastructure.rpc.server.channel;

import com.emily.infrastructure.rpc.core.decoder.RpcDecoder;
import com.emily.infrastructure.rpc.core.encoder.RpcEncoder;
import com.emily.infrastructure.rpc.core.protocol.RpcRequest;
import com.emily.infrastructure.rpc.core.protocol.RpcResponse;
import com.emily.infrastructure.rpc.server.handler.RpcServerChannelHandler;
import com.emily.infrastructure.rpc.server.registry.RpcProviderRegistry;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

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
                .addLast(new IdleStateHandler(0, 0, 10, TimeUnit.SECONDS))
                .addLast(new RpcEncoder(RpcResponse.class))
                .addLast(new RpcDecoder(RpcRequest.class))
                .addLast(new RpcServerChannelHandler(registry));
    }
}
