package com.yaomy.control.zeromq.reqreply.client;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * @Description: 请求应答模式--client
 * @Version: 1.0
 */
@SuppressWarnings("all")
public class RequestClient {

    public static void main(String[] args) {
        /**
         * ZContext提供一种高级的ZeroMQ上下文管理类，它管理上下文中打开的SOCKET套接字，并在终止上下文之前自动关闭这些SOCKET套接字
         * 它提供一种在SOCKET套接字上设置延时超时的简单方法，并未I/O线程数配置上线文；设置进程的信号（中断）处理。
         * 默认构造函数分配给此上下文的IO线程数是1（ioThreads）
         */
        ZContext context = new ZContext(1);
        // Socket to talk to server
        System.out.println("Connecting to hello world server…");
        /**
         * 在此ZContext中创建新的托管SOCKET套接字，指定创建的套接字类型是客户端（REQ）
         */
        ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
        /**
         * 连接到远程应用程序
         * @param addr 将要连接的端点
         */
        socket.connect("tcp://127.0.0.1:5554");
        /**
         * 设置SOCKET套接字发送时的超时时间，
         * 如果为0，则send将立即返回，如果无法发送消息，则返回false
         * 如果是-1，则它将阻塞，直到发送消息为止
         * 对于其他的值，它将尝试在返回false和EAGAIN之前的指端时间发送消息
         * 单位：毫秒（milliseconds）
         * 默认值：-1
         */
        socket.setSendTimeOut(10000);
        /**
         * 设置SOCKET套接字接收的超时时间
         * 如果为0，则recv将立即返回，如果没有要接收的消息，则返回null
         * 如果为-1，它将阻塞，直到接收到消息为止
         * 对于其他的值，它将等待在返回null和EAGAIN错误之前接收消息
         * 单位：毫秒（milliseconds）
         * 默认：-1
         */
        socket.setReceiveTimeOut(-1);
        /**
         * 设置通过TCP或者IPC传输的用户名
         */
        //socket.setPlainUsername("user");
        /**
         * 设置通过TCP或者IPC传输的密码
         */
        //socket.setPlainPassword("123");
        /**
         * 要成为曲线（CURVE）客户端，要在客户端设置ZMQ_CURVE_PUBLICKEY {@link setCurveServerKey}设置和服务端{@link setCurveSecretKey}设置的私钥对应的公钥
         * 参考：http://api.zeromq.org/4-2:zmq-curve
         */
        socket.setCurveServerKey("s".getBytes());
        System.out.println("send_time_out:"+socket.getSendTimeOut()+", recv_time_out:"+socket.getReceiveTimeOut());;
        for (int i=0; i<100; i++) {
             String request = "Hello";
             System.out.println("Sending Hello " + i);
            /**
             * 发送消息到指定的标记
             * @param data 消息
             * @param flags 发送消息的标记
             */
            socket.send(request.getBytes(), 0);
            /**
             * 接收消息，如果没有接收到消息会一直阻塞等待，直到超时返回null
             * @param flags 接收消息的标记
             */
            byte[] reply = socket.recv(0);
            System.out.println("Received " + new String(reply) + " " + i);
        }
        /**
         * 销毁ZContext上下文中的SOCKET套接字
         */
        socket.close();

    }

}
