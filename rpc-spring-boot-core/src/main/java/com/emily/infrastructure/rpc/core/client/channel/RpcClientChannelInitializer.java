package com.emily.infrastructure.rpc.core.client.channel;

import com.emily.infrastructure.rpc.core.client.handler.RpcClientChannelHandler;
import com.emily.infrastructure.rpc.core.decoder.MyMessageDecoder;
import com.emily.infrastructure.rpc.core.encoder.MyMessageEncoder;
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
                .addLast(new MyMessageDecoder())
                .addLast(new MyMessageEncoder())
                .addLast(rpcClientChannelHandler);
    }
}
