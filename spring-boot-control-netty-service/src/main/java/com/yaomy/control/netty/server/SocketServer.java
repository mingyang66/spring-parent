package com.yaomy.control.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * @Description: Description
 * @Version: 1.0
 */
public class SocketServer {
    public static void main(String[] args) {
        try{
            //负责接收客户端连接
            NioEventLoopGroup bossGroup = new NioEventLoopGroup();

            //负责处理连接
            NioEventLoopGroup wokerGroup = new NioEventLoopGroup();

                try{

                ServerBootstrap bootstrap = new ServerBootstrap();

                bootstrap.group(bossGroup,wokerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();

                                //数据分包，组包，粘包
                                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                                pipeline.addLast(new LengthFieldPrepender(4));

                                pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                                pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));

                                pipeline.addLast(new ServerHandler());
                            }
                        });

                //绑定端口号
                ChannelFuture channelFuture = bootstrap.bind(9999).sync();
                channelFuture.channel().closeFuture().sync();

            } finally {
                bossGroup.shutdownGracefully();
                wokerGroup.shutdownGracefully();
            }
        }catch (InterruptedException e){

        }

    }
}
