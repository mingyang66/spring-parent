package com.emily.infrastructure.rpc.core.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @program: spring-parent
 * @description: 解码器
 * @author: Emily
 * @create: 2021/09/17
 */
public class MyMessageDecoder extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        //先读取要接收的字节长度
        final int len = byteBuf.readInt();
        final byte[] bytes = new byte[len];
        //再根据长度读取真正的字节数组
        byteBuf.readBytes(bytes);
        list.add(new String(bytes));
    }
}
