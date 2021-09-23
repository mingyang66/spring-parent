package com.emily.infrastructure.rpc.core.client.channel;

import com.emily.infrastructure.rpc.core.client.handler.RpcClientChannelHandler;
import com.emily.infrastructure.rpc.core.decoder.RpcDecoder;
import com.emily.infrastructure.rpc.core.encoder.RpcEncoder;
import com.emily.infrastructure.rpc.core.protocol.RpcRequest;
import com.emily.infrastructure.rpc.core.protocol.RpcResponse;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/09/22
 */
public class RpcClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    private RpcClientChannelHandler rpcClientChannelHandler;

    public RpcClientChannelInitializer(RpcClientChannelHandler rpcClientChannelHandler) {
        this.rpcClientChannelHandler = rpcClientChannelHandler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                .addLast(new RpcEncoder(RpcRequest.class))
                .addLast(new RpcDecoder(RpcResponse.class))
                .addLast(rpcClientChannelHandler);
    }
}
