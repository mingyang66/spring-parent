package com.emily.infrastructure.rpc.core.encoder;

import com.emily.infrastructure.common.utils.json.JSONUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @program: spring-parent
 * @description: Rpc编码
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
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
        byteBuf.writeBytes(new byte[]{'\r', '\n'});
    }
}
