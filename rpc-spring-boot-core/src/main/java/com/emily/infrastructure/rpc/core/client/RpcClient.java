package com.emily.infrastructure.rpc.core.client;

import com.emily.infrastructure.rpc.core.client.channel.RpcClientChannelInitializer;
import com.emily.infrastructure.rpc.core.client.handler.RpcProxyHandler;
import com.emily.infrastructure.rpc.core.client.proxy.RpcMethodProxy;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;

/**
 * @program: spring-parent
 * @description: 创建Netty客户端及自定义处理器
 * @author: Emily
 * @create: 2021/09/17
 */
public class RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private RpcProxyHandler rpcProxyHandler;
    private String host;
    private int port;

    public RpcClient(RpcProxyHandler rpcProxyHandler, String host, int port) {
        this.rpcProxyHandler = rpcProxyHandler;
        this.host = host;
        this.port = port;
    }

    /**
     * 启动netty客户端
     *
     */
    public void start() {
        //客户端需要一个事件循环组就可以
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        try {
            //创建客户端的启动对象 bootstrap ，不是 serverBootStrap
            Bootstrap bootstrap = new Bootstrap();
            //设置相关参数,设置线程组
            bootstrap.group(group)
                    //设置客户端通道的实现数 （反射）
                    .channel(NioSocketChannel.class)
                    //加入自己的处理器
                    .handler(new RpcClientChannelInitializer(rpcProxyHandler));
            logger.info("客户端 ready is ok..");
            //连接服务器
            final ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            //对关闭通道进行监听
//            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
//            group.shutdownGracefully();
        }
    }
}
