package com.emily.infrastructure.rpc.core.client.handler;

import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.rpc.core.protocol.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: spring-parent
 * @description: 由于需要在 handler 中发送消息给服务端，并且将服务端返回的消息读取后返回给消费者,所以实现了 Callable 接口，这样可以运行有返回值的线程
 * @author: Emily
 * @create: 2021/09/17
 */
public class RpcClientChannelHandler extends SimpleChannelInboundHandler implements Callable {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientChannelHandler.class);
    /**
     * 传递数据的类
     */
    private RpcRequest invokerProtocol;
    /**
     * 上下文
     */
    private ChannelHandlerContext context;
    /**
     * 服务端返回的结果
     */
    private Object result;


    public void setInvokerProtocol(RpcRequest invokerProtocol) {
        this.invokerProtocol = invokerProtocol;
    }

    /**
     * 实现channelActive  客户端和服务器连接时,该方法就自动执行
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.context = ctx;
    }

    /**
     * 实现channelRead 当我们读到服务器数据,该方法自动执行
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected synchronized void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            logger.info("收到服务端发送的消息 " + msg);
            result = msg;
        } catch (Exception e) {
            logger.error(PrintExceptionInfo.printErrorInfo(e));
        } finally {
            notify();
        }
    }

    /**
     * 将客户端的数写到服务器
     * @return
     * @throws Exception
     */
    @Override
    public synchronized Object call() throws Exception {
        try {
            final String s = JSONUtils.toJSONString(invokerProtocol);
            context.writeAndFlush(s);
            logger.info("RPC请求数据：{}  ", s);
        } catch (Exception e) {
            logger.error(PrintExceptionInfo.printErrorInfo(e));
        } finally {
            wait();
        }
        return result;
    }

    /**
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(PrintExceptionInfo.printErrorInfo(cause));
    }
}
