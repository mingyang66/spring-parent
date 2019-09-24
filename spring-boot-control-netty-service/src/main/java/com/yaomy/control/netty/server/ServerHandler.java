package com.yaomy.control.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.UUID;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Author: 姚明洋
 * @Date: 2019/9/23 14:16
 * @Version: 1.0
 */
public class ServerHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws   Exception {
        //接收到的数据
        System.out.println(ctx.channel().remoteAddress()+" , "+msg);

        //返回给客户端的数据
        ctx.channel().writeAndFlush("server: "+ UUID.randomUUID());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws  Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
