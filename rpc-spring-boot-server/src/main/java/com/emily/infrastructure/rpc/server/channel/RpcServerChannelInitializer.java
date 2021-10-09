package com.emily.infrastructure.rpc.server.channel;

import com.emily.infrastructure.rpc.core.decoder.RpcDecoder;
import com.emily.infrastructure.rpc.core.encoder.RpcEncoder;
import com.emily.infrastructure.rpc.core.protocol.RpcRequest;
import com.emily.infrastructure.rpc.server.handler.RpcServerChannelHandler;
import com.emily.infrastructure.rpc.server.registry.RpcRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/09/22
 */
public class RpcServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private RpcRegistry registry;

    public RpcServerChannelInitializer(RpcRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //自定义分隔符
        ByteBuf delimiter  = Unpooled.copiedBuffer("\r\n".getBytes());
        pipeline.addFirst(new DelimiterBasedFrameDecoder(8192, delimiter));
        pipeline.addLast(new RpcEncoder());
        pipeline.addLast(new RpcDecoder(RpcRequest.class));
        pipeline.addLast(new RpcServerChannelHandler(registry));
    }
}
