package com.emily.infrastructure.rpc.server.channel;

import com.emily.infrastructure.rpc.core.decoder.IRpcDecoder;
import com.emily.infrastructure.rpc.core.encoder.IRpcEncoder;
import com.emily.infrastructure.rpc.core.entity.message.IRTail;
import com.emily.infrastructure.rpc.server.handler.IRpcServerChannelHandler;
import com.emily.infrastructure.rpc.server.registry.IRpcRegistry;
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
public class IRpcServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private IRpcRegistry registry;

    public IRpcServerChannelInitializer(IRpcRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //自定义分隔符
        ByteBuf delimiter = Unpooled.copiedBuffer(IRTail.TAIL);
        //分隔符解码器
        pipeline.addFirst(new DelimiterBasedFrameDecoder(8192, delimiter));
        //自定义编码器
        pipeline.addLast(new IRpcEncoder());
        //自定义解码器
        pipeline.addLast(new IRpcDecoder());
        //自定义处理器
        pipeline.addLast(new IRpcServerChannelHandler(registry));
    }
}
