package com.yaomy.control.netty.time.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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

    private int counter;
    private final byte[] req;

    public TimeClientHandler() {
       req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
       ByteBuf message = null;
       for(int i=0; i<100; i++){
           message = Unpooled.buffer(req.length);
           message.writeBytes(req);
           ctx.writeAndFlush(message);
       }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        System.out.println("Now is : "+ body + " ; the counter is : " + ++counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Unexpected exception from downstream : "+ cause.getMessage());
        ctx.close();
    }
}
