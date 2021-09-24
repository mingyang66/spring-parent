package com.emily.infrastructure.rpc.client.pool;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BusinessException;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.rpc.client.RpcClientProperties;
import com.emily.infrastructure.rpc.client.channel.RpcClientChannelInitializer;
import com.emily.infrastructure.rpc.client.handler.BaseClientHandler;
import com.emily.infrastructure.rpc.client.handler.RpcClientChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
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
public class SocketConn extends Conn<Channel> {

    private static final Logger logger = LoggerFactory.getLogger(SocketConn.class);
    /**
     * 线程工作组
     */
    private static final NioEventLoopGroup GROUP = new NioEventLoopGroup();
    /**
     * 创建客户端的启动对象 bootstrap ，不是 serverBootStrap
     */
    private static final Bootstrap BOOTSTRAP = new Bootstrap();

    private RpcClientProperties properties;

    public SocketConn(RpcClientProperties properties) {
        this.properties = properties;
    }

    static {
        //设置线程组
        BOOTSTRAP.group(GROUP);
        //初始化通道
        BOOTSTRAP.channel(NioSocketChannel.class);
    }

    public boolean createConn() {
        try {
            BaseClientHandler handler = new RpcClientChannelHandler();
            //加入自己的处理器
            BOOTSTRAP.handler(new RpcClientChannelInitializer(handler));
            logger.info("客户端连接成功...");
            //连接服务器
            Channel channel = BOOTSTRAP.connect(properties.getHost(), properties.getPort()).sync().channel();
            ClientResource.handlerMap.put(channel.id().asLongText(), handler);
            this.conn = channel;
            if (canUse()) {
                return true;
            }
            return false;
        } catch (InterruptedException e) {
            logger.error(PrintExceptionInfo.printErrorInfo(e));
            throw new BusinessException(AppHttpStatus.EXCEPTION.getStatus(), "创建连接失败");
        }
    }

    /**
     * Socket连接是否可用
     *
     * @return
     */
    public boolean canUse() {
        return null != this.conn && this.conn.isActive() && this.conn.isWritable();
    }

    public void close() {
        this.conn.close();
    }
}
