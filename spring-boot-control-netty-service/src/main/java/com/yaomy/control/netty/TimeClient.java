package com.yaomy.control.netty;

import com.yaomy.control.netty.handler.TimeClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @Description: 客户端Client
 * @ProjectName: spring-parent
 * @Author: 姚明洋
 * @Date: 2020/1/21 10:30
 * @Version: 1.0
 */
public class TimeClient {

    public void connect(int port, String host){
        //配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new TimeClientHandler());
                    }
                });
        try {
            //发起异步连接操作
            ChannelFuture f = b.connect(host, port).sync();
            //等待客户端链路关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8080;
        if(args != null && args.length > 0){
            port = Integer.valueOf(args[0]);
        }
        new TimeClient().connect(port, "127.0.0.1");
    }
}
