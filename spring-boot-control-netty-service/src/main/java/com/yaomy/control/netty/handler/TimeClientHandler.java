package com.yaomy.control.netty.handler;

import com.yaomy.control.common.control.utils.CharsetUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Author: 姚明洋
 * @Date: 2020/1/21 10:48
 * @Version: 1.0
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    private final ByteBuf firstMessage;

    public TimeClientHandler() {
       byte[] req = "QUERY TIME ORDER".getBytes();
       firstMessage = Unpooled.buffer(req.length);
       firstMessage.writeBytes(req);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
       ctx.writeAndFlush(firstMessage);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, CharsetUtils.UTF8);
        System.out.println("Now is : "+ body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Unexpected exception from downstream : "+ cause.getMessage());
        ctx.close();
    }
}
