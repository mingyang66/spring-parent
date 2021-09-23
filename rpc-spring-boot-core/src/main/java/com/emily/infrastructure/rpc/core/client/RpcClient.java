package com.emily.infrastructure.rpc.core.client;

import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.rpc.core.client.channel.RpcClientChannelInitializer;
import com.emily.infrastructure.rpc.core.client.handler.RpcClientChannelHandler;
import com.emily.infrastructure.rpc.core.protocol.RpcRequest;
import com.emily.infrastructure.rpc.core.protocol.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: spring-parent
 * @description: 创建Netty客户端及自定义处理器
 * @author: Emily
 * @create: 2021/09/17
 */
public class RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private RpcClientChannelHandler rpcClientChannelHandler;
    private String host;
    private int port;
    private Channel channel;

    public RpcClient(RpcClientChannelHandler rpcClientChannelHandler, String host, int port) {
        this.rpcClientChannelHandler = rpcClientChannelHandler;
        this.host = host;
        this.port = port;
    }

    /**
     * 启动netty客户端
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
                    .handler(new RpcClientChannelInitializer(rpcClientChannelHandler));
            logger.info("客户端连接成功...");
            //连接服务器
            channel = bootstrap.connect(host, port).sync().channel();
        } catch (InterruptedException e) {
            logger.error(PrintExceptionInfo.printErrorInfo(e));
            group.shutdownGracefully();
        }
    }

    /**
     * 发送请求
     *
     * @param request
     * @throws InterruptedException
     */
    public RpcResponse sendRequest(RpcRequest request) throws InterruptedException {
        synchronized (rpcClientChannelHandler.object) {
            channel.writeAndFlush(request);
            rpcClientChannelHandler.object.wait(5000);
        }
        logger.info("RPC请求数据：{}  ", JSONUtils.toJSONString(request));
        return rpcClientChannelHandler.response;
    }
}
