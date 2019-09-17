package com.yaomy.control.zeromq.pubsub.server;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @Description: Description
 * @Version: 1.0
 */
public class ZeroMQServer {
    private String endPoint;

    public ZeroMQServer(String endPoint) {
        this.endPoint = endPoint;
    }

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
        ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
        /**
         * 绑定到网络端口，开始监听新的连接
         */
        publisher.bind(endPoint);

        System.out.println("send_time_out:" + publisher.getSendTimeOut() + ", recv_time_out:" + publisher.getReceiveTimeOut());
        ;
        System.out.println("-----------------start-------------------------");
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }
            /**
             * ZMQ_SNDMORE 指定发送的消息是一个多部分组成消息，接下来是更多的消息；ZMQ消息由一个或者多个组成，ZMQ确保消息的原子性传递，对等方要么接收消息的所有部分，
             * 要么根本不接收任何消息部分，除可用内存外，消息部分的总数不受限制。
             * send方法如果发送成功将会返回true，否则将会返回false
             * 参考：http://api.zeromq.org/4-1:zmq-send
             */
            publisher.send("A 哈哈".getBytes(), ZMQ.SNDMORE);
            publisher.send("This is A 测试");
            publisher.send("This is A");
            publisher.send("A This is A");
            publisher.send("B".getBytes());
            publisher.send("This is B 测试".getBytes());
        }
    }
}
