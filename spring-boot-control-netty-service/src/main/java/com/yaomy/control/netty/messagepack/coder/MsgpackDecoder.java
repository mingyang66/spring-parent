package com.yaomy.control.netty.messagepack.coder;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.util.List;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Author: 姚明洋
 * @Date: 2020/1/21 16:52
 * @Version: 1.0
 */
public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        out.add(objectMapper.readValue(bytes, Object.class));
    }
}
