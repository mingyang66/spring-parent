package com.emily.infrastructure.rpc.client.channel;

import com.emily.infrastructure.rpc.client.handler.BaseClientHandler;
import com.emily.infrastructure.rpc.core.decoder.IRpcDecoder;
import com.emily.infrastructure.rpc.core.encoder.IRpcEncoder;
import com.emily.infrastructure.rpc.core.entity.message.IRTail;
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
 * @description: 解决粘包拆包处理器问题方法，参考：https://blog.csdn.net/u011035407/article/details/80454511
 * @author: Emily
 * @create: 2021/09/22
 */
public class IRpcClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    private BaseClientHandler baseClientHandler;

    public IRpcClientChannelInitializer(BaseClientHandler baseClientHandler) {
        this.baseClientHandler = baseClientHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //自定义分隔符
        ByteBuf delimiter  = Unpooled.copiedBuffer(IRTail.TAIL);
        //分隔符解码器
        pipeline.addFirst(new DelimiterBasedFrameDecoder(8192, delimiter));
        //自定义编码器
        pipeline.addLast(new IRpcEncoder());
        //自定义解码器
        pipeline.addLast(new IRpcDecoder());
        //自定义handler处理
        pipeline.addLast(baseClientHandler);
        //空闲状态处理器，参数说明：读时间空闲时间，0禁用时间|写事件空闲时间，0则禁用|读或写空闲时间，0则禁用
        pipeline.addLast(new IdleStateHandler(20, 0, 0, TimeUnit.SECONDS));
    }
}
