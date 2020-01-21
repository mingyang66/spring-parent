package com.yaomy.control.netty;

import com.yaomy.control.netty.handler.TimeServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

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
