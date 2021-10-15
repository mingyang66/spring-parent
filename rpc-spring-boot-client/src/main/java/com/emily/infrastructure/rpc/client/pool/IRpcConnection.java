package com.emily.infrastructure.rpc.client.pool;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BasicException;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.rpc.client.IRpcClientProperties;
import com.emily.infrastructure.rpc.client.channel.IRpcClientChannelInitializer;
import com.emily.infrastructure.rpc.client.handler.BaseClientHandler;
import com.emily.infrastructure.rpc.client.handler.IRpcClientChannelHandler;
import com.emily.infrastructure.rpc.core.entity.message.IRMessage;
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
public class IRpcConnection extends AbstractConnection<Channel> {

    private static final Logger logger = LoggerFactory.getLogger(IRpcConnection.class);
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

    private IRpcClientProperties properties;

    public IRpcConnection(IRpcClientProperties properties) {
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
            handler = new IRpcClientChannelHandler();
            //加入自己的处理器
            BOOTSTRAP.handler(new IRpcClientChannelInitializer(handler));
            //连接服务器
            ChannelFuture channelFuture = BOOTSTRAP.connect(properties.getHost(), properties.getPort()).sync();
            channelFuture.addListener(listener -> {
                if (listener.isSuccess()) {
                    logger.info("connect success...");
                } else {
                    logger.info("RPC客户端重连接...");
                }
            });
            //获取channel
            Channel channel = channelFuture.channel();
            //将通道赋值给连接对象
            this.setConnection(channel);
            //判定通道是否可用
            if (this.isAvailable()) {
                return true;
            }
            return false;
        } catch (InterruptedException e) {
            logger.error(PrintExceptionInfo.printErrorInfo(e));
            throw new BasicException(AppHttpStatus.EXCEPTION.getStatus(), "创建连接失败");
        }
    }

    /**
     * 发送请求
     *
     * @param message
     */
    @Override
    public Object sendRequest(IRMessage message) {
        logger.info("RPC请求数据：{}  ", JSONUtils.toJSONString(message));
        try {
            synchronized (handler.object) {
                this.getConnection().writeAndFlush(message);
                handler.object.wait(5000);
            }
            return handler.response;
        } catch (Exception exception) {
            throw new BasicException(AppHttpStatus.EXCEPTION.getStatus(), PrintExceptionInfo.printErrorInfo(exception));
        }
    }

    /**
     * Socket连接是否可用
     *
     * @return
     */
    @Override
    public boolean isAvailable() {
        return null != this.getConnection() && this.getConnection().isActive() && this.getConnection().isWritable();
    }

    /**
     * 关闭连接通道
     */
    @Override
    public void close() {
        this.getConnection().close();
    }
}
