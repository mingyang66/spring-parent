package com.yaomy.control.netty.time;

import com.yaomy.control.netty.time.handler.TimeServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @Description: 服务端Server
 * @ProjectName: spring-parent
 * @Author: 姚明洋
 * @Date: 2020/1/21 9:51
 * @Version: 1.0
 */
public class TimeServer {

    public void bind(int port) {
        //配置服务端NIO线程组,一个用于服务端接受客户端的连接，另一个用于进行SocketChannel的网络读写
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        //启动NIO服务端的的辅助类
        ServerBootstrap b = new ServerBootstrap();
        b.group(parentGroup, childGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 1024)
            //绑定I/O事件的处理类
            .childHandler(new ChildChannelHandler());
        try {
            //绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();
            //等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅退出，释放线程池资源
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            //半包解码器LineBasedFrameDecoder的工作原理是它依次遍历ByteBuf中的可读字节，判断是否有\n或者\r\n,如果有就以此位置结束，
            // 从可读位置到结束区间的字节就组成了一行。它是以换行符为结束标志的解码器
            //支持携带结束符或者不携带结束符两种方式，同时支持配置单行的最大长度。如果连续读取到最大长度后仍然没有发现换行符，就会抛异常，
            //同时忽略调之前读到的异常码
            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
            //解码器StringDecoder的功能非常简单，就是将接收到的对象转换成字符串
            socketChannel.pipeline().addLast(new StringDecoder());
            socketChannel.pipeline().addLast(new TimeServerHandler());
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        if(args != null && args.length > 0){
            port = Integer.valueOf(args[0]);
        }

        new TimeServer().bind(port);
    }
}
