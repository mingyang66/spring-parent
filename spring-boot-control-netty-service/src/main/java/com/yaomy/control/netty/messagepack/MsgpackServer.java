package com.yaomy.control.netty.messagepack;

import com.yaomy.control.netty.messagepack.coder.MsgpackDecoder;
import com.yaomy.control.netty.messagepack.coder.MsgpackEncoder;
import com.yaomy.control.netty.messagepack.handler.MsgpackServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Date: 2020/1/21 13:50
 * @Version: 1.0
 */
@SuppressWarnings("all")
public class MsgpackServer {

    public void bind(int port){
        //配置服务端NIO线程组
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //解码器
                        ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
                        ch.pipeline().addLast("msgpack decoder", new MsgpackDecoder());
                        //编码器
                        ch.pipeline().addLast("frameEncode", new LengthFieldPrepender(2));
                        ch.pipeline().addLast("msgpack encoder", new MsgpackEncoder());
                        ch.pipeline().addLast(new MsgpackServerHandler());
                    }
                });
        try {
            //绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();

            //等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        if(args != null && args.length > 0){
            port = Integer.valueOf(port);
        }
        new MsgpackServer().bind(port);
    }
}
