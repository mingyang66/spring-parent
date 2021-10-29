package com.emily.infrastructure.rpc.core.encoder;

import com.emily.infrastructure.rpc.core.entity.message.IRpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @program: spring-parent
 * @description: Rpc编码器 protobuf[https://github.com/protocolbuffers/protobuf/releases]
 * @author: Emily
 * @create: 2021/09/23
 */
public class IRpcEncoder extends MessageToByteEncoder<IRpcMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, IRpcMessage message, ByteBuf byteBuf) throws Exception {
        if (message == null) {
            return;
        }
        //写入head包类型
        byteBuf.writeInt(message.getHead().getPackageType());
        //写入事务唯一编号长度
        byteBuf.writeInt(message.getHead().getTraceId().length);
        //写入事务唯一标识字节流
        byteBuf.writeBytes(message.getHead().getTraceId());
        //写入编码数据长度
        byteBuf.writeInt(message.getBody().getLen());
        //写入编码数据字节流
        byteBuf.writeBytes(message.getBody().getData());
        //写入编码数据结束的行尾标识
        byteBuf.writeBytes(message.getTail().getTail());
    }
}
