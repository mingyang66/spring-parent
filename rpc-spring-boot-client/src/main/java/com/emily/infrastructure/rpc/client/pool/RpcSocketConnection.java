package com.emily.infrastructure.rpc.client.pool;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BusinessException;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.rpc.client.RpcClientProperties;
import com.emily.infrastructure.rpc.client.channel.RpcClientChannelInitializer;
import com.emily.infrastructure.rpc.client.handler.BaseClientHandler;
import com.emily.infrastructure.rpc.client.handler.RpcClientChannelHandler;
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
public class RpcSocketConnection extends RpcConnection<Channel> {

    private static final Logger logger = LoggerFactory.getLogger(RpcSocketConnection.class);
    /**
     * 线程工作组
     */
    private static final NioEventLoopGroup GROUP = new NioEventLoopGroup();
    /**
     * 创建客户端的启动对象 bootstrap ，不是 serverBootStrap
     */
    private static final Bootstrap BOOTSTRAP = new Bootstrap();
    /**
     *
     */
    private BaseClientHandler handler;

    private RpcClientProperties properties;

    public RpcSocketConnection(RpcClientProperties properties) {
        this.properties = properties;
    }

    static {
        //设置线程组
        BOOTSTRAP.group(GROUP);
        //初始化通道
        BOOTSTRAP.channel(NioSocketChannel.class);
    }

    @Override
    public boolean connect() {
        try {
            handler = new RpcClientChannelHandler();
            //加入自己的处理器
            BOOTSTRAP.handler(new RpcClientChannelInitializer(handler));
            //连接服务器
            ChannelFuture channelFuture = BOOTSTRAP.connect(properties.getHost(), properties.getPort()).sync();
            channelFuture.addListener(listener -> {
                if (listener.isSuccess()) {
                    logger.info("RPC客户端连接成功...");
                } else {
                    logger.info("RPC客户端重连接...");
                    connect();
                }
            });
            //获取channel
            Channel channel = channelFuture.channel();
            //将通道赋值给连接对象
            this.connection = channel;
            if (this.isAvailable()) {
                return true;
            }
            return false;
        } catch (InterruptedException e) {
            logger.error(PrintExceptionInfo.printErrorInfo(e));
            throw new BusinessException(AppHttpStatus.EXCEPTION.getStatus(), "创建连接失败");
        }
    }

    /**
     * 发送请求
     *
     * @param request
     */
    @Override
    public RpcResponse call(RpcRequest request) {
        logger.info("RPC请求数据：{}  ", JSONUtils.toJSONString(request));
        try {
            synchronized (handler.object) {
                this.connection.writeAndFlush(request);
                handler.object.wait(5000);
            }
            return handler.response;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new RpcResponse("12", "error");
    }

    /**
     * Socket连接是否可用
     *
     * @return
     */
    @Override
    public boolean isAvailable() {
        return null != this.connection && this.connection.isActive() && this.connection.isWritable();
    }

    /**
     * 关闭连接通道
     */
    @Override
    public void close() {
        this.connection.close();
    }
}
