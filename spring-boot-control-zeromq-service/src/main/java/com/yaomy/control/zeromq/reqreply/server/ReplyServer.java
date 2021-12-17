package com.yaomy.control.zeromq.reqreply.server;


import com.emily.infrastructure.logback.factory.LoggerFactory;
import org.slf4j.Logger;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.concurrent.TimeUnit;

/**
 * @Description: 请求应答模式--server
 * @Version: 1.0
 */
@SuppressWarnings("all")
public class ReplyServer {
    private static final Logger logger = LoggerFactory.getLogger(ReplyServer.class);
    /**
     * 端点
     */
    private String endpoint;

    public ReplyServer(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * 启动服务端
     */
    public void start() {
        /**
         * ZContext提供一种高级的ZeroMQ上下文管理类，它管理上下文中打开的SOCKET套接字，并在终止上下文之前自动关闭这些SOCKET套接字
         * 它提供一种在SOCKET套接字上设置延时超时的简单方法，并未I/O线程数配置上线文；设置进程的信号（中断）处理。
         * 默认构造函数分配给此上下文的IO线程数是1（ioThreads）
         */
        ZContext context = new ZContext(1);
        /**
         * 在此ZContext中创建新的托管SOCKET套接字，指定创建的套接字类型是服务端（REP）
         */
        ZMQ.Socket socket = context.createSocket(SocketType.REP);
        /**
         * 绑定到网络端口，开始监听新的连接
         */
        socket.bind(endpoint);
        /**
         * 设置SOCKET套接字发送时的超时时间，
         * 如果为0，则send将立即返回，如果无法发送消息，则返回false
         * 如果是-1，则它将阻塞，直到发送消息为止
         * 对于其他的值，它将尝试在返回false和EAGAIN之前的指端时间发送消息
         * 单位：毫秒（milliseconds）
         * 默认值：-1
         */
        socket.setSendTimeOut(-1);
        /**
         * 设置SOCKET套接字接收的超时时间
         * 如果为0，则recv将立即返回，如果没有要接收的消息，则返回null
         * 如果为-1，它将阻塞，直到接收到消息为止
         * 对于其他的值，它将等待在返回null和EAGAIN错误之前接收消息
         * 单位：毫秒（milliseconds）
         * 默认：-1
         */
        socket.setReceiveTimeOut(-1);

        logger.info("ReplyServer服务端启动成功...");
        while (true) {
            /**
             * 接收消息，如果没有接收到消息会一直阻塞等待，直到超时返回null
             * @param flags 接收消息的标记
             */
            byte[] reply = socket.recv();
            // Print the message
            logger.info("Received: [" + new String(reply, ZMQ.CHARSET) + "]");
            /**
             * 发送消息到指定的标记
             * @param data 消息
             * @param flags 发送消息的标记
             */
            socket.send("ZEROMQ SERVER RESPONSE...".getBytes(ZMQ.CHARSET), 0);
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {

            }
        }

    }
}
