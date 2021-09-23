package com.emily.infrastructure.rpc.core.encoder;

import com.emily.infrastructure.common.utils.json.JSONUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @program: spring-parent
 * @description:
 * @author: 姚明洋
 * @create: 2021/09/23
 */
public class RpcEncoder extends MessageToByteEncoder<Object> {
    private Class<?> clazz;

    public RpcEncoder(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object object, ByteBuf byteBuf) throws Exception {
        if (object == null) {
            return;
        }
        if (clazz != null) {
            byte[] bytes = JSONUtils.toByteArray(object);
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
        }
    }
}
