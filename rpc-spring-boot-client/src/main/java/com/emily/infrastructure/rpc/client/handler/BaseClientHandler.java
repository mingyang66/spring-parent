package com.emily.infrastructure.rpc.client.handler;

import com.emily.infrastructure.rpc.core.protocol.RpcBody;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: spring-parent
 * @description: 客户端基础handler
 * @author: Emily
 * @create: 2021/09/23
 */
public class BaseClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(BaseClientHandler.class);

    public final Object object = new Object();
    /**
     * 服务端返回的结果
     */
    public RpcBody response;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端信息：{}", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("服务断开连接：{}", ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

}
