package com.yaomy.control.netty.messagepack.coder;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.jackson.dataformat.MessagePackFactory;

/**
 * @Description: msgpack编码器
 * @ProjectName: spring-parent
 * @Author: 姚明洋
 * @Date: 2020/1/21 16:09
 * @Version: 1.0
 */
public class MsgpackEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        out.writeBytes(objectMapper.writeValueAsBytes(msg));
    }
}
