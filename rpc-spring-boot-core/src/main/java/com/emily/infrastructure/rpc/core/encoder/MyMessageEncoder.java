package com.emily.infrastructure.rpc.core.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @program: spring-parent
 * @description: 编码器, 解决粘包拆包问题
 * @author: Emily
 * @create: 2021/09/17
 */
public class MyMessageEncoder extends MessageToByteEncoder<String> {
    @Override
    protected void encode(ChannelHandlerContext ctx, String s, ByteBuf byteBuf) throws Exception {
        //先发送内容长度
        byteBuf.writeInt(s.getBytes().length);
        //发送具体的内容
        byteBuf.writeBytes(s.getBytes());
    }
}
