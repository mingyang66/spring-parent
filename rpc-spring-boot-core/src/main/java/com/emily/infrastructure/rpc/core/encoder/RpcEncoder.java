package com.emily.infrastructure.rpc.core.encoder;

import com.emily.infrastructure.rpc.core.entity.message.RMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @program: spring-parent
 * @description: Rpc编码器
 * @author: Emily
 * @create: 2021/09/23
 */
public class RpcEncoder extends MessageToByteEncoder<RMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RMessage message, ByteBuf byteBuf) throws Exception {
        if (message == null) {
            return;
        }
        //写入head连接超时时间
        byteBuf.writeInt(message.getHead().getKeepAlive());
        //写入编码数据长度
        byteBuf.writeInt(message.getBody().getLen());
        //写入编码数据字节流
        byteBuf.writeBytes(message.getBody().getData());
        //写入编码数据结束的行尾标识
        byteBuf.writeBytes(message.getTail().getTail());
    }
}
