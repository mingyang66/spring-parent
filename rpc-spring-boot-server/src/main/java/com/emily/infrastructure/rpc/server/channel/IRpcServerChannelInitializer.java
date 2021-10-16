package com.emily.infrastructure.rpc.server.channel;

import com.emily.infrastructure.rpc.core.decoder.IRpcDecoder;
import com.emily.infrastructure.rpc.core.encoder.IRpcEncoder;
import com.emily.infrastructure.rpc.core.entity.message.IRTail;
import com.emily.infrastructure.rpc.server.handler.IRpcServerChannelHandler;
import com.emily.infrastructure.rpc.server.registry.IRpcProviderRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/09/22
 */
public class IRpcServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private IRpcProviderRegistry registry;

    public IRpcServerChannelInitializer(IRpcProviderRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //空闲状态处理器，参数说明：读时间空闲时间，0禁用时间|写事件空闲时间，0则禁用|读或写空闲时间，0则禁用
        pipeline.addFirst(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS));
        //分隔符解码器
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Unpooled.copiedBuffer(IRTail.TAIL)));
        //自定义编码器
        pipeline.addLast(new IRpcEncoder());
        //自定义解码器
        pipeline.addLast(new IRpcDecoder());
        //自定义处理器
        pipeline.addLast(new IRpcServerChannelHandler(registry));
    }
}
