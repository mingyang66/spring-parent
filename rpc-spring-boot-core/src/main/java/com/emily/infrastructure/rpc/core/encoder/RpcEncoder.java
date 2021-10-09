package com.emily.infrastructure.rpc.core.encoder;

import com.emily.infrastructure.common.utils.json.JSONUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @program: spring-parent
 * @description: Rpc编码器
 * @author: Emily
 * @create: 2021/09/23
 */
public class RpcEncoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object object, ByteBuf byteBuf) throws Exception {
        if (object == null) {
            return;
        }
        byte[] bytes = JSONUtils.toByteArray(object);
        //写入编码数据长度
        byteBuf.writeInt(bytes.length);
        //写入编码数据字节流
        byteBuf.writeBytes(bytes);
        //写入编码数据结束的行尾标识
        byteBuf.writeBytes(new byte[]{'\r', '\n'});
    }
}
