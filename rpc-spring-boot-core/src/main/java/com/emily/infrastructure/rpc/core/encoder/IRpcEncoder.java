package com.emily.infrastructure.rpc.core.encoder;

import com.emily.infrastructure.rpc.core.message.IRpcMessage;
import com.emily.infrastructure.rpc.core.message.IRpcTail;
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
        //写入包类型
        byteBuf.writeByte(message.getPackageType());
        //请求|响应体长度
        byteBuf.writeInt(message.getLen());
        //写入编码数据字节流
        byteBuf.writeBytes(message.getBody());
        //写入编码数据结束的行尾标识
        byteBuf.writeBytes(IRpcTail.TAIL);
    }
}
