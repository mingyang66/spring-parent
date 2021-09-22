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
    /**
     * 使用锁将 channelRead和 call 函数同步
     */
    private Lock lock = new ReentrantLock();
    /**
     * 精准唤醒 call中的等待
     */
    private Condition condition = lock.newCondition();


    public void setInvokerProtocol(RpcRequest invokerProtocol) {
        this.invokerProtocol = invokerProtocol;
    }

    /**
     * 通道连接时，就将上下文保存下来，因为这样其他函数也可以用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.context = ctx;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelInactive 被调用。。。");
    }

    /**
     * 当服务端返回消息时，将消息复制到类变量中，然后唤醒正在等待结果的线程，返回结果
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            lock.lock();
            logger.info(ctx.channel().hashCode() + "");
            logger.info("收到服务端发送的消息 " + msg);
            result = msg;

        } catch (Exception e) {
            logger.error(PrintExceptionInfo.printErrorInfo(e));
        } finally {
            //唤醒等待的线程
            condition.signal();
            lock.unlock();

        }
    }

    /**
     * 这里面发送数据到服务端，等待channelRead方法接收到返回的数据时，将数据返回给服务消费者
     *
     * @return
     * @throws Exception
     */
    @Override
    public Object call() throws Exception {
        try {
            lock.lock();
            final String s = JSONUtils.toJSONString(invokerProtocol);
            context.writeAndFlush(s);
            logger.info("RPC请求数据：{}  ", s);
        } catch (Exception e) {
            logger.error(PrintExceptionInfo.printErrorInfo(e));
        } finally {
            //向服务端发送消息后等待channelRead中接收到消息后唤醒
            condition.await();
            lock.unlock();
        }
        return result;
    }

    /**
     * 异常处理
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(PrintExceptionInfo.printErrorInfo(cause));
    }
}
