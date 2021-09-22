package com.emily.infrastructure.rpc.core.client.channel;

import com.emily.infrastructure.rpc.core.client.handler.RpcProxyHandler;
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
    private RpcProxyHandler rpcProxyHandler;

    public RpcClientChannelInitializer(RpcProxyHandler rpcProxyHandler) {
        this.rpcProxyHandler = rpcProxyHandler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                .addLast(new MyMessageDecoder())
                .addLast(new MyMessageEncoder())
                .addLast(rpcProxyHandler);
    }
}
