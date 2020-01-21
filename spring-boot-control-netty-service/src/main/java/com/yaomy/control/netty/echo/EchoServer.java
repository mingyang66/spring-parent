package com.yaomy.control.netty.echo;

import com.yaomy.control.netty.echo.handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Author: 姚明洋
 * @Date: 2020/1/21 13:50
 * @Version: 1.0
 */
@SuppressWarnings("all")
public class EchoServer {

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
                        //分隔符缓冲对象
                        ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                        //分隔符半包解码器DelimiterBasedFrameDecoder，第一个参数1024表示单条消息的最大长度，当达到该长度后仍然没有查到到分隔符，
                        //就抛出TooLongFrameException异常，防止异常码流缺失导致的内存溢出，这是Netty解码器的可靠性保护，第二个参数就是分隔符缓冲对象
                        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                        //固定长度编码器
                        //ch.pipeline().addLast(new FixedLengthFrameDecoder(20));
                        //解码器StringDecoder的功能非常简单，将接收到的对象转换为字符串
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new EchoServerHandler());
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
        new EchoServer().bind(port);
    }
}
